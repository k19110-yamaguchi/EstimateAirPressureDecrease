package com.example.estimateairpressuredecrease

import android.util.Log
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 共通で使う値、関数
class Common {
    // デバックタグ
    private val tag = "MY_LOG_TAG"
    // フォントサイズ
    val largeFont: TextUnit = 36.sp
    val normalFont: TextUnit = 28.sp
    val smallFont: TextUnit = 20.sp

    // テキストフィールドサイズ
    val textField = 300.dp

    // 余白サイズ
    val space = 40.dp

    // 画面番号
    val homeNum = 1
    val sensingNum = 2
    val inputNum = 3
    val dataManagementNum = 4

    // 入力画面番号
    val inputProperPressureNum = 1
    val inputPressureNum = 2

    // デバックの表示
    fun log(mes: String) {
        Log.d(tag, mes)
    }
}