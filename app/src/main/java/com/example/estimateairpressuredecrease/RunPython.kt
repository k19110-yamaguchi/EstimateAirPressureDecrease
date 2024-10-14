package com.example.estimateairpressuredecrease

import android.content.Context
import android.os.Environment
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class RunPython {
    val context: Context = MainActivity.content
    private var filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

    // 走行データを推定に使用できる区間に分割
    fun extractIntervals(sensingDate: String, common: Common = Common()): Boolean{

        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractIntervals")
        // 区間をPythonで分割
        val res = module.callAttr("extractIntervals", sensingDate, filePath).toString()
        val isSuccess = res.toBoolean()
        if (isSuccess){
            common.log("走行データを推定に使用できる区間の抽出に成功")
        }else{
            common.log("走行データを推定に使用できる区間の抽出に失敗")
        }
        return isSuccess

    }

    //　共通区間の抽出
    fun extractCommonIntervals(sensingDateList: List<String>, common: Common = Common()){

        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractCommonIntervals")
        // 区間をPythonで分割
        val res = module.callAttr("extractCommonIntervals", sensingDateList, filePath).toString()
        val isSuccess = res.toBoolean()
        if (isSuccess){
            common.log("共通区間の抽出の抽出に成功")
        }

    }

    // 安定区間の抽出
    fun extractStableInterval(sensingDateList: List<String>, sensingAirPressureList: List<Int>, minProperPressure: Int, requiredRouteCount: Int, common: Common = Common()): List<String>{
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractStableInterval")
        // 区間をPythonで分割
        var res = module.callAttr("extractStableInterval", sensingDateList, sensingAirPressureList, minProperPressure, requiredRouteCount, filePath).toString()
        common.log(res)
        return if(res != "True"){
            res.removeSurrounding("[", "]")
                .split(Regex(",(?![^\\[\\]]*\\])"))  // リスト内のカンマを無視する
                .map { it.trim() }
        }else{
            emptyList()
        }

    }

    // 安定区間内の加速度を抽出
    fun extractAccData(availableFileNameList: List<String>, siFileName: String, siStartTime: Double, siStopTime: Double, common: Common = Common()){
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractAccData")
        // 区間をPythonで分割
        val isSuccess = module.callAttr("extractAccData", availableFileNameList, siFileName, siStartTime, siStopTime, filePath).toBoolean()

        if (isSuccess){
            common.log("安定区間内の加速度抽出に成功")
        }


    }


    // 学習用特徴量を取得
    fun createTrainingFeatureValue(availableFileNameList: List<String>, availableAirPressureList: List<Int>,common: Common = Common()) {
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("analyzeData")
        // 特徴量をPythonで取得
        val isSuccess = module.callAttr("createTrainingFeatureValue", availableFileNameList, availableAirPressureList, filePath).toBoolean()
        // 最初と最後の[]を取り除き、","で分割
        if (isSuccess){
            common.log("特徴量抽出に成功")
        }
    }

    // 学習モデルを作成
    fun createModel(common: Common = Common()){
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        Log.d("filePath", filePath)
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("machineLearning")
        val isSuccess = module.callAttr("createModel", filePath).toBoolean()
        if (isSuccess){
            common.log("学習モデル作成に成功")
        }
    }

    // 今取ったデータが安定区間内に使用できるデータがあるか
    fun extractEstimatedAccData(curtSensorDate: String, siFileName: String, siStartTime: Double, siStopTime: Double, common: Common = Common()): Boolean {
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }

        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractAccData")
        return module.callAttr("extractEstimatedAccData", curtSensorDate, siFileName, siStartTime, siStopTime, filePath).toBoolean()
    }

    // 学習用特徴量を取得
    fun createEstimatedFeatureValue(curtSensorDate: String, common: Common = Common()) {
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("analyzeData")
        // 特徴量をPythonで取得
        val isSuccess = module.callAttr("createEstimatedFeatureValue", curtSensorDate, filePath).toBoolean()
        // 最初と最後の[]を取り除き、","で分割
        if (isSuccess){
            common.log("特徴量抽出に成功")
        }
    }

    // todo: 特徴量から空気圧を推定
    fun estimateAirPressure(common: Common = Common()): Int{
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("machineLearning")
        var estimatedAirPressure = module.callAttr("estimateAirPressure", filePath).toString()

        return estimatedAirPressure.toInt()

    }
}