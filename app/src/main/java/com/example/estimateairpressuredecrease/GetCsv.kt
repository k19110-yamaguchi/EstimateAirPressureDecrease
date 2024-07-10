package com.example.estimateairpressuredecrease

import android.content.Context


class GetCsv {
    val context: Context = MainActivity.content
    // 拡張子
    private val extension : String = ".csv"

    /*
    fun getAccDataCsv(filePath: String): AccData {
        val accDataCsv: AccData
        val lines = File("${filePath}/acc${extension}").bufferedReader().use { it.readLines() }

        for (line in lines) {
            val elements = line.split(",")
            for (element in elements) {
                println(element)
            }
        }

        return accDataCsv
    }

     */
}