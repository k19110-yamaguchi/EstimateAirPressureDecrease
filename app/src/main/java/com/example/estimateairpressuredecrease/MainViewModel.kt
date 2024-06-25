package com.example.estimateairpressuredecrease
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.room.dao.FeatureValueDao
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorDao
import com.example.estimateairpressuredecrease.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.round


@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeDao: HomeDao,
    private val sensorDao: SensorDao,
    private val featureValueDao: FeatureValueDao,
) : ViewModel(){
    private var common = Common()

    // Main
    // 画面の状態
    // [ホーム:1,センシング:2、入力:3]
    var screenStatus by mutableStateOf(common.homeNum)
    // [適正空気圧:1、測定空気圧:2]
    var inputStatus by mutableStateOf(common.inputProperPressureNum)
    private var isFirst by mutableStateOf(false)

    //Home
    // Homeのデータを取得
    val homeData = homeDao.getHomeData().distinctUntilChanged()
    // 学習状態かどうか
    var isTrainingState by mutableStateOf(true)
    // 適正外のデータ数
    var outOfSize by mutableStateOf(0)
    // 適正内のデータ数
    var withinSize by mutableStateOf(0)
    // 推定に必要な適正外、適正内の特徴量の数
    val requiredFvSize = 5
    // 初期の日付
    val initDate: LocalDateTime = LocalDateTime.of(2000, 1, 1, 0, 0, 0)
    // 空気を注入した時期
    var inflatedDate: LocalDateTime by mutableStateOf(initDate)

    // メッセージ
    var homeMessage by mutableStateOf("")
    // 空気圧推定の数
    private val requiredAirPressureSize = 3

    // Input
    // 入力している空気圧
    var editingAirPressure by mutableStateOf("")
    // 最小適正空気圧を求めるのに必要な情報
    var editingBodyWeight by mutableStateOf("")
    var editingBicycleWeight by mutableStateOf("")
    var editingTireWidth by mutableStateOf("")
    // 空気圧入力欄でのエラー
    var errorInputAirPressure by mutableStateOf("")
    // 最小適正空気圧
    var minProperPressure by mutableStateOf(0)
    // 測定空気圧
    var sensingAirPressure by mutableStateOf(0)

    // Sensing
    val sensorData = sensorDao.getSensorData().distinctUntilChanged()
    // 推定に必要なデータがあるか
    var isRequiredData: Boolean by mutableStateOf(false)
    // 測定開始時刻
    var startDate: LocalDateTime by mutableStateOf(initDate)
    // 測定終了時刻
    var stopDate: LocalDateTime by mutableStateOf(initDate)
    // 推定空気圧
    var estimatedAirPressure: Int by mutableStateOf(0)


    // Acc
    val accData = sensorDao.getAccData().distinctUntilChanged()
    var xAccList: MutableList<Double> = mutableListOf()
    var yAccList: MutableList<Double> = mutableListOf()
    var zAccList: MutableList<Double> = mutableListOf()
    var accTime: Double by mutableStateOf(-1.0)
    var accTimeList: MutableList<Double> = mutableListOf()

    // Loc
    val locData = sensorDao.getLocData().distinctUntilChanged()
    var latList: MutableList<Double> = mutableListOf()
    var lonList: MutableList<Double> = mutableListOf()
    var locTime: Double by mutableStateOf(-1.0)
    var locTimeList: MutableList<Double> = mutableListOf()
    var disList: MutableList<Double> = mutableListOf()
    var speedList: MutableList<Double> = mutableListOf()

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
    val featureValueData = featureValueDao.getFeatureValues().distinctUntilChanged()
    var accSd: Double by mutableStateOf(0.0)
    var ampSptList: MutableList<Double> = mutableListOf()
    var startGetFv: MutableList<Double> = mutableListOf()
    var stopGetFv: MutableList<Double> = mutableListOf()
    var estimatedRange = 10

    // Main
    // 最初の起動かどうか調査
    fun checkIsInitialization(){
        viewModelScope.launch {
            // id:0 のhomeがnullだった場合
            if (homeDao.getHomeById(0) == null){
                isFirst = true
                screenStatus = common.inputNum
                inputStatus = common.inputProperPressureNum
            }else{
                isFirst = false
            }
        }
    }

    // Home
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
    private fun updateHome() {
        viewModelScope.launch {
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }

    // todo: 学習状態から推定状態に変更できるか調べる
    fun checkIsEstState(featureValueData: List<FeatureValueData>){
        outOfSize = 0
        withinSize = 0
        // 適正外、適正内のデータの数を調べる
        for (fv in featureValueData) {
            if (fv.sensingAirPressure >= minProperPressure) {
                withinSize += 1
            } else {
                outOfSize += 1
            }
        }

        // 必要サイズ以上になった場合
        if(outOfSize >= requiredFvSize && withinSize >= requiredFvSize){
            // todo: モデルの作成
            createModel(featureValueData)
            isTrainingState = false
            updateHome()
        }
    }

    // 空気注入時期の更新
    fun updateInflateDate(){
        inflatedDate = LocalDateTime.now()
        updateHome()
    }

    fun showInflateDate(): String{
        val year = inflatedDate.toString().substring(0, 4)
        val month = inflatedDate.toString().substring(5, 7)
        val day = inflatedDate.toString().substring(8, 10)
        return "${month}月${day}日"

    }

    fun showEstimatedAirPressure(sensorData: List<SensorData>): String{
        val l :MutableList<Int> = mutableListOf()
        val sensorDataSize = sensorData.size
        for(i in 0 until requiredAirPressureSize) {
            val index = sensorDataSize - 1 - i
            if (inflatedDate.isAfter(sensorData[index].startDate)){
                var airPressure = sensorData[index].estimatedAirPressure
                if (airPressure == 0) {
                    airPressure = sensorData[index].sensingAirPressure
                }
                l.add(airPressure)
            }else{
                break
            }
        }

        return if(l.isNotEmpty()){
            var sum = 0
            for(ap in l){
                sum += ap
            }
            (sum/l.size).toString()
        }else{
            ""
        }
    }

    // Input
    // 最小適正空気圧を計算
    fun calcMinProperPressure(){
        try{
            var sloop = 0.9 / (editingTireWidth.toDouble() - 11.2)
            var weight = (editingBodyWeight.toDouble() + editingBicycleWeight.toDouble() + 46)
            editingAirPressure = (10 * round(0.01 * 50 * 10 * sloop * weight)).toInt().toString()
            errorInputAirPressure = ""

        }catch (e: NumberFormatException){
            errorInputAirPressure = "数字(整数)を入力してください"
        }
    }

    // 入力された空気圧をデータベースに保存
    fun inputAirPressure(){
        try{
            // 最小適正空気圧入力時
            if(inputStatus == common.inputProperPressureNum){
                minProperPressure = editingAirPressure.toInt()
                // 初回起動時
                if(isFirst){
                    createHome()
                    // 初回以外時
                }else{
                    updateHome()
                }

            // 学習状態・空気圧入力時
            }else{
                sensingAirPressure = editingAirPressure.toInt()
                addData()
            }
            screenStatus = common.homeNum
            resetInput()


        }catch (e: NumberFormatException){
            errorInputAirPressure = "数字(整数)を入力してください"
        }
    }

    private fun resetInput(){
        editingAirPressure = ""
        editingBodyWeight = ""
        editingBicycleWeight = ""
        editingTireWidth = ""
        errorInputAirPressure = ""

    }

    // Sensing
    // 推定に必要なデータがあるか調べる
    fun checkRequiredData(){
        if(locTimeList.size > 10){
            isRequiredData = true
        }
    }


    // データベースにセンサデータを追加
    private fun addData() {
        // テストデータを作成する
        // createTestData()

        // 新しいデータの要素
        val newAcc = AccData(xAccList = xAccList, yAccList = yAccList, zAccList = zAccList, timeList = accTimeList)
        val newGra = GraData(xGraList = xGraList, yGraList = yGraList, zGraList = zGraList, timeList = graTimeList)
        val newLoc = LocData(latList = latList, lonList = lonList, timeList = locTimeList, disList = disList, speedList = speedList)
        val newBar = BarData(barList = barList, timeList = barTimeList)
        val newSensor = SensorData(startDate = startDate, stopDate = stopDate, sensingAirPressure = sensingAirPressure, estimatedAirPressure = estimatedAirPressure)

        // データベースに保存
        addAcc(newAcc)
        addGra(newGra)
        addLoc(newLoc)
        addBar(newBar)
        addSensor(newSensor)

        // ファイルの作成
        val openCsv = OpenCsv()
        openCsv.createSensorDataCsv(startDate, newAcc, newGra, newLoc, newBar)
        common.log("ファイルの作成に成功")
        resetSensing()
    }

    // 加速度データをデータベースに追加
    private fun addAcc(newAcc: AccData) {
        viewModelScope.launch {
            sensorDao.insertAccData(newAcc)

        }
    }

    // 重力加速度データをデータベースに追加
    private fun addGra(newGra: GraData) {
        viewModelScope.launch {
            sensorDao.insertGraData(newGra)
        }
    }

    // 位置情報データをデータベースに追加
    private fun addLoc(newLoc: LocData) {
        viewModelScope.launch {
            sensorDao.insertLocData(newLoc)
        }
    }

    // 気圧データをデータベースに追加
    private fun addBar(newBar: BarData) {
        viewModelScope.launch {
            sensorDao.insertBarData(newBar)
        }
    }

    // センサデータをデータベースに追加
    private fun addSensor(newSensor: SensorData) {
        viewModelScope.launch {
            sensorDao.insertSensorData(newSensor)
        }
    }

    // 特徴量データをデータベースに追加
    private fun addFeatureValue(newFeatureValue: FeatureValueData) {
        viewModelScope.launch {
            featureValueDao.insertFeatureValues(newFeatureValue)
        }
    }

    // 特徴量を取得
    private fun createFeatureValue(): Boolean {
        // リストに変換
        val newAcc = createList("Acc")
        val newGra = createList("Gra")
        val newLoc = createList("Loc")
        val newBar = createList("Bar")

        // Pythonを用いて特徴量を取得
        val runPython = RunPython()
        val featureValue = runPython.createFeatureValue(newAcc, newGra, newLoc, newBar)
        // 特徴量が正しく取得できた場合
        return if(featureValue.isNotEmpty()){
            accSd = featureValue[0]
            ampSptList = featureValue as MutableList<Double>
            ampSptList.removeAt(0)
            true
        // 特徴量が正しく取得できなかった場合
        }else{
            false
        }
    }

    // センサデータをリストに変換
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
                    newList.add(listOf(latList[i], lonList[i], locTimeList[i], disList[i], speedList[i]))
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

    private fun resetSensing() {
        startDate = initDate
        stopDate = initDate
        sensingAirPressure = 0
        estimatedAirPressure = 0

        xAccList = emptyList<Double>().toMutableList()
        yAccList = emptyList<Double>().toMutableList()
        zAccList = emptyList<Double>().toMutableList()
        accTime = -1.0
        accTimeList = emptyList<Double>().toMutableList()

        latList =  emptyList<Double>().toMutableList()
        lonList = emptyList<Double>().toMutableList()
        locTime = -1.0
        locTimeList = emptyList<Double>().toMutableList()
        disList = emptyList<Double>().toMutableList()
        speedList = emptyList<Double>().toMutableList()

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
        isRequiredData = false
        startGetFv = emptyList<Double>().toMutableList()
        stopGetFv = emptyList<Double>().toMutableList()

    }


    // ↓ 書き換え前

    private fun createTestData(){
        latList = listOf(35.188842,35.188878, 35.188914, 35.188953, 35.188991, 35.189028, 35.189066, 35.189105, 35.189145, 35.189183) as MutableList<Double>
        lonList = listOf(137.105494,137.105483, 137.105470, 137.105459, 137.105450, 137.105440, 137.105429, 137.105416, 137.105404, 137.1053935) as MutableList<Double>
        locTimeList = listOf(1.44, 2.46, 3.44, 4.45, 5.45, 6.46, 7.45, 8.45, 9.44, 10.46) as MutableList<Double>
    }


    private fun createModel(featureValueData: List<FeatureValueData>){
        // モデル作成時の特徴量をListに変換
        val len = featureValueData.size
        val fvList = mutableListOf<List<Double>>()
        for(i in 0 until len){
            val ampSpcSize = featureValueData[i].ampSptList.size
            var l = mutableListOf<Double>()
            l.add(featureValueData[i].accSd)
            for(j in 0 until ampSpcSize){
                l.add(featureValueData[i].ampSptList[j])
            }
            l.add(featureValueData[i].sensingAirPressure.toDouble())
            fvList.add(l)
        }

        val runPython = RunPython()
        runPython.createModel(fvList)
        Log.d("createModel", "モデル作成成功")
    }

    private fun estimateAirPressure(featureValueData: FeatureValueData) {
        // 空気圧推定時の特徴量をListに変換
        val runPython = RunPython()
        var l = mutableListOf<Double>()
        val fvList = mutableListOf<List<Double>>()
        val ampSpcSize = featureValueData.ampSptList.size
        l.add(featureValueData.accSd)
        for(i in 0 until ampSpcSize){
            l.add(featureValueData.ampSptList[i])
        }
        fvList.add(l)

        // 推定空気圧を取得
        estimatedAirPressure = runPython.estimateAirPressure(fvList)

    }

    private fun selectFv(featureValueData: List<FeatureValueData>): MutableList<List<Double>> {
        val len = featureValueData.size
        val fvList = mutableListOf<List<Double>>()
        // モデル作成時の特徴量をListに変換
        if(isTrainingState){
            for(i in 0 until len){
                val ampSpcSize = featureValueData[i].ampSptList.size
                var l = mutableListOf<Double>()
                l.add(featureValueData[i].accSd)
                for(j in 0 until ampSpcSize){
                    l.add(featureValueData[i].ampSptList[j])
                }
                l.add(featureValueData[i].sensingAirPressure.toDouble())
                fvList.add(l)
            }
        //
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
}