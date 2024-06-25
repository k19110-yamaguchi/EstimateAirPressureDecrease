package com.example.estimateairpressuredecrease.components.screen.sensing

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel

@Composable
fun SensorDataText(viewModel: MainViewModel, common: Common = Common()){
    if (viewModel.accTime != -1.0) {
        Text(text = "センシング中 ${viewModel.accTime.toInt()}s", fontSize = 30.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.xAcc), fontSize = common.smallFont)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.yAcc), fontSize = common.smallFont)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.zAcc), fontSize = common.smallFont)

        }

    }else{
        Text(text = "加速度未取得", color = Color.Red, fontSize = common.smallFont)
    }

    Spacer(modifier = Modifier.height(common.space))

    if (viewModel.graTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "x軸重力加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.xGra), fontSize = common.smallFont)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "y軸重力加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.yGra), fontSize = common.smallFont)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "z軸重力加速度(m/s):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.zGra), fontSize = common.smallFont)

        }
    }else{
        Text(text = "重力加速度未取得", color = Color.Red, fontSize = common.smallFont)
    }

    Spacer(modifier = Modifier.height(common.space))

    if (viewModel.locTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "緯度:", fontSize = 20.sp)
            Text(text = String.format("%.6f", viewModel.lat), fontSize = common.smallFont)

        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "経度:", fontSize = common.smallFont)
            Text(text = String.format("%.6f", viewModel.lon), fontSize = common.smallFont)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "距離(km):", fontSize = common.smallFont)
            Text(text = String.format("%.5f", viewModel.dis), fontSize = common.smallFont)

        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "速度(km/h):", fontSize = common.smallFont)
            Text(text = String.format("%.6f", viewModel.speed), fontSize = common.smallFont)

        }

    }else{
        Text(text = "位置情報未取得", color = Color.Red, fontSize = common.smallFont)
    }

    Spacer(modifier = Modifier.height(common.space))

    if (viewModel.barTime != -1.0) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "気圧:", fontSize = common.smallFont)
            Text(text = String.format("%.1f", viewModel.bar), fontSize = common.smallFont)

        }
        Spacer(modifier = Modifier.height(common.space))
    }else{
        Text(text = "気圧未取得", color = Color.Red, fontSize = common.smallFont)
    }


}

