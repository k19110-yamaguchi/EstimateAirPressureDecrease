package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estimateairpressuredecrease.MainViewModel
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun SensorDataText(viewModel: MainViewModel){
    if (viewModel.accTime != -1.0) {
        Text(text = "センシング中 ${viewModel.accTimeList.last().toInt()}s", fontSize = 30.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.xAccList.last().round(2).toString(), fontSize = 20.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.yAccList.last().round(2).toString(), fontSize = 20.sp)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.zAccList.last().round(2).toString(), fontSize = 20.sp)

        }

    }else{
        Text(text = "加速度未取得", color = Color.Red, fontSize = 20.sp)
    }

    Spacer(modifier = Modifier.height(30.dp))

    if (viewModel.graTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸重力加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.xGraList.last().round(2).toString(), fontSize = 20.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸重力加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.yGraList.last().round(2).toString(), fontSize = 20.sp)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸重力加速度(m/s):", fontSize = 20.sp)
            Text(text = viewModel.zGraList.last().round(2).toString(), fontSize = 20.sp)

        }
    }else{
        Text(text = "重力加速度未取得", color = Color.Red, fontSize = 20.sp)
    }

    Spacer(modifier = Modifier.height(30.dp))

    if (viewModel.locTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "緯度:", fontSize = 20.sp)
            Text(text = viewModel.latList.last().round(6).toString(), fontSize = 20.sp)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "経度:", fontSize = 20.sp)
            Text(text = viewModel.lonList.last().round(6).toString(), fontSize = 20.sp)

        }

    }else{
        Text(text = "位置情報未取得", color = Color.Red, fontSize = 20.sp)
    }

    Spacer(modifier = Modifier.height(30.dp))

    if (viewModel.barTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "気圧:", fontSize = 20.sp)
            Text(text = viewModel.barList.last().round(2).toString(), fontSize = 20.sp)

        }
        Spacer(modifier = Modifier.height(30.dp))
    }else{
        Text(text = "気圧未取得", color = Color.Red, fontSize = 20.sp)
    }


}

private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor

}
