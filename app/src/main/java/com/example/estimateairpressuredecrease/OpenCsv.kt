package com.example.estimateairpressuredecrease

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.estimateairpressuredecrease.room.entities.AccData
import com.example.estimateairpressuredecrease.room.entities.LocData
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime

class OpenCsv {
    private val fileAppend : Boolean = true //true=追記, false=上書き
    val context: Context = MainActivity.content
    private val extension : String = ".csv"
    // 内部ストレージのDocumentのURL

    private fun createFileName(startDate: LocalDateTime): String{
        Log.d("startDate", startDate.toString())
        var res = startDate.toString()
        return res
    }

    fun createCsv(startDate: LocalDateTime, accData: AccData, locData: LocData, airPressure: Int){
        val accDataCsv: String = accData.xAccList[0].toString()
        val fileName = createFileName(startDate)
        var filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "_" + airPressure.toString() + "_acc" + extension

        Log.d("accDataCsv", accDataCsv)
        var csvData = ""
        csvData += "time(s),xAcc(m/s^2),yAcc(m/s^2),zAcc(m/s^2),\n"
        for(i in 0 until accData.timeList.size){
            csvData += accData.timeList[i].toString() + "," +
                    accData.xAccList[i].toString() + "," +
                    accData.yAccList[i].toString() + "," +
                    accData.zAccList[i].toString() + "," + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

        filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "_loc" + extension
        csvData = ""
        csvData += "time(s),lat,lon,\n"
        for(i in 0 until locData.timeList.size){
            csvData += locData.timeList[i].toString() + "," +
                    locData.latList[i].toString() + "," +
                    locData.lonList[i].toString() + "," + "\n"
        }
        fw = FileWriter(filePath, fileAppend)
        pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

        Log.d("createCsv", "csvファイル作成成功")

    }
}