package com.example.estimateairpressuredecrease

import android.content.Context
import android.os.Environment
import com.example.estimateairpressuredecrease.room.entities.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime

class OpenCsv {
    private val fileAppend : Boolean = true //true=追記, false=上書き
    val context: Context = MainActivity.content
    private val extension : String = ".csv"
    // 内部ストレージのDocumentのURL

    private fun createFileName(startDate: LocalDateTime): String {
        return startDate.toString()
            .replace("-", "")
            .replace("T", "")
            .replace(":", "")
            .substring(0, 14)
    }


    private fun createAccDataCsv(accData: AccData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/acc" + extension
        val header = "time(s),xAcc(m/s^2),yAcc(m/s^2),zAcc(m/s^2),"
        var csvData = header + "\n"
        for(i in 0 until accData.timeList.size){
            csvData +=
                accData.timeList[i].toString() + "," +
                accData.xAccList[i].toString() + "," +
                accData.yAccList[i].toString() + "," +
                accData.zAccList[i].toString() + "," + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

    }

    private fun createGraDataCsv(graData: GraData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "_gra" + extension
        val header = "time(s),xGra(m/s^2),yGra(m/s^2),zGra(m/s^2),"
        var csvData = header + "\n"
        for(i in 0 until graData.timeList.size){
            csvData +=
                graData.timeList[i].toString() + "," +
                        graData.xGraList[i].toString() + "," +
                        graData.yGraList[i].toString() + "," +
                        graData.zGraList[i].toString() + "," + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

    }

    private fun createLocDataCsv(locData: LocData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "gra" + extension
        val header = "time(s),lat(°),lon(°),"
        var csvData = header + "\n"
        for(i in 0 until locData.timeList.size){
            csvData +=
                locData.timeList[i].toString() + "," +
                        locData.latList[i].toString() + "," +
                        locData.lonList[i].toString() + "," + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

    }

    private fun createBarDataCsv(barData: BarData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/gra" + extension
        val header = "time(s),bar(kPa),"
        var csvData = header + "\n"
        for(i in 0 until barData.timeList.size){
            csvData +=
                barData.timeList[i].toString() + "," +
                        barData.barList[i].toString() + "," + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

    }

    // 特徴量
    private fun createFeatureValueDataCsv(featureValueData: FeatureValueData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/gra" + extension

        var header = "accSd(m/s^2),"
        val fqMax = 40
        val fqWidth = 0.5
        for(i in 0 until (fqMax/fqWidth).toInt()){
            header += (i*fqWidth).toString() + "~" + ((i+1)*fqWidth).toString() + "(Hz),"
        }

        var csvData = header + "\n"
        csvData += featureValueData.accSd.toString() + ","

        for(i in 0 until featureValueData.ampSptList.size){
            csvData += featureValueData.ampSptList[i].toString() + ","
        }
        csvData += "\n"

        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.println(csvData)
        pw.close()

    }


    fun createCsv(startDate: LocalDateTime, accData: AccData, graData: GraData,locData: LocData, barData: BarData, featureValueData: FeatureValueData){
        val fileName = createFileName(startDate)
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName
        val f = File(filePath)
        // ディレクトリ存在チェック
        if (!f.exists()) {
            f.mkdirs()
        } else {

        }
        createAccDataCsv(accData, fileName)
        createGraDataCsv(graData, fileName)
        createLocDataCsv(locData,fileName)
        createBarDataCsv(barData, fileName)
        createFeatureValueDataCsv(featureValueData, fileName)

    }
}