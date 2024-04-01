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
    //true=追記, false=上書き
    private val fileAppend : Boolean = true
    val context: Context = MainActivity.content
    // 拡張子
    private val extension : String = ".csv"
    // 内部ストレージのDocumentのURL

    // 保存するフォルダ名の作成
    private fun createFileName(startDate: LocalDateTime): String {
        return startDate.toString()
            .replace("-", "")
            .replace("T", "")
            .replace(":", "")
            .substring(0, 14)
    }

    // 加速度のcsv作成
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

    // 重力加速度のcsv作成
    private fun createGraDataCsv(graData: GraData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/gra" + extension
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

    // 位置情報のcsv作成
    private fun createLocDataCsv(locData: LocData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/loc" + extension
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

    // 気圧のcsv作成
    private fun createBarDataCsv(barData: BarData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/bar" + extension
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

    // 特徴量のcsv作成
    private fun createFeatureValueDataCsv(featureValueData: FeatureValueData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/fv" + extension

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

    // センサデータのcsv作成
    fun createCsv(startDate: LocalDateTime, accData: AccData, graData: GraData,locData: LocData, barData: BarData, featureValueData: FeatureValueData){
        val fileName = createFileName(startDate)
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName
        val f = File(filePath)
        // ディレクトリ存在チェック
        if (!f.exists()) {
            // フォルダの作成
            f.mkdirs()
        } else {

        }

        // センサデータのcsvファイルの作成
        createAccDataCsv(accData, fileName)
        createGraDataCsv(graData, fileName)
        createLocDataCsv(locData,fileName)
        createBarDataCsv(barData, fileName)
        createFeatureValueDataCsv(featureValueData, fileName)

    }
}