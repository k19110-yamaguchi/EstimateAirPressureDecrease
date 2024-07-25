package com.example.estimateairpressuredecrease.components.screen.sensing

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.ui.theme.element
import java.time.LocalDateTime

@Composable
fun Sensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel) {
    val common = Common()
    // 推定に必要なデータがあるか調べる
    viewModel.checkRequiredData()

    // 表示
    Box(modifier = Modifier.fillMaxSize()){
        // トップ
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(common.space))
            Text(text = "センシング画面", fontSize = common.largeFont)
        }

        // センター
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            // センサの値の表示
            SensorDataText(viewModel)
        }

        // ボトム
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            // データが十分な可能性がある場合
            if (viewModel.isRequiredData) {
                // 終了ボタン
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = element,
                        contentColor = Color.White
                    ),
                    onClick = {
                        stopSensing(acc, gra, loc, bar, viewModel)
                        viewModel.screenStatus = common.inputNum
                        viewModel.inputStatus = common.inputPressureNum
                    }) {
                    Text(text = "測定終了", fontSize = common.normalFont)
                }
            // データが不十分な場合
            }else{
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        Log.d("Sensor", "データが不十分です")
                    }
                ) {
                    Text(text = "測定終了", fontSize = common.normalFont)
                }
            }
            Spacer(modifier = Modifier.height(common.space))
        }
    }
}

// センシングを終了
fun stopSensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel){
    // 現在時刻(終了)の取得
    viewModel.stopDate = LocalDateTime.now()
    // センサーの停止
    acc.stopListening()
    loc.stopListening()
    gra.stopListening()
    bar.stopListening()
}

