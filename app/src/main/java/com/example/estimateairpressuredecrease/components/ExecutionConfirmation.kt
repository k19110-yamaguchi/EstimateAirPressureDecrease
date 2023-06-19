package com.example.estimateairpressuredecrease.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.room.entities.Home
import java.time.LocalDateTime

@Composable
fun executionConfirmation(viewModel: MainViewModel = hiltViewModel()) {
    val home by viewModel.home.collectAsState(initial = emptyList())

    viewModel.isFirstTime = home.isEmpty()

    Log.d("isFirstTime", viewModel.isFirstTime.toString())

    if(!viewModel.isFirstTime) {
        // データベースの値を挿入
        viewModel.isTrainingState = home[0].isTrainingState
        viewModel.minProperPressure = home[0].minProperPressure
        viewModel.inflatedDate = home[0].inflatedDate

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 状態
            Text(text = "status: ${viewModel.textStatus}", fontSize = 30.sp)
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    viewModel.isTrainingState = !viewModel.isTrainingState
                    viewModel.textStatus = showStatus(viewModel.textStatus)
                    Log.d("isTrainingState", viewModel.isTrainingState.toString())
                    Log.d("textStatus", viewModel.textStatus)
                    viewModel.updateHome()
                }
            ) {
                Text(text = "状態変更")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 最小適正空気圧
            Text(text = "最小適正空気圧: ${showMinProperPressure(viewModel.minProperPressure)}", fontSize = 30.sp)

            TextField(
                modifier = Modifier.width(240.dp),
                value = viewModel.textMinProperPressure,
                label = { Text("最小適正空気圧を入力(kPa)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                onValueChange = { viewModel.textMinProperPressure = it }
            )

            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    viewModel.minProperPressure = changeMinProperPressure(viewModel.textMinProperPressure)
                    viewModel.textMinProperPressure=""
                    viewModel.updateHome()
                }
            ) {
                Text(text = "保存")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 注入した日付
            Text(text = "空気注入日:${showInflatedDate(viewModel.inflatedDate)}", fontSize = 30.sp)
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    viewModel.inflatedDate = LocalDateTime.now()
                    viewModel.updateHome()
                }
            ) {
                Text(text = "空気を注入した!!")
            }
        }

    }else{

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Button(onClick = { viewModel.createHome() }) {
                Text(text = "アプリを使用する")
            }
        }
    }
}

fun showStatus(textStatus: String): String {
    var res = ""

    if (textStatus == "学習状態"){
        res = "推定状態"
    }else{
        res = "学習状態"
    }
    return res
}

fun showMinProperPressure(minProperPressure: Int): String {
    var res = ""

    if(minProperPressure != 0){
        res = minProperPressure.toString() + "kPa"
    }

    return res
}

fun showInflatedDate(inflatedDate: LocalDateTime): String{
    var res = ""

    if(inflatedDate != LocalDateTime.of(2000, 1, 1, 0, 0, 0)){
        res = "${inflatedDate.monthValue}月${inflatedDate.dayOfMonth}日"
    }

    return res
}

fun changeMinProperPressure(text: String): Int {
    var res = 0
    if(text != ""){
        res = Integer.parseInt(text)

    }

    return res
}





