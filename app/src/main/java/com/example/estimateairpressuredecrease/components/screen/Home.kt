package com.example.estimateairpressuredecrease.components.screen

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.example.estimateairpressuredecrease.sensors.Location
import com.example.estimateairpressuredecrease.ui.theme.element
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Home(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric, viewModel: MainViewModel, common:Common = Common()) {
    Text(text = "ホーム画面", fontSize = common.largeFont)

    Spacer(modifier = Modifier.height(common.space))

    // ホーム画面の情報を取得
    val homeData by viewModel.homeData.collectAsState(initial = emptyList())
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())
    val featureValueData by viewModel.featureValueData.collectAsState(initial = emptyList())
    if(homeData.isNotEmpty()){
        viewModel.setHome(homeData[0])
        // 学習→推定状態に移るかどうか
        if(viewModel.isTrainingState){
            // 特徴量の取得
            if(featureValueData.isNotEmpty()){
                viewModel.fv = featureValueData
                var count = 0
                for(i in viewModel.fv){
                    if(i.airPressure > viewModel.minProperPressure){
                        count++
                    }
                }
                common.log(viewModel.fv[0].airPressure.toString())
                Text(text = "適正内の特徴量データ数:${count}")
                Text(text = "適正外の特徴量データ数:${viewModel.fv.size-count}")
            }
            viewModel.checkState(featureValueData)
        }

        // 状態の表示
        if(viewModel.isTrainingState) {
            Text(text = "学習状態", fontSize = common.largeFont)
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
private fun startSensing(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric, viewModel: MainViewModel, common: Common = Common()){
    // 現在時刻(開始)を取得
    viewModel.startDate = LocalDateTime.now()

    // 加速度を取得
    acc.startListening(object : Accelerometer.AccListener {
        override fun onAccelerationChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xAccList.add(x.round(5))
            viewModel.yAccList.add(y.round(5))
            viewModel.zAccList.add(z.round(5))
            viewModel.accTime = t.round(2)
            viewModel.accTimeList.add(viewModel.accTime)
            Log.d("accTime", viewModel.accTime.toString())
        }
    })

    // 重力加速度を取得
    gra.startListening(object : Gravity.GravityListener {
        override fun onGravityChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xGraList.add(x.round(5))
            viewModel.yGraList.add(y.round(5))
            viewModel.zGraList.add(z.round(5))
            viewModel.graTime = t.round(2)
            viewModel.graTimeList.add(viewModel.graTime)
        }
    })

    // 位置情報を取得
    loc.startListening(object : Location.LocationListener {
        override fun onLocationInfoChanged(lat: Double, lon: Double, t: Double) {
            viewModel.latList.add(lat.round(6))
            viewModel.lonList.add(lon.round(6))
            viewModel.locTime = t.round(2)
            viewModel.locTimeList.add(viewModel.locTime)
            Log.d("LocationTime", t.toString())
        }
    })

    // 気圧を取得
    bar.startListening(object : Barometric.BarListener {
        override fun onBarometricChanged(bar: Double, t: Double) {
            viewModel.barList.add(bar.round(1))
            viewModel.barTime = t.round(2)
            viewModel.barTimeList.add(viewModel.barTime)
        }
    })
}

// 四捨五入を行う関数
private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}
