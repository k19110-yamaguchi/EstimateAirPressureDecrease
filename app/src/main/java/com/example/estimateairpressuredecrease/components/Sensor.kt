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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.estimateairpressuredecrease.FontSize
import com.example.estimateairpressuredecrease.MainActivity
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.room.entities.FeatureValueData
import com.example.estimateairpressuredecrease.sensors.*
import com.example.estimateairpressuredecrease.ui.theme.element
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Sensor(
    acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric,
    spaceSize: Dp,
    fontSize: FontSize = FontSize(), viewModel: MainViewModel = hiltViewModel()
) {
    // センシング中の場合
    if (viewModel.isSensing) {
        // 最後に取得したセンサデータを表示
        SensorDataText(spaceSize)

        // 推定に必要なデータがあるか調べる
        viewModel.checkRequiredData()

        // 推定に必要なデータがある場合
        if (viewModel.isRequiredData) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = {
                    stopSensing(acc, gra, loc, bar, viewModel)
                }
            ) {
                Text(text = "測定終了", fontSize = fontSize.normal)
            }
        // 推定に必要なデータがない場合
        } else {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Black,
                    contentColor = Color.White
                ),
                onClick = {
                    Log.d("Sensor", "データが不十分です")
                }
            ) {
                Text(text = "測定終了", fontSize = fontSize.normal)
            }
        }
    // センシング外の場合
    } else {
        // 必要なデータがあるの場合
        if (viewModel.isRequiredData) {
            // 学習状態の場合
            if (viewModel.isTrainingState) {
                // 空気圧を入力
                viewModel.isInputtingAirPressure = true
            }
        // 必要なデータがない場合
        } else {
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
                    Text(text = "測定開始", fontSize = fontSize.normal)
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
            viewModel.xAccList.add(x.round(5))
            viewModel.yAccList.add(y.round(5))
            viewModel.zAccList.add(z.round(5))
            viewModel.accTime = t.round(2)
            viewModel.accTimeList.add(viewModel.accTime)
            Log.d("accTime", viewModel.accTime.toString())
        }
    })

    gra.startListening(object : Gravity.GravityListener {
        override fun onGravityChanged(x: Double, y: Double, z: Double, t: Double) {
            viewModel.xGraList.add(x.round(5))
            viewModel.yGraList.add(y.round(5))
            viewModel.zGraList.add(z.round(5))
            viewModel.graTime = t.round(2)
            viewModel.graTimeList.add(viewModel.graTime)
        }
    })

    loc.startListening(object : Location.LocationListener {
        override fun onLocationInfoChanged(lat: Double, lon: Double, t: Double) {
            viewModel.latList.add(lat.round(6))
            viewModel.lonList.add(lon.round(6))
            viewModel.locTime = t.round(2)
            viewModel.locTimeList.add(viewModel.locTime)
            Log.d("LocationTime", t.toString())
        }
    })

    bar.startListening(object : Barometric.BarListener {
        override fun onBarometricChanged(bar: Double, t: Double) {
            viewModel.barList.add(bar.round(1))
            viewModel.barTime = t.round(2)
            viewModel.barTimeList.add(viewModel.barTime)
        }
    })

}

fun stopSensing(acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric, viewModel: MainViewModel){
    if(!viewModel.isTrainingState){
        viewModel.addData()
    }
    viewModel.isSensing = false
    viewModel.stopDate = LocalDateTime.now()
    acc.stopListening()
    loc.stopListening()
    gra.stopListening()
    bar.stopListening()
    Log.d("stopSensing", "センシング終了2")


}

private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor

}





