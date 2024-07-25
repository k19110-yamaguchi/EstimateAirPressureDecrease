package com.example.estimateairpressuredecrease.components.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
fun Home(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel, common:Common = Common()) {

    Text(text = "適正内データ数： ${viewModel.withinSize}")
    Text(text = "適正外データ数： ${viewModel.outOfSize}")
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = element,
            contentColor = Color.White
        ),
        onClick = {
            viewModel.screenStatus = common.dataManagementNum
        }) {
        Text(text = "データ管理", fontSize = common.smallFont)
    }


    Spacer(modifier = Modifier.height(common.space))

    Text(text = "ホーム画面", fontSize = common.largeFont)

    Spacer(modifier = Modifier.height(common.space))

    // ホーム画面の情報を取得
    val homeData by viewModel.homeData.collectAsState(initial = emptyList())
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())


    if(homeData.isNotEmpty()){
        viewModel.setHome(homeData[0])
        if (sensorData.isNotEmpty()){
            viewModel.countWithinData(sensorData)
        }

        // 状態の表示
        if(viewModel.isTrainingState) {
            Text(text = "学習状態", fontSize = common.largeFont)
            // todo: 推定状態に移行できるかどうか
            //viewModel.checkIsEstState()

        } else {
            Text(text = "推定状態", fontSize = common.largeFont)
            if(sensorData.isNotEmpty()){
                // 推定結果の表示
                val estimatedAirPressureText = viewModel.showEstimatedAirPressure(sensorData)
                if(estimatedAirPressureText != ""){
                    Text(text = "最小適正空気圧: ${estimatedAirPressureText}kPa", fontSize = common.smallFont)
                }
                if(estimatedAirPressureText.toInt() < viewModel.minProperPressure){
                    Text(text = "空気を注入してください", fontSize = common.largeFont, color = Color.Red)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(common.space))

    // 空気注入時期
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(viewModel.inflatedDate == viewModel.initDate){
            Text(text = "空気を注入してください", fontSize = common.smallFont)
        }else{
            Text(text = "空気注入時期: ${viewModel.showInflateDate()}", fontSize = common.smallFont)
        }

        Spacer(modifier = Modifier.width(common.space))

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = element,
                contentColor = Color.White
            ),
            onClick = {
                viewModel.updateInflateDate()
            }) {
            Text(text = "空気\n注入", fontSize = common.smallFont)
        }
    }

    Spacer(modifier = Modifier.height(common.space))

    // 最小適正空気圧表示、変更
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "最小適正空気圧: ${viewModel.minProperPressure}kPa", fontSize = common.smallFont)

        Spacer(modifier = Modifier.width(common.space))

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = element,
                contentColor = Color.White
            ),
            onClick = {
            viewModel.screenStatus = common.inputNum
            viewModel.inputStatus = common.inputProperPressureNum
        }) {
            Text(text = "変更", fontSize = common.smallFont)
        }
    }

    Spacer(modifier = Modifier.height(common.space))

    Button(
        colors = ButtonDefaults.buttonColors(
        backgroundColor = element,
        contentColor = Color.White
        ),
        onClick = {
            startSensing(acc, gra, loc, bar, viewModel)
            viewModel.screenStatus = common.sensingNum
        }) {
        Text(text = "測定開始", fontSize = common.largeFont)
    }

    Text(text = viewModel.homeMessage, fontSize = common.smallFont)
}

// センシングを開始
private fun startSensing(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, viewModel: MainViewModel, common: Common = Common()){
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

fun startSensing2(acc: Accelerometer, gra: Gravity, bar: Barometric, viewModel: MainViewModel, common: Common = Common()){
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

private fun getDis(startLat: Double, startLon: Double, endLat: Double, endLon: Double): Double{
    val dis = FloatArray(3)
    android.location.Location.distanceBetween(startLat, startLon, endLat, endLon, dis)
    return (dis[0] * 0.001).round(5)
}

private fun getSpeed(dis: Double, startTime: Double, endTime: Double): Double {
    return (dis / (endTime - startTime) * 3600).round(1)
}

// 四捨五入を行う関数
private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}
