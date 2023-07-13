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
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.estimateairpressuredecrease.MainActivity
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.ui.theme.element
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Sensor(acc: Accelerometer, gps: Gps, gra: Gravity, bar: Barometric,
           viewModel: MainViewModel = hiltViewModel()){

    val accData by viewModel.accData.collectAsState(initial = emptyList())
    val graData by viewModel.graData.collectAsState(initial = emptyList())
    val locData by viewModel.locData.collectAsState(initial = emptyList())
    val barData by viewModel.barData.collectAsState(initial = emptyList())
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())


    Column() {
        Text(text = "データ数: ${accData.size}")
        Text(text = "データ取得: ${viewModel.csvData}")

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = element,
                contentColor = Color.White
            ),
            onClick = { viewModel.csvData = getCsv() }) {
            Text(text = "ファイルから取得")

        }

        
    }
    

    if(accData.isNotEmpty() && graData.isNotEmpty() && locData.isNotEmpty() && barData.isNotEmpty() && sensorData.isNotEmpty()){
        Log.d("acc", accData[0].id.toString())

    }

    Log.d("gra", graData.isNotEmpty().toString())

    Log.d("loc", locData.isNotEmpty().toString())

    Log.d("bar", barData.isNotEmpty().toString())

    Log.d("sensor", sensorData.isNotEmpty().toString())

    // センシング中の場合
    if (viewModel.isSensing){
        if(viewModel.startDate == viewModel.initDate){
            viewModel.startDate = LocalDateTime.now()
        }

        Log.d("Acc", "start")
        acc.startListening(object : Accelerometer.AccListener {
            override fun onAccelerationChanged(x: Double, y: Double, z: Double, t: Double) {
                viewModel.xAccList.add(x)
                viewModel.yAccList.add(y)
                viewModel.zAccList.add(z)
                viewModel.accTime = t
                //Log.d("Acc", t.toString())
                viewModel.accTimeList.add(viewModel.accTime)
                Log.d("time", viewModel.accTimeList.last().toString())
            }

        })

        gps.startListening(object: Gps.LocationListener {
            override fun onLocationChanged(lat: Double, lon: Double, t: Double) {
                viewModel.latList.add(lat)
                viewModel.lonList.add(lon)
                viewModel.locTime = t
                viewModel.locTimeList.add(viewModel.locTime)
                //Log.d("Loc", t.toString())
            }
        })

        gra.startListening(object : Gravity.GravityListener {
            override fun onGravityChanged(x: Double, y: Double, z: Double, t: Double) {
                viewModel.xGraList.add(x)
                viewModel.yGraList.add(y)
                viewModel.zGraList.add(z)
                viewModel.graTime = t
                // Log.d("Gra", t.toString())
                viewModel.graTimeList.add(viewModel.graTime)
            }
        })

        bar.startListening(object : Barometric.BarListener {
            override fun onBarometricChanged(bar: Double, t: Double) {
                viewModel.barList.add(bar)
                viewModel.barTime = t
                // Log.d("Bar", t.toString())
                viewModel.barTimeList.add(viewModel.barTime)
            }
        })

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Log.d("Acc", "${viewModel.accTimeList}")
            if (viewModel.accTime != -1.0) {
                Text(text = "センシング中${viewModel.accTimeList.last().round(2)}", fontSize = 30.sp)
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

            Log.d("Gra", "${viewModel.graTimeList}")
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

            Log.d("Loc", "${viewModel.locTimeList}")
            if (viewModel.locTime != -1.0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "緯度:", fontSize = 20.sp)
                    Text(text = viewModel.latList.last().round(2).toString(), fontSize = 20.sp)

                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "経度:", fontSize = 20.sp)
                    Text(text = viewModel.lonList.last().round(2).toString(), fontSize = 20.sp)

                }

            }else{
                Text(text = "位置情報未取得", color = Color.Red, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(30.dp))

            Log.d("Bra", "${viewModel.barTimeList}")
            if (viewModel.barTime != -1.0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "気圧:", fontSize = 20.sp)
                    Text(text = viewModel.barList.last().round(2).toString(), fontSize = 20.sp)

                }
                Spacer(modifier = Modifier.height(30.dp))
            }else{
                Text(text = "気圧未取得", color = Color.Red, fontSize = 20.sp)
            }


            if(viewModel.accTime != -1.0 &&
                viewModel.graTime != -1.0 &&
                viewModel.locTime != -1.0 &&
                viewModel.barTime != -1.0){
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = element,
                        contentColor = Color.White
                    ),
                    onClick = {
                        viewModel.isSensing = !viewModel.isSensing
                    }) {
                    Text(text = "測定終了", fontSize = 30.sp)
                }
            }else{
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Black,
                        contentColor = Color.White
                    ),
                    onClick = {

                    }) {
                    Text(text = "測定終了", fontSize = 30.sp)
                }

            }
        }

    // センシング外の場合
    }else{
        if(viewModel.accTimeList.isNotEmpty() and
            viewModel.graTimeList.isNotEmpty() and
            viewModel.locTimeList.isNotEmpty() and
            viewModel.barTimeList.isNotEmpty()
        ){
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if(viewModel.stopDate == viewModel.initDate){
                    viewModel.stopDate = LocalDateTime.now()
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    // 入力欄
                    TextField(
                        modifier = Modifier
                            .width(240.dp)
                            .background(element),
                        value = if (viewModel.airPressure == 0) "" else viewModel.airPressure.toString(),
                        label = { Text("現在の空気圧(kPa)を入力", fontSize = 20.sp, color = Color.White)},
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                        onValueChange = {
                            try {
                                viewModel.airPressure = it.toInt()
                            } catch (e: NumberFormatException) {
                                viewModel.airPressure = 0
                            }
                        }
                    )

                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = element,
                            contentColor = Color.White
                        ),
                        onClick = {
                            viewModel.addData()

                        }) {
                        Text(text = "入力完了", fontSize = 30.sp)
                    }
                }
            }
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
                        viewModel.isSensing = !viewModel.isSensing
                    }) {
                    Text(text = "測定開始", fontSize = 30.sp)
                }

            }
        }
        acc.stopListening()
        gps.stopListening()
        gra.stopListening()
        bar.stopListening()
    }
}

private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}

private fun getRandNum(): Double {
    // Pythonコードを実行する前にPython.start()の呼び出しが必要
    if (!Python.isStarted()) {
        Python.start(AndroidPlatform(MainActivity.instance))
    }
    val py = Python.getInstance()
    val module = py.getModule("test") // スクリプト名
    return module.callAttr("create_random_number").toDouble()

}

private fun getCsv(): String {
    // Pythonコードを実行する前にPython.start()の呼び出しが必要
    if (!Python.isStarted()) {
        Python.start(AndroidPlatform(MainActivity.instance))
    }
    val py = Python.getInstance()
    val module = py.getModule("test") // スクリプト名
    return module.callAttr("getCsv").toString()
}

