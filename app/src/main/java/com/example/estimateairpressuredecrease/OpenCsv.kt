package com.example.estimateairpressuredecrease

import android.content.Context
import android.os.Environment
import com.example.estimateairpressuredecrease.data.*
import com.example.estimateairpressuredecrease.room.entities.SensorData
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class OpenCsv {
    //true=追記, false=上書き
    private var fileAppend : Boolean = true
    val context: Context = MainActivity.content
    // 拡張子
    private val extension : String = ".csv"

    // 保存するフォルダ名の作成
    fun createFileName(startDate: LocalDateTime): String {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH-mm-ss")
        return startDate.format(dateFormat).toString()
            .replace("/", "")
            .replace("-", "")
            .replace(" ", "")
            .substring(0, 14)
    }

    private fun isFileExists(file: File): Boolean {
        return file.exists() && !file.isDirectory
    }

    // 加速度のcsv作成
    private fun createAccDataCsv(accData: AccData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/acc" + extension
        var csvData = ""

        // ファイルが存在するか調べる
        val file = File(filePath)
        if(!isFileExists(file)){
            val header = "time(s),x(m/s^2),y(m/s^2),z(m/s^2)"
            csvData = header + "\n"
        }

        for(i in 0 until accData.timeList.size){
            csvData +=
                accData.timeList[i].toString() + "," +
                accData.xAccList[i].toString() + "," +
                accData.yAccList[i].toString() + "," +
                accData.zAccList[i].toString() + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }

    // 重力加速度のcsv作成
    private fun createGraDataCsv(graData: GraData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/gra" + extension
        var csvData = ""

        // ファイルが存在するか調べる
        val file = File(filePath)
        if(!isFileExists(file)){
            val header = "time(s),x(m/s^2),y(m/s^2),z(m/s^2)"
            csvData = header + "\n"
        }

        for(i in 0 until graData.timeList.size){
            csvData +=
                graData.timeList[i].toString() + "," +
                        graData.xGraList[i].toString() + "," +
                        graData.yGraList[i].toString() + "," +
                        graData.zGraList[i].toString() + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }

    // 位置情報のcsv作成
    private fun createLocDataCsv(locData: LocData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/loc" + extension
        var csvData = ""

        // ファイルが存在するか調べる
        val file = File(filePath)
        if(!isFileExists(file)){
            val header = "time(s),lat(°),lon(°),dis(km),speed(km/h)"
            csvData = header + "\n"
        }

        for(i in 0 until locData.timeList.size){
            val formattedString = String.format("%.5f", locData.disList[i])
            csvData +=
                locData.timeList[i].toString() + "," +
                        locData.latList[i].toString() + "," +
                        locData.lonList[i].toString() + "," +
                        formattedString + "," +
                        locData.speedList[i].toString() + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }

    // 気圧のcsv作成
    private fun createBarDataCsv(barData: BarData, fileName: String){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/bar" + extension
        var csvData = ""

        // ファイルが存在するか調べる
        val file = File(filePath)
        if(!isFileExists(file)){
            val header = "time(s),bar(kPa)"
            csvData = header + "\n"
        }

        for(i in 0 until barData.timeList.size){
            csvData +=
                barData.timeList[i].toString() + "," +
                        barData.barList[i].toString() + "\n"
        }
        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }

    /*
    // 特徴量のcsv作成
    private fun createFeatureValueDataCsv(featureValueData: FeatureValueData, fileName: String, common: Common = Common()){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName + "/fv" + extension

        var header = "accSd(m/s^2),"
        val fqMax = 20
        val fqWidth = 0.5
        for(i in 0 until (fqMax/fqWidth).toInt()){
            header += (i*fqWidth).toString() + "~" + ((i+1)*fqWidth).toString() + "(Hz),"
        }
        header += "sensingAirPressure(kPa),estimatedAirPressure(kPa)"

        var csvData = header + "\n"
        csvData += featureValueData.accSd.toString() + ","

        for(i in 0 until featureValueData.ampSptList.size){
            common.log("fvAmp[${i}]: ${featureValueData.ampSptList[i]}")
            csvData += featureValueData.ampSptList[i].toString() + ","
        }
        csvData += featureValueData.sensingAirPressure.toString() + ","
        csvData += featureValueData.estimatedAirPressure.toString() + ","
        csvData += "\n"
        csvData += "startGetFv,"
        for(i in 0 until featureValueData.startGetFv.size){
            csvData += featureValueData.startGetFv[i].toString() + ","
        }
        csvData += "stopGetFv,"
        for(i in 0 until featureValueData.stopGetFv.size){
            csvData += featureValueData.stopGetFv[i].toString() + ","
        }
        csvData += "\n"

        var fw = FileWriter(filePath, fileAppend)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }
    */

    // センサデータのcsv作成
    fun createSensorDataCsv(startDate: LocalDateTime, accData: AccData, graData: GraData, locData: LocData, barData: BarData): String{
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

        return filePath

    }


    // フォルダを再帰的に削除する関数
    private fun deleteFolderRecursively(file: File): Boolean {
        if (file.isDirectory) {
            val children = file.listFiles() ?: return false
            for (child in children) {
                deleteFolderRecursively(child)  // サブファイル/フォルダを再帰的に削除
            }
        }
        return file.delete()  // 最後にフォルダ自体を削除
    }

    // センサデータのフォルダを削除
    fun deleteSensorDataCsv(startDate: LocalDateTime){
        val fileName = createFileName(startDate)
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + fileName
        val f = File(filePath)
        if (f.exists()) {
            deleteFolderRecursively(f)
        }
    }

    fun createSensingAirPressure(dateList: List<String>, airPressureList: List<Int>){
        val filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/sensingAirPressure" + extension
        var csvData = ""
        val header = "date,airPressure(kPa)"
        csvData = header + "\n"

        for(i in dateList.indices){
            csvData += "${dateList[i]},${airPressureList[i]}\n"
        }
        var fw = FileWriter(filePath, false)
        var pw = PrintWriter(BufferedWriter(fw))
        pw.print(csvData)
        pw.close()

    }

}