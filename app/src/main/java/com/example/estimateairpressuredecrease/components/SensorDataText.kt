package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.FontSize
import com.example.estimateairpressuredecrease.MainViewModel
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun SensorDataText(spaceSize: Dp, fontSize: FontSize = FontSize(), viewModel: MainViewModel = hiltViewModel()){
    if (viewModel.accTime != -1.0) {
        Text(text = "センシング中 ${viewModel.accTimeList.last().toInt()}s", fontSize = 30.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.xAccList.last()), fontSize = fontSize.small)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.yAccList.last()), fontSize = fontSize.small)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.zAccList.last()), fontSize = fontSize.small)

        }

    }else{
        Text(text = "加速度未取得", color = Color.Red, fontSize = fontSize.small)
    }

    Spacer(modifier = Modifier.height(spaceSize))

    if (viewModel.graTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸重力加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.xGraList.last()), fontSize = fontSize.small)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸重力加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.xGraList.last()), fontSize = fontSize.small)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸重力加速度(m/s):", fontSize = fontSize.small)
            Text(text = String.format("%.5f", viewModel.zGraList.last()), fontSize = fontSize.small)

        }
    }else{
        Text(text = "重力加速度未取得", color = Color.Red, fontSize = fontSize.small)
    }

    Spacer(modifier = Modifier.height(spaceSize))

    if (viewModel.locTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "緯度:", fontSize = 20.sp)
            Text(text = String.format("%.6f", viewModel.latList.last()), fontSize = fontSize.small)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "経度:", fontSize = fontSize.small)
            Text(text = String.format("%.6f", viewModel.lonList.last()), fontSize = fontSize.small)

        }

    }else{
        Text(text = "位置情報未取得", color = Color.Red, fontSize = fontSize.small)
    }

    Spacer(modifier = Modifier.height(spaceSize))

    if (viewModel.barTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "気圧:", fontSize = fontSize.small)
            Text(text = String.format("%.1f", viewModel.barList.last()), fontSize = fontSize.small)

        }
        Spacer(modifier = Modifier.height(spaceSize))
    }else{
        Text(text = "気圧未取得", color = Color.Red, fontSize = fontSize.small)
    }


}

