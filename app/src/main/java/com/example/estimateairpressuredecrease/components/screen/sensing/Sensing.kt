package com.example.estimateairpressuredecrease.components.screen.sensing

import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
fun Sensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel, common: Common = Common()) {
    Text(text = "センシング画面", fontSize = common.largeFont)

    Spacer(modifier = androidx.compose.ui.Modifier.height(common.space))

    // センサの値の表示
    SensorDataText(viewModel)

    // 推定に必要なデータがあるか調べる
    viewModel.checkRequiredData()
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

