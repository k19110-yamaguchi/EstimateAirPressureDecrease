package com.example.estimateairpressuredecrease.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.estimateairpressuredecrease.MainActivity
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.sensors.*
import com.example.estimateairpressuredecrease.ui.theme.element
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Sensor(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric,
           viewModel: MainViewModel = hiltViewModel()){

    val accData by viewModel.accData.collectAsState(initial = emptyList())
    val graData by viewModel.graData.collectAsState(initial = emptyList())
    val locData by viewModel.locData.collectAsState(initial = emptyList())
    val barData by viewModel.barData.collectAsState(initial = emptyList())
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())
    val featureValueData by viewModel.featureValueData.collectAsState(initial = emptyList())

    Test(featureValueData)

    // センシング中の場合
    if (viewModel.isSensing){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // 最後に取得したセンサデータを表示
            SensorDataText(viewModel = viewModel)

            // 推定に必要なデータがあるか調べる
            viewModel.checkRequiredData()

            // 推定に必要なデータがある場合
            if(viewModel.isRequiredData){
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = element,
                        contentColor = Color.White
                    ),
                    onClick = {
                        stopSensing(acc, gra, loc, bar, viewModel)
                    }) {
                    Text(text = "測定終了", fontSize = 30.sp)
                }

            // 推定に必要なデータがない場合
            }else{
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {
                        Log.d("Sensor", "データが不十分です")
                    }) {
                    Text(text = "測定終了", fontSize = 30.sp)
                }
            }
        }

    // センシング外の場合
    }else{
        // 必要なデータがあり、空気圧が未入力の場合
        if(viewModel.isRequiredData){
            // 空気圧を入力する画面
            InputAirPressure(viewModel)

        // 空気圧の入力が終了した場合
        }else{
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = element,
                        contentColor = Color.White
                    ),
                    onClick = {
                        startSensing(acc, gra, loc, bar, viewModel)
                    }) {
                    Text(text = "測定開始", fontSize = 30.sp)
                }
            }
        }
    }
}

fun startSensing(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric, viewModel: MainViewModel){
    viewModel.isSensing = true
    viewModel.startDate = LocalDateTime.now()
    acc.startListening(object : Accelerometer.AccListener {
        override fun onAccelerationChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xAccList.add(x)
            viewModel.yAccList.add(y)
            viewModel.zAccList.add(z)
            viewModel.accTime = t
            viewModel.accTimeList.add(viewModel.accTime)
        }
    })

    gra.startListening(object : Gravity.GravityListener {
        override fun onGravityChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xGraList.add(x)
            viewModel.yGraList.add(y)
            viewModel.zGraList.add(z)
            viewModel.graTime = t
            viewModel.graTimeList.add(viewModel.graTime)
        }
    })

    loc.startListening(object : Location.LocationListener {
        override fun onLocationInfoChanged(lat: Double, lon: Double, t: Double) {
            viewModel.latList.add(lat)
            viewModel.lonList.add(lon)
            viewModel.locTime = t
            viewModel.locTimeList.add(viewModel.locTime)
            Log.d("LocationTime", t.toString())
        }
    })

    bar.startListening(object : Barometric.BarListener {
        override fun onBarometricChanged(bar: Double, t: Double) {
            viewModel.barList.add(bar)
            viewModel.barTime = t
            viewModel.barTimeList.add(viewModel.barTime)
        }
    })

}

fun stopSensing(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric, viewModel: MainViewModel){
    viewModel.isSensing = false
    viewModel.stopDate = LocalDateTime.now()
    acc.stopListening()
    loc.stopListening()
    gra.stopListening()
    bar.stopListening()

}








