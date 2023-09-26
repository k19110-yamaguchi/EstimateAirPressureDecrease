package com.example.estimateairpressuredecrease
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.estimateairpressuredecrease.room.dao.FeatureValueDao
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorDao
import com.example.estimateairpressuredecrease.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import javax.inject.Inject
import java.nio.file.Paths

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeDao: HomeDao,
    private val sensorDao: SensorDao,
    private val featureValueDao: FeatureValueDao,
) : ViewModel(){

    private val initDate: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)

    //
    var isHome: Boolean by mutableStateOf(true)

    // Home
    var isTrainingState by mutableStateOf(true)
    // テキストフィールドに入力された最小適正空気圧
    var editingMinProperPressure by mutableStateOf("")
    var minProperPressure by mutableStateOf(0)
    var inflatedDate: LocalDateTime by mutableStateOf(initDate)



    // Sensor
    // センシング中かどうか
    var isSensing: Boolean by mutableStateOf(false)
    var isRequiredData: Boolean by mutableStateOf(false)

    var startDate: LocalDateTime by mutableStateOf(initDate)
    var stopDate: LocalDateTime by mutableStateOf(initDate)
    var airPressure: Int by mutableStateOf(0)

    // Acc
    var xAccList: MutableList<Double> = mutableListOf()
    var yAccList: MutableList<Double> = mutableListOf()
    var zAccList: MutableList<Double> = mutableListOf()
    var accTime: Double by mutableStateOf(-1.0)
    var accTimeList: MutableList<Double> = mutableListOf()

    // Loc
    var latList: MutableList<Double> = mutableListOf()
    var lonList: MutableList<Double> = mutableListOf()
    var locTime: Double by mutableStateOf(-1.0)
    var locTimeList: MutableList<Double> = mutableListOf()

    // Gra
    var xGraList: MutableList<Double> = mutableListOf()
    var yGraList: MutableList<Double> = mutableListOf()
    var zGraList: MutableList<Double> = mutableListOf()
    var graTime: Double by mutableStateOf(-1.0)
    var graTimeList: MutableList<Double> = mutableListOf()

    // Bar
    var barList: MutableList<Double> = mutableListOf()
    var barTime: Double by mutableStateOf(-1.0)
    var barTimeList: MutableList<Double> = mutableListOf()

    // FeatureValue
    var accSd: Double by mutableStateOf(0.0)
    // 振幅スペクトル
    var ampSptList: MutableList<Double> = mutableListOf()


    // Homeのデータを取得
    val home = homeDao.getHomeData().distinctUntilChanged()

    // Accのデータ取得
    val sensorData = sensorDao.getSensorData().distinctUntilChanged()
    val accData = sensorDao.getAccData().distinctUntilChanged()
    val graData = sensorDao.getGraData().distinctUntilChanged()
    val locData = sensorDao.getLocData().distinctUntilChanged()
    val barData = sensorDao.getBarData().distinctUntilChanged()
    val featureValueData = featureValueDao.getFeatureValues().distinctUntilChanged()

    // python



    // 初めての起動かどうか確認
    suspend fun checkWhetherFirstTime() {
        // id:0 のhomeがnullだった場合
        if(homeDao.getHomeById(0) == null){
            // データベースを作成
            createHome()
        }
    }


    // 初期値を設定
    fun setHome(home: HomeData){
        isTrainingState = home.isTrainingState
        minProperPressure = home.minProperPressure
        inflatedDate = home.inflatedDate
    }

    // データベースを作成
    private fun createHome() {
        viewModelScope.launch {
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.createHomeDB(newHome)
        }
    }

    // データベースを更新
    fun updateHome() {
        viewModelScope.launch {
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }

    // 推定に必要なデータがあるか調べる
    fun checkRequiredData(){
        if(accTime != -1.0 &&
            graTime != -1.0 &&
            locTime != -1.0 &&
            barTime != -1.0){
            isRequiredData = true
        }
    }

    // 空気圧を入力したか
    fun checkAirPressure(){
        if(airPressure != 0){
            addData()
        }else{
            Log.d("checkAirPressure", "空気圧の入力が正しくありません")
        }
    }

    private fun addData() {
        addAcc()
        addGra()
        addLoc()
        addBar()
        addSensor()
        viewModelScope.launch {
            val newSensor = SensorData(startDate = startDate, stopDate = stopDate, airPressure = airPressure)
            sensorDao.insertSensorData(newSensor)
        }
        createFeatureValue()
        reset()
    }

    private fun addAcc() {
        viewModelScope.launch {
            val newAcc = AccData(xAccList = xAccList, yAccList = yAccList, zAccList = zAccList, timeList = accTimeList)
            sensorDao.insertAccData(newAcc)

        }
    }

    private fun addGra() {
        viewModelScope.launch {
            val newGra = GraData(xGraList = xGraList, yGraList = yGraList, zGraList = zGraList, timeList = graTimeList)
            sensorDao.insertGraData(newGra)
        }
    }

    private fun addLoc() {
        viewModelScope.launch {
            val newLoc = LocData(latList = latList, lonList = lonList, timeList = locTimeList)
            sensorDao.insertLocData(newLoc)
        }
    }

    private fun addBar() {
        viewModelScope.launch {
            val newBar = BarData(barList = barList, timeList = barTimeList)
            sensorDao.insertBarData(newBar)
        }
    }

    private fun addSensor() {
        viewModelScope.launch {
            val newSensor = SensorData(startDate = startDate, stopDate = stopDate, airPressure = airPressure)
            sensorDao.insertSensorData(newSensor)
        }
    }

    private fun addFeatureValue() {
        viewModelScope.launch {
            val newFeatureValue = FeatureValueData(accSd = accSd, ampSptList = ampSptList, airPressure = airPressure)
            featureValueDao.insertFeatureValues(newFeatureValue)

        }
    }

    private fun createFeatureValue() {

        // createAccCsv()
        val fileNameList = listOf("acc", "gra", "loc", "bar")

        var len = accTimeList.size
        val accData = mutableListOf<List<Double>>()
        for(i in 0 until len){
            accData.add(listOf(xAccList[i], yAccList[i], zAccList[i], accTimeList[i]))
        }

        len = graTimeList.size
        val graData = mutableListOf<List<Double>>()
        for(i in 0 until len){
            graData.add(listOf(xGraList[i], yGraList[i], zGraList[i], graTimeList[i]))
        }

        len = locTimeList.size
        val locData = mutableListOf<List<Double>>()
        for(i in 0 until len){
            locData.add(listOf(latList[i], lonList[i], locTimeList[i]))
        }

        len = barTimeList.size
        val barData = mutableListOf<List<Double>>()
        for(i in 0 until len){
            barData.add(listOf(barList[i], barTimeList[i]))
        }

        val dataList = listOf(accData, graData, locData, barData)

        val ravelList = listOf(
            listOf("x(m/s)", "y(m/s)", "z(m/s)", "t(s)"),
            listOf("x(m/s)", "y(m/s)", "z(m/s)", "t(s)"),
            listOf("lat", "lon", "t(s)"),
            listOf("bar(kPa)", "t(s)")
        )

        for(i in fileNameList.indices){
            val csvData = mutableListOf<List<String>>()
            csvData.add(ravelList[i])
            csvData.addAll(dataList[i].map { it.map { it.toString() } })

            val file = File(MainActivity.instance.getExternalFilesDir(null), startDate.toString() + "_" + fileNameList[i] + ".csv")
            val writer = BufferedWriter(FileWriter(file))

            for (row in csvData) {
                writer.write(row.joinToString(","))
                writer.newLine()
            }
            writer.close()

        }

        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.instance))
        }
        val py = Python.getInstance()
        val module = py.getModule("test") // スクリプト名
        val featureValueStr = module.callAttr("getFeatureValues", accData, graData, locData, barData, airPressure).toString()

        // 最初と最後の[]を取り除き、","で分割
        val featureValues = featureValueStr.substring(1, featureValueStr.length - 1).split(",").map { it.trim().toDouble() }

        accSd = featureValues[0]
        ampSptList = featureValues as MutableList<Double>
        ampSptList.removeAt(0)
        airPressure = 300

        Log.d("runPython:accSd", accSd.toString())
        Log.d("runPython:ampSptList", ampSptList.toString())
        addFeatureValue()

    }

    private fun reset() {
        startDate = initDate
        stopDate = initDate
        airPressure = 0

        xAccList = emptyList<Double>().toMutableList()
        yAccList = emptyList<Double>().toMutableList()
        zAccList = emptyList<Double>().toMutableList()
        accTime = -1.0
        accTimeList = emptyList<Double>().toMutableList()

        latList =  emptyList<Double>().toMutableList()
        lonList = emptyList<Double>().toMutableList()
        locTime = -1.0
        locTimeList = emptyList<Double>().toMutableList()

        // Gra
        xGraList =  emptyList<Double>().toMutableList()
        yGraList =  emptyList<Double>().toMutableList()
        zGraList =  emptyList<Double>().toMutableList()
        graTime = -1.0
        graTimeList =  emptyList<Double>().toMutableList()

        // Bar
        barList =  emptyList<Double>().toMutableList()
        barTime = -1.0
        barTimeList =  emptyList<Double>().toMutableList()

        // 特徴量
        accSd = 0.0
        // 振幅スペクトル
        ampSptList = emptyList<Double>().toMutableList()

        startDate = initDate
        stopDate = initDate

        isRequiredData = false

        airPressure = 0
    }

    fun runPython(){

        val currentDirPath = Paths.get("").toAbsolutePath()
        println("Current Directory Path: $currentDirPath")

        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.instance))
        }
        val py = Python.getInstance()
        val module = py.getModule("test") // スクリプト名
        val featureValueStr = module.callAttr("getFeatureValues", 0, 0, 0, 0, 300).toString()
        // 最初と最後の[]を取り除き、","で分割
        val featureValues = featureValueStr.substring(1, featureValueStr.length - 1).split(",").map { it.trim().toDouble() }

        accSd = featureValues[0]
        ampSptList = featureValues as MutableList<Double>
        ampSptList.removeAt(0)
        airPressure = 300

        Log.d("runPython:accSd", accSd.toString())
        Log.d("runPython:ampSptList", ampSptList.toString())


    }



}