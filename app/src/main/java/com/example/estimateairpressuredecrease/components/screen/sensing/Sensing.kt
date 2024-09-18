package com.example.estimateairpressuredecrease.components.screen.sensing

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Sensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel) {
    val common = Common()
    // 推定に必要なデータがあるか調べる
    viewModel.checkRequiredData()
    if (viewModel.isSensingInit){
        startSensing(acc, gra, loc, bar, viewModel)
        viewModel.isSensingInit = false

    }

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

// センシングを開始
// todo: startSensingとstartSensing2をまとめる
fun startSensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel){
    val common = Common()
    // 現在時刻(開始)を取得
    viewModel.startDate = LocalDateTime.now()
    var isEnableGettingLoc = false
    var count = 1

    // 位置情報を取得
    loc.startListening(object : Gps.LocationListener {
        override fun onLocationInfoChanged(lat: Double, lon: Double, t: Double) {
            if(t >= 5){
                val prevLat = viewModel.lat
                val prevLon = viewModel.lon
                val prevTime = viewModel.locTime
                viewModel.lat = lat.round(6)
                viewModel.lon = lon.round(6)
                viewModel.locTime = t.round(2)-5.0

                viewModel.latList.add(viewModel.lat)
                viewModel.lonList.add(viewModel.lon)
                viewModel.locTimeList.add(viewModel.locTime)

                // 距離，速度を求める
                Log.d("LocationTime", viewModel.locTime.toString())
                val size = viewModel.locTimeList.size
                if(count == 1 && size == 1){
                    viewModel.disList.add(0.0)
                    viewModel.speedList.add(0.0)
                }else{
                    viewModel.dis = getDis(prevLat, prevLon, viewModel.lat, viewModel.lon)
                    viewModel.speed = getSpeed(viewModel.dis, prevTime, viewModel.locTime)
                    viewModel.disList.add(viewModel.dis)
                    viewModel.speedList.add(viewModel.speed)
                }

                Log.d("Distance", viewModel.dis.toString())

                if(viewModel.locTime >= 0.0 && !isEnableGettingLoc){
                    startSensing2(acc, gra, bar, viewModel, common)
                    isEnableGettingLoc = true

                }

                if(viewModel.locTimeList.last() > viewModel.saveTime*count){
                    viewModel.addSensorData()
                    common.log("${count}:　センサデータを追記")
                    count++

                }

            }
        }
    })
}

private fun startSensing2(acc: Accelerometer, gra: Gravity, bar: Barometric, viewModel: MainViewModel, common: Common){
    // 加速度を取得
    acc.startListening(object : Accelerometer.AccListener {
        override fun onAccelerationChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xAcc = x.round(5)
            viewModel.yAcc = y.round(5)
            viewModel.zAcc = z.round(5)
            viewModel.accTime = t.round(2)

            viewModel.xAccList.add(viewModel.xAcc)
            viewModel.yAccList.add(viewModel.yAcc)
            viewModel.zAccList.add(viewModel.zAcc)
            viewModel.accTimeList.add(viewModel.accTime)

        }
    })

    // 重力加速度を取得
    gra.startListening(object : Gravity.GravityListener {
        override fun onGravityChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xGra = x.round(5)
            viewModel.yGra = y.round(5)
            viewModel.zGra = z.round(5)
            viewModel.graTime = t.round(2)

            viewModel.xGraList.add(viewModel.xGra)
            viewModel.yGraList.add(viewModel.yGra)
            viewModel.zGraList.add(viewModel.zGra)
            viewModel.graTimeList.add(viewModel.graTime)
        }
    })

    // 気圧を取得
    bar.startListening(object : Barometric.BarListener {
        override fun onBarometricChanged(bar: Double, t: Double) {
            viewModel.bar = bar.round(1)
            viewModel.barTime = t.round(2)

            viewModel.barList.add(bar.round(1))
            viewModel.barTimeList.add(viewModel.barTime)
        }
    })
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

// 距離を取得
private fun getDis(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double{
    val dis = FloatArray(3)
    android.location.Location.distanceBetween(startLat, startLon, endLat, endLon, dis)
    return (dis[0] * 0.001).round(5)
}

// 速度を取得
private fun getSpeed(dis: Double, startTime: Double, endTime: Double): Double {
    return (dis / (endTime - startTime) * 3600).round(1)
}

// 四捨五入を行う関数
private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}



