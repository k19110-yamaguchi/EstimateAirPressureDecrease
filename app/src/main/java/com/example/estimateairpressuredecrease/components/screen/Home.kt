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
import com.example.estimateairpressuredecrease.OpenCsv
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Gps
import com.example.estimateairpressuredecrease.ui.theme.element
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun Home(viewModel: MainViewModel) {
    val common = Common()
    // ホーム画面の情報を取得
    val homeData by viewModel.homeData.collectAsState(initial = emptyList())
    if (homeData.isNotEmpty()){
        // 状態をセット
        viewModel.setHome(homeData[0])
    }

    // センサデータを取得
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())
    if (sensorData.isNotEmpty()){
        // 適正空気圧の範囲内，外のセンサデータの個数をカウント
        viewModel.countWithinData(sensorData)
        // センシングデータの日付リストを取得
        viewModel.getSensingData(sensorData)
        // todo: 推定状態に移行できるかどうか
        //viewModel.checkIsEstState()
    }


    // 画面表示
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // トップ
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(common.space))

            Text(text = "ホーム画面", fontSize = common.largeFont)

            if (sensorData.isNotEmpty()){

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

            }
        }

        // センター
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            // 状態の表示
            if(homeData.isNotEmpty()){
                if(viewModel.isTrainingState) {
                    Text(text = "学習状態", fontSize = common.largeFont)

                } else {
                    Text(text = "推定状態", fontSize = common.largeFont)
                    // todo 推定空気圧の取得，表示
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
        }


        // ボトム
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            // メッセージの表示
            Text(text = viewModel.homeMessage, fontSize = common.smallFont)

            // 測定開始ボタン
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = {
                    // todo: startSensing()をSensingに移動させる
                    // startSensing(acc, gra, loc, bar, viewModel)
                    viewModel.screenStatus = common.sensingNum
                }) {
                Text(text = "測定開始", fontSize = common.largeFont)
            }
            Spacer(modifier = Modifier.height(common.space))
        }
    }
}


// 四捨五入を行う関数
private fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}
