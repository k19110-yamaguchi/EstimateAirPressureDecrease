package com.example.estimateairpressuredecrease

import android.content.Context
import android.os.Environment
import android.util.Log
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class RunPython {
    val context: Context = MainActivity.content
    var filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

    fun createFeatureValue(newAcc: MutableList<List<Double>>,
                           newGra: MutableList<List<Double>>,
                           newLoc: MutableList<List<Double>>,
                           newBar: MutableList<List<Double>>,
    ): List<Double> {
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        val module = py.getModule("analyzeData") // スクリプト名
        val featureValueStr = module.callAttr("createFeatureValue", newAcc, newGra, newLoc, newBar).toString()
        // 最初と最後の[]を取り除き、","で分割
        if(featureValueStr != "0"){
            return featureValueStr.substring(1, featureValueStr.length - 1).split(",").map { it.trim().toDouble() }
        }else{
            return emptyList()
        }
    }

    fun createModel(TrainingFv: MutableList<List<Double>>){
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        Log.d("filePath", filePath)
        val py = Python.getInstance()
        val module = py.getModule("machineLearning") // スクリプト名
        module.callAttr("createModel", TrainingFv, filePath)
    }

    fun estimateAirPressure(EstimatedFv: MutableList<List<Double>>): List<Int>{
        // Pythonコードを実行する前にPython.start()の呼び出しが必要
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(MainActivity.content))
        }
        val py = Python.getInstance()
        val module = py.getModule("machineLearning") // スクリプト名
        var estimatedAirPressureStr = module.callAttr("estimateAirPressure", EstimatedFv, filePath).toString()
        println(estimatedAirPressureStr)
        // 最初と最後の[]を取り除き、","で分割
        return estimatedAirPressureStr.substring(1, estimatedAirPressureStr.length - 1).split(",").map { it.trim().toFloat().toInt() }

    }
}