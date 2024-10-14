package com.example.estimateairpressuredecrease
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.data.AccData
import com.example.estimateairpressuredecrease.data.BarData
import com.example.estimateairpressuredecrease.data.GraData
import com.example.estimateairpressuredecrease.data.LocData
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.dao.SensorDao
import com.example.estimateairpressuredecrease.room.dao.StableIntervalDao
import com.example.estimateairpressuredecrease.room.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.round


@HiltViewModel
class MainViewModel @Inject constructor(
    private val homeDao: HomeDao,
    private val sensorDao: SensorDao,
    private val stableIntervalDao: StableIntervalDao,
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
    var outOfCount by mutableStateOf(0)
    // 適正内のデータ数
    var withinCount by mutableStateOf(0)
    // 推定に必要な適正外、適正内の特徴量の数
    val requiredRouteCount = 10
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
    private var sensingAirPressure by mutableStateOf(0)

    // Sensing
    val sensorData = sensorDao.getSensorData().distinctUntilChanged()
    // データを保存するまでの時間(s): デフォ:10
    val saveTime: Double = 2.0
    // センシング終了までに必要なデータサイズ
    private val requiredDataSize = 0.0
    // センシング画面に初めて移動したか
    var isSensingInit = true
    // 推定に必要なデータがあるか
    var isRequiredData: Boolean by mutableStateOf(false)
    // 測定開始時刻
    var startDate: LocalDateTime by mutableStateOf(initDate)
    // 測定終了時刻
    var stopDate: LocalDateTime by mutableStateOf(initDate)
    // 推定空気圧
    var estimatedAirPressure: Int by mutableStateOf(0)
    // センシングデータの日付リスト
    var sensingDateList: MutableList<LocalDateTime> = mutableListOf()
    // センシングデータの空気圧リスト
    var sensingAirPressureList: MutableList<Int> = mutableListOf()


    // Acc
    var xAcc :Double by mutableStateOf(-1.0)
    var yAcc :Double by mutableStateOf(-1.0)
    var zAcc :Double by mutableStateOf(-1.0)
    var accTime: Double by mutableStateOf(-1.0)
    var xAccList: MutableList<Double> = mutableListOf()
    var yAccList: MutableList<Double> = mutableListOf()
    var zAccList: MutableList<Double> = mutableListOf()
    var accTimeList: MutableList<Double> = mutableListOf()


    // Loc
    var lat: Double by mutableStateOf(-1.0)
    var lon: Double by mutableStateOf(-1.0)
    var dis: Double by mutableStateOf(-1.0)
    var speed: Double by mutableStateOf(-1.0)
    var locTime: Double by mutableStateOf(-1.0)
    var latList: MutableList<Double> = mutableListOf()
    var lonList: MutableList<Double> = mutableListOf()
    var locTimeList: MutableList<Double> = mutableListOf()
    var disList: MutableList<Double> = mutableListOf()
    var speedList: MutableList<Double> = mutableListOf()


    // Gra
    var xGra :Double by mutableStateOf(-1.0)
    var yGra :Double by mutableStateOf(-1.0)
    var zGra :Double by mutableStateOf(-1.0)
    var graTime: Double by mutableStateOf(-1.0)
    var xGraList: MutableList<Double> = mutableListOf()
    var yGraList: MutableList<Double> = mutableListOf()
    var zGraList: MutableList<Double> = mutableListOf()
    var graTimeList: MutableList<Double> = mutableListOf()

    // Bar
    var bar: Double by mutableStateOf(-1.0)
    var barTime: Double by mutableStateOf(-1.0)
    var barList: MutableList<Double> = mutableListOf()
    var barTimeList: MutableList<Double> = mutableListOf()

    // StableInterval
    // 安定区間を抽出するファイル名
    var siFileName: String by mutableStateOf("")
    // 安定区間の開始時間
    var siStartTime: Double by mutableStateOf(-1.0)
    // 安定区間の終了時間
    var siStopTime: Double by mutableStateOf(-1.0)
    // 安定区間が取得できる適正内のデータ数
    var withinAvailableRouteCount: Int by mutableStateOf(-1)
    // 安定区間が取得できる適正外のデータ数
    var outOfAvailableRouteCount: Int by mutableStateOf(-1)
    // 使用できるセンサデータのファイル名
    var availableFileNameList : MutableList<String> = mutableListOf()
    // stableIntervalのデータを取得
    var stableIntervalData = stableIntervalDao.getStableIntervalData().distinctUntilChanged()

    // FeatureValue
    var accSd: Double by mutableStateOf(0.0)
    var ampSptList: MutableList<Double> = mutableListOf()
    var startGetFv: MutableList<Double> = mutableListOf()
    var stopGetFv: MutableList<Double> = mutableListOf()
    var estimatedRange = 10

    // DataManagement
    var dataManagementMessage: String by mutableStateOf("")

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

    // todo: 推定に適した場所をから特徴量を抽出
    // todo: モデルの作成
    // todo: 空気圧を推定
    // todo: 空気圧注入を促す

    fun countWithinData(sensorData: List<SensorData>){
        outOfCount = 0
        withinCount = 0
        // 適正外、適正内のデータの数を調べる
        for (sd in sensorData) {
            if (sd.sensingAirPressure >= minProperPressure) {
                withinCount += 1
            } else {
                outOfCount += 1
            }
        }
    }

    // センシングデータの日付・空気圧リストを取得
    fun getSensingData(sensorData: List<SensorData>){
        sensingDateList = emptyList<LocalDateTime>().toMutableList()
        sensingAirPressureList = emptyList<Int>().toMutableList()
        for (sd in sensorData){
            sensingDateList.add(sd.startDate)
            sensingAirPressureList.add(sd.sensingAirPressure)
        }
    }

    fun checkIsEstState(sensorData: List<SensorData>){
        // 必要サイズ以上になった場合
        if(outOfCount >= requiredRouteCount && withinCount >= requiredRouteCount){
            // 特徴量を計算
            calcFeatureValue(sensorData)
            //createModel(featureValueData)
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
                addSensorData(true)
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
    // todo: 推定に必要なデータ数を決める
    fun checkRequiredData(){
        if(locTime > requiredDataSize){
            isRequiredData = true
        }
    }

    // 安定区間
    private fun checkRequiredIntervals(sensingAirPressureList: List<Int>): Boolean{
        var res = false
        for(sa in sensingAirPressureList){
            withinCount = 0
            outOfCount = 0
            if(sa >= minProperPressure){
                withinCount += 1
            }else{
                outOfCount += 1
            }
        }
        if(withinCount >= requiredRouteCount && outOfCount >= requiredRouteCount){
            res = true
        }
        return res
    }

    fun checkRequiredStableIntervalRoute(): Boolean{
        var res = false
        if(withinAvailableRouteCount >= requiredRouteCount && outOfAvailableRouteCount >= requiredRouteCount){
            res = true
        }
        return res
    }


    // センサデータを追加
    fun addSensorData(isFinished: Boolean = false) {
        // テストデータを作成する
        // createTestData()

        // 新しいデータの要素
        val newAcc = AccData(xAccList = xAccList, yAccList = yAccList, zAccList = zAccList, timeList = accTimeList)
        val newGra = GraData(xGraList = xGraList, yGraList = yGraList, zGraList = zGraList, timeList = graTimeList)
        val newLoc = LocData(latList = latList, lonList = lonList, timeList = locTimeList, disList = disList, speedList = speedList)
        val newBar = BarData(barList = barList, timeList = barTimeList)

        // ファイルの作成
        val openCsv = OpenCsv()
        val sensorDataPath = openCsv.createSensorDataCsv(startDate, newAcc, newGra, newLoc, newBar)
        common.log("センサデータをcsvとして保存")

        if(isFinished){
            GlobalScope.launch(Dispatchers.IO) {
                // 保存されていたセンサ日付をファイル名の形に変換
                val sensorDataFileNameList: MutableList<String> = mutableListOf()
                for (sd in sensingDateList) {
                    sensorDataFileNameList.add(openCsv.createFileName(sd))
                }
                // 現在センシングした日付と空気圧を取得
                val curtSensorDate = openCsv.createFileName(startDate)
                sensorDataFileNameList.add(curtSensorDate)
                sensingAirPressureList.add(sensingAirPressure)


                // 走行データから推定に使用できる区間に分割
                val rp = RunPython()
                val isSuccessExtractIntervals = rp.extractIntervals(curtSensorDate)
                // 区間抽出に成功したら
                if (isSuccessExtractIntervals) {
                    // 重い処理をここで実行

                    // 学習時
                    if (isTrainingState) {
                        // センサ情報をデータベースに保存
                        val newSensor = SensorData(
                            startDate = startDate,
                            stopDate = stopDate,
                            sensingAirPressure = sensingAirPressure,
                            estimatedAirPressure = estimatedAirPressure,
                            sensorDataPath = sensorDataPath
                        )
                        addSensor(newSensor)
                        common.log("センサデータをデータベースに保存")


                        //　共通区間の抽出
                        homeMessage = "共通区間の抽出中"
                        rp.extractCommonIntervals(sensorDataFileNameList)

                        // センシング時の空気圧をcsvに保存
                        openCsv.createSensingAirPressure(
                            sensorDataFileNameList,
                            sensingAirPressureList
                        )

                        // 安定区間を求めるか
                        val isRequiredIntervals = checkRequiredIntervals(sensingAirPressureList)

                        if (true) {
                            common.log("安定区間の抽出")
                            // 安定区間の抽出
                            homeMessage = "安定区間の抽出中"
                            val siInfoList = rp.extractStableInterval(
                                sensorDataFileNameList,
                                sensingAirPressureList,
                                minProperPressure,
                                requiredRouteCount
                            )
                            addStableInterval(siInfoList)
                            // 安定区間内のデータが必要な数あるか
                            val isRequiredStableIntervalRoute = checkRequiredStableIntervalRoute()

                            if (true) {
                                // todo: 安定区間内の加速度csvを作成
                                homeMessage = "安定区間内の加速度抽出中"
                                rp.extractAccData(
                                    availableFileNameList,
                                    siFileName,
                                    siStartTime,
                                    siStopTime
                                )
                                // todo: モデルの作成
                            }

                        } else {

                            common.log("安定区間を求めるのに必要なデータが足りない")

                        }


                        // 推定時
                    } else {
                        // todo: 今取ったデータが安定区間内に使用できるデータがあるか

                        // todo: 空気圧の推定

                        // todo: 安定区間を作り直す

                    }
                    homeMessage = "処理の終了"


                } else {
                    // todo: 今取得したセンサデータのcsvファイルを削除(コメントアウト中)
                    homeMessage = "区間抽出に失敗"
                    // openCsv.deleteSensorDataCsv(startDate)
                }
                resetSensing()
            }

        }else{
            resetSensorData()
        }

    }


    // センサデータをデータベースに追加
    private fun addSensor(newSensor: SensorData) {
        viewModelScope.launch {
            sensorDao.insertSensorData(newSensor)
        }
    }

    // 安定区間データのデータベースを作成
    private fun createStableInterval(newStableInterval: StableIntervalData) {
        viewModelScope.launch {
            stableIntervalDao.createStableIntervalDB(newStableInterval)
        }
    }

    // 安定区間データのデータベースを更新
    private fun updateStableInterval(newStableInterval: StableIntervalData) {
        viewModelScope.launch {
            stableIntervalDao.updateStableIntervalData(newStableInterval)
        }
    }

    // 安定区間の情報を追加
    private fun addStableInterval(siInfoList: List<String>){
        withinAvailableRouteCount = siInfoList[0].toInt()
        outOfAvailableRouteCount = siInfoList[1].toInt()

        // 安定区間データのデータベースがあるかどうか
        if ((siInfoList.size  >= 3)){
            siFileName = siInfoList[2].replace("'", "")
            siStartTime = siInfoList[3].toDouble()
            siStopTime = siInfoList[4].toDouble()
            availableFileNameList = siInfoList.last()
                .removeSurrounding("['", "']")
                .split("', '")
                .map { it.trim() }.toMutableList()

        }

        val newStableInterval = StableIntervalData(siFileName = siFileName, siStarTime = siStartTime, siStopTime = siStopTime, withinAvailableRouteCount = withinAvailableRouteCount, outOfAvailableRouteCount = outOfAvailableRouteCount, availableFileNameList = availableFileNameList)

        viewModelScope.launch {
            // id:0 のstableIntervalがnullだった場合
            if (stableIntervalDao.getStableIntervalById(0) == null){
                // 安定区間のデータベースを作成
                createStableInterval(newStableInterval)
            }else{
                // 安定区間のデータベースを更新
                updateStableInterval(newStableInterval)
            }
        }
    }

    fun setStableInterval(stableInterval: StableIntervalData){
        siFileName = stableInterval.siFileName
        siStartTime = stableInterval.siStarTime
        siStopTime = stableInterval.siStopTime
        withinAvailableRouteCount = stableInterval.withinAvailableRouteCount
        outOfAvailableRouteCount = stableInterval.outOfAvailableRouteCount
        availableFileNameList = stableInterval.availableFileNameList.toMutableList()

    }




    // 特徴量を取得
    private fun calcFeatureValue(sensorData: List<SensorData>) {



        // Pythonを用いて特徴量を取得
        /*
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
         */

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

    private fun resetSensorData(){
        xAccList = emptyList<Double>().toMutableList()
        yAccList = emptyList<Double>().toMutableList()
        zAccList = emptyList<Double>().toMutableList()
        accTimeList = emptyList<Double>().toMutableList()

        xGraList =  emptyList<Double>().toMutableList()
        yGraList =  emptyList<Double>().toMutableList()
        zGraList =  emptyList<Double>().toMutableList()
        graTimeList =  emptyList<Double>().toMutableList()

        latList = emptyList<Double>().toMutableList()
        lonList = emptyList<Double>().toMutableList()
        locTimeList = emptyList<Double>().toMutableList()
        disList = emptyList<Double>().toMutableList()
        speedList = emptyList<Double>().toMutableList()

        barList =  emptyList<Double>().toMutableList()
        barTimeList =  emptyList<Double>().toMutableList()

    }

    private fun resetSensing() {
        resetSensorData()
        startDate = initDate
        stopDate = initDate
        sensingAirPressure = 0
        estimatedAirPressure = 0
        isSensingInit = true
        isRequiredData = false

        xAcc = -1.0
        yAcc = -1.0
        zAcc = -1.0
        accTime = -1.0

        lat = -1.0
        lon = -1.0
        locTime = -1.0
        dis = -1.0
        speed = -1.0

        // Gra
        xGra = -1.0
        yGra = -1.0
        zGra = -1.0
        graTime = -1.0

        // Bar
        bar = -1.0
        barTime = -1.0
    }

    // DataManagement
    // 受け取ったセンサデータを削除
    fun deleteSensorData(sensorData: SensorData){
        val openCsv = OpenCsv()
        // センサデータのcsvを削除
        openCsv.deleteSensorDataCsv(sensorData.startDate)
        viewModelScope.launch {
            sensorDao.deleteSensorData(sensorData)
            val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH-mm-ss")
            dataManagementMessage = "${sensorData.startDate.format(dateFormat)}のデータを削除"
        }

    }



    // ↓ 書き換え前
    /*
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
 */
}