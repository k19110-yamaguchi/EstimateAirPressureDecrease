package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.ui.theme.background
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun Sensor(acc: Accelerometer, gps: Gps, gra: Gravity, bar: Barometric,
           viewModel: MainViewModel = hiltViewModel()){
    // センシング中の場合
    if (viewModel.isSensing){
        acc.startListening(object : Accelerometer.AccListener {
            override fun onAccelerationChanged(x: Double, y: Double, z: Double) {
                viewModel.xAcc = x
                viewModel.yAcc = y
                viewModel.zAcc = z
            }
        })

        gps.startListening(object: Gps.LocationListener {
            override fun onLocationChanged(lat: Double, lon: Double) {
                viewModel.lat = lat
                viewModel.lon = lon
            }
        })

        gra.startListening(object : Gravity.GravityListener {
            override fun onGravityChanged(x: Double, y: Double, z: Double) {
                viewModel.xGra = x
                viewModel.yGra = y
                viewModel.zGra = z
            }
        })

        bar.startListening(object : Barometric.BarListener {
            override fun onBarometricChanged(bar: Double) {
                viewModel.bar = bar
            }
        })



    // センシング外の場合
    }else{
        acc.stopListening()
        gps.stopListening()
        gra.stopListening()
        bar.stopListening()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if(viewModel.isSensing){
            Text(text = "センシング中" , fontSize = 30.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "x軸加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.xAcc.toString() , fontSize = 20.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "y軸加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.yAcc.toString() , fontSize = 20.sp)

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "z軸加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.zAcc.toString() , fontSize = 20.sp)

            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "x軸重力加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.xGra.toString() , fontSize = 20.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "y軸重力加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.yGra.toString() , fontSize = 20.sp)

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "z軸重力加速度(m/s):", fontSize = 20.sp)
                Text(text = viewModel.zGra.toString() , fontSize = 20.sp)

            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "緯度:", fontSize = 20.sp)
                Text(text = viewModel.lat.toString() , fontSize = 20.sp)

            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "経度:", fontSize = 20.sp)
                Text(text = viewModel.lon.toString() , fontSize = 20.sp)

            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "気圧:", fontSize = 20.sp)
                Text(text = viewModel.bar.toString() , fontSize = 20.sp)

            }

        }else{

        }

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = element,
                contentColor = Color.White
            ),
            onClick = {
            viewModel.isSensing = !viewModel.isSensing
        }) {
            Text(text = if(viewModel.isSensing) "測定終了" else "測定開始", fontSize = 30.sp)
        }
    }
}