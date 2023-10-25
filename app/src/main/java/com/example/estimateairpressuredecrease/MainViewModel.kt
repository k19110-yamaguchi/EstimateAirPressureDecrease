package com.example.estimateairpressuredecrease
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.estimateairpressuredecrease.room.dao.FeatureValueDao
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorDao
import com.example.estimateairpressuredecrease.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import java.nio.file.Paths


@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeDao: HomeDao,
    private val sensorDao: SensorDao,
    private val featureValueDao: FeatureValueDao,
) : ViewModel(){

    // 初期の日付
    private val initDate: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)

    // MainContent
    var isFirst by mutableStateOf(false)
    var isTrainingState by mutableStateOf(true)

    // InputAirPressure
    var editingAirPressure by mutableStateOf("")
    var errorInputAirPressure by mutableStateOf("")

    var errorData by mutableStateOf("")


    // テキストフィールドに入力された最小適正空気圧
    var editingMinProperPressure by mutableStateOf("")
    // 最小適正空気圧
    var minProperPressure by mutableStateOf(0)
    // 推定空気圧
    var estimatedAirPressure: Int by mutableStateOf(0)
    // 空気を注入した時期
    var inflatedDate: LocalDateTime by mutableStateOf(initDate)
    // 最小適正空気圧を入力しているか
    var isInputtingMinProperPressure by mutableStateOf(false)
    // 空気圧を入力している最中
    var isInputtingAirPressure by mutableStateOf(false)

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
    var fv : List<FeatureValueData> by mutableStateOf(emptyList())
    var accSd: Double by mutableStateOf(0.0)
    // 振幅スペクトル
    var ampSptList: MutableList<Double> = mutableListOf()

    var estimatedRange = 3

    // Homeのデータを取得
    val homeData = homeDao.getHomeData().distinctUntilChanged()

    //

    val sensorData = sensorDao.getSensorData().distinctUntilChanged()
    //
    val accData = sensorDao.getAccData().distinctUntilChanged()

    //
    val locData = sensorDao.getLocData().distinctUntilChanged()

    // のデータ取得
    val featureValueData = featureValueDao.getFeatureValues().distinctUntilChanged()

    // python

    // 初めての起動かどうか確認
    fun checkIsFirst(){
        viewModelScope.launch {
            // id:0 のhomeがnullだった場合
            if (homeDao.getHomeById(0) == null){
                isFirst = true
            }else{
                isFirst = false
            }
        }
    }

    // 入力された空気圧をデータベースに保存
    fun inputAirPressure(isFirst: Boolean, isProper: Boolean){
        errorInputAirPressure = ""
        try{
            // 最小適正空気圧入力時
            if(isProper){
                minProperPressure = editingAirPressure.toInt()
                // 初回起動時
                if(isFirst){
                    createHome()
                // 初回以外時
                }else{
                    updateHome()
                }
                isInputtingMinProperPressure = false
            // 学習状態・空気圧入力時
            }else{
                airPressure = editingAirPressure.toInt()
                addData()
                isInputtingAirPressure = false
            }
        }catch (e: NumberFormatException){
            errorInputAirPressure = "数字(整数)を入力してください"
        }

        editingAirPressure = ""

    }

    // 学習状態から推定状態に変更できるか調べる
    fun checkStatus(featureValueData: List<FeatureValueData>){
        val requiredFvSize = 2
        var outOfSize = 0
        var withinSize = 0
        // 適正外、適正内のデータの数を調べる
        for (fv in featureValueData) {
            if (fv.airPressure >= minProperPressure) {
                outOfSize += 1
            } else {
                withinSize += 1
            }
        }
        // 必要サイズ以上になった場合
        if(outOfSize >= requiredFvSize && withinSize >= requiredFvSize){
           createModel(featureValueData)
            isTrainingState = false
            updateHome()
        }
    }

    // 初期値を設定
    fun setHome(home: HomeData){
        isTrainingState = home.isTrainingState
        minProperPressure = home.minProperPressure
        estimatedAirPressure = home.estimatedAirPressure
        inflatedDate = home.inflatedDate
    }

    // データベースを作成
    private fun createHome() {
        viewModelScope.launch {
            val newHome = HomeData(isTrainingState = isTrainingState, estimatedAirPressure = estimatedAirPressure, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.createHomeDB(newHome)
        }
    }

    // データベースを更新
    private fun updateHome() {
        viewModelScope.launch {
            Log.d("updateHome", estimatedAirPressure.toString())
            val newHome = HomeData(isTrainingState = isTrainingState, estimatedAirPressure = estimatedAirPressure, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
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

    private fun createTestData(){
        latList = listOf(35.188842,35.188878, 35.188914, 35.188953, 35.188991, 35.189028, 35.189066, 35.189105, 35.189145, 35.189183) as MutableList<Double>
        lonList = listOf(137.105494,137.105483, 137.105470, 137.105459, 137.105450, 137.105440, 137.105429, 137.105416, 137.105404, 137.1053935) as MutableList<Double>
        locTimeList = listOf(1.44, 2.46, 3.44, 4.45, 5.45, 6.46, 7.45, 8.45, 9.44, 10.46) as MutableList<Double>
    }

    fun addData() {
        // createTestData()
        var successCreateFv = createFeatureValue()
        if(successCreateFv){
            // データベースに保存
            addAcc()
            addGra()
            addLoc()
            addBar()
            addSensor()

            // ファイルの作成
            val openCsv = OpenCsv()
            val newAcc = AccData(xAccList = xAccList, yAccList = yAccList, zAccList = zAccList, timeList = accTimeList)
            val newLoc = LocData(latList = latList, lonList = lonList, timeList = locTimeList)
            openCsv.createCsv(startDate, newAcc, newLoc)
            if(!isTrainingState){
                Log.d("addData", "推定中")
                estimateAirPressure(fv)
                Log.d("addData", "推定終了")
            }
        }

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

    private fun createFeatureValue(): Boolean {
        val newAcc = createList("Acc")
        val newGra = createList("Gra")
        val newLoc = createList("Loc")
        val newBar = createList("Bar")

        val runPython = RunPython()
        val featureValue = runPython.createFeatureValue(newAcc, newGra, newLoc, newBar)
        if(featureValue.isNotEmpty()){
            accSd = featureValue[0]
            ampSptList = featureValue as MutableList<Double>
            ampSptList.removeAt(0)
            addFeatureValue()
        }else{
            errorData = "特徴量が取得できなかった"
            Log.d("createFeatureValue", errorData)
            return false
        }
        return true
    }

    private fun createModel(featureValueData: List<FeatureValueData>){
        val fvList: MutableList<List<Double>> = selectFv(featureValueData)
        val runPython = RunPython()
        runPython.createModel(fvList)
        Log.d("createModel", "モデル作成成功")
    }

    private fun estimateAirPressure(featureValueData: List<FeatureValueData>) {
        val fvList: MutableList<List<Double>> = selectFv(featureValueData)
        val runPython = RunPython()
        val estimatedAirPressureList = runPython.estimateAirPressure(fvList)
        var sum = 0
        for(l in estimatedAirPressureList){
            Log.d("l", l.toString())
            sum += l
        }
        Log.d("sum", sum.toString())
        Log.d("size", estimatedAirPressureList.size.toString())
        estimatedAirPressure = sum / estimatedAirPressureList.size
        updateHome()
    }

    private fun selectFv(featureValueData: List<FeatureValueData>): MutableList<List<Double>> {
        val len = featureValueData.size
        Log.d("fv_len", len.toString())
        val fvList = mutableListOf<List<Double>>()
        //
        if(isTrainingState){
            for(i in 0 until len){
                val ampSpcSize = featureValueData[i].ampSptList.size
                Log.d("ampSpcSize", ampSpcSize.toString())
                print(fv)
                Log.d("fv_index", i.toString())
                Log.d("fv", featureValueData[i].toString())
                var l = mutableListOf<Double>()
                l.add(featureValueData[i].accSd)
                for(j in 0 until ampSpcSize){
                    l.add(featureValueData[i].ampSptList[j])
                }
                l.add(featureValueData[i].airPressure.toDouble())
                fvList.add(l)
            }
        }else {
            for(i in len - estimatedRange until len){
                val ampSpcSize = featureValueData[i].ampSptList.size
                var l = mutableListOf<Double>()
                l.add(featureValueData[i].accSd)
                for(j in 0 until ampSpcSize){
                    l.add(featureValueData[i].ampSptList[j])
                }
                fvList.add(l)
            }
        }
        return fvList
    }

    private fun createList(sensorName: String): MutableList<List<Double>> {
        val newList = mutableListOf<List<Double>>()
        when (sensorName) {
            "Acc" -> {
                var len = accTimeList.size
                for(i in 0 until len){
                    newList.add(listOf(xAccList[i], yAccList[i], zAccList[i], accTimeList[i]))
                }
            }
            "Gra" -> {
                var len = graTimeList.size
                for(i in 0 until len){
                    newList.add(listOf(xGraList[i], yGraList[i], zGraList[i], graTimeList[i]))
                }
            }
            "Loc" -> {
                var len = locTimeList.size
                for(i in 0 until len){
                    newList.add(listOf(latList[i], lonList[i], locTimeList[i]))
                }
            }
            "Bar" -> {
                var len = barTimeList.size
                for(i in 0 until len){
                    newList.add(listOf(barList[i], barTimeList[i]))
                }
            }
        }
        return newList

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

}