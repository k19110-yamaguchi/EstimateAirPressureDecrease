package com.example.estimateairpressuredecrease
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeDao: HomeDao,
    private val sensorDao: SensorDao,
) : ViewModel(){

    val initDate: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)

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



    // Homeのデータを取得
    val home = homeDao.getHomeData().distinctUntilChanged()

    // Accのデータ取得
    val sensorData = sensorDao.getSensorData().distinctUntilChanged()
    val accData = sensorDao.getAccData().distinctUntilChanged()
    val graData = sensorDao.getGraData().distinctUntilChanged()
    val locData = sensorDao.getLocData().distinctUntilChanged()
    val barData = sensorDao.getBarData().distinctUntilChanged()

    // python
    var csvData: String by mutableStateOf("")


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
        Log.d("setHome", home.isTrainingState.toString() +home.minProperPressure.toString() + home.inflatedDate.toString())
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
            Log.d("updateHome", isTrainingState.toString() + minProperPressure.toString() + inflatedDate.toString())
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }

    fun addData() {
        addAcc()
        addGra()
        addLoc()
        addBar()
        addSensor()
        viewModelScope.launch {
            val newSensor = SensorData(startDate = startDate, stopDate = stopDate, airPressure = airPressure)
            sensorDao.insertSensorData(newSensor)
        }
        createCsv()
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

    private fun createCsv() {
        createAccCsv()
        val ravel = listOf(
            "xAcc", "yAcc", "zAcc", "accTime",
            "xGra", "yGra", "zGra", "graTime",
            "lat", "lon", "locTime",
            "bar", "barTime"
        )
    }

    private fun createAccCsv() {
        val ravel = listOf("x(m/s)", "y(m/s)", "z(m/s)", "t(s)")
        val data = mutableListOf<List<Double>>()
        val len = accTimeList.size
        for(i in 0 until len){
            data.add(listOf(xAccList[i], yAccList[i], zAccList[i], accTimeList[i]))
        }

        val csvData = mutableListOf<List<String>>()
        csvData.add(ravel)
        csvData.addAll(data.map { it.map { it.toString() } })

        val file = File(MainActivity.instance.getExternalFilesDir(null), "output.csv")
        val writer = BufferedWriter(FileWriter(file))

        for (row in csvData) {
            writer.write(row.joinToString(","))
            writer.newLine()
        }
        writer.close()
    }

    private fun createGraCsv() {
        val ravel = listOf("x(m/s)", "y(m/s)", "z(m/s)", "t(s)")
    }

    private fun createLocCsv() {
        val ravel = listOf("lat", "lon", "t(s)")
    }

    private fun createBarCsv() {
        val ravel = listOf("bar(kPa)", "t(s)")
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

        startDate = initDate
        stopDate = initDate
        airPressure = 0
    }
}