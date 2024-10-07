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
        val res = module.callAttr("extractStableInterval", sensingDateList, sensingAirPressureList, minProperPressure, requiredRouteCount, filePath).toString()
        // 最初と最後の[]を取り除き、","で分割
        common.log(res)
        return if(res != "True"){
            res.substring(1, res.length - 1).split(",").map { it.trim().toString() }
        }else{
            emptyList()
        }

    }

    // todo: 推定に使用できるルート数を取得
    fun getAvailableRouteCount(sensingDateList: List<String>, siFileName: String, siStartTime: Double, siStopTime: Double, sensingAirPressureList: List<Int>, minProperPressure: Int, requiredRouteCount: Int, common: Common = Common()){
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("extractStableInterval")
        // 区間をPythonで分割
        val res = module.callAttr("getAvailableRouteCount", sensingDateList, siFileName, siStartTime, siStopTime, sensingAirPressureList, minProperPressure, requiredRouteCount, filePath).toString()
        // 最初と最後の[]を取り除き、","で分割


    }

    // todo: 安定区間内の加速度を抽出

    // todo: 特徴量を取得
    fun createFeatureValue(newAcc: MutableList<List<Double>>, newGra: MutableList<List<Double>>, newLoc: MutableList<List<Double>>, newBar: MutableList<List<Double>>): List<Double> {
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("analyzeData")
        // 特徴量をPythonで取得
        val featureValueStr = module.callAttr("createFeatureValue", newAcc, newGra, newLoc, newBar).toString()
        // 最初と最後の[]を取り除き、","で分割
        return if(featureValueStr != "0"){
            featureValueStr.substring(1, featureValueStr.length - 1).split(",").map { it.trim().toDouble() }
        }else{
            emptyList()
        }
    }

    // todo: 学習モデルを作成
    fun createModel(TrainingFv: MutableList<List<Double>>){
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        Log.d("filePath", filePath)
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("machineLearning")
        module.callAttr("createModel", TrainingFv, filePath)
    }

    // todo: 特徴量から空気圧を推定
    fun estimateAirPressure(EstimatedFv: MutableList<List<Double>>): Int{
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        // スクリプト名
        val module = py.getModule("machineLearning")
        var estimatedAirPressure = module.callAttr("estimateAirPressure", EstimatedFv, filePath).toString()
        // 最初と最後の[]を取り除き、","で分割
        return estimatedAirPressure.toInt()

    }
}