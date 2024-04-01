package com.example.estimateairpressuredecrease.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.components.screen.Home
import com.example.estimateairpressuredecrease.components.screen.Input
import com.example.estimateairpressuredecrease.components.screen.sensing.Sensing
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Location

@Composable
fun MainContent(
    acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric,
    viewModel: MainViewModel = hiltViewModel(), common: Common = Common()
){

    // 初回起動か調べる
    viewModel.checkIsInitialization()

    when (viewModel.screenStatus) {
        // ホーム画面を表示
        common.homeNum -> {
            Home(acc, gra, loc, bar, viewModel)
        }

        // センシング画面を表示
        common.sensingNum -> {
            Sensing(acc, gra, loc, bar, viewModel)
        }

        // 入力画面を表示
        common.inputNum -> {
            Input(viewModel)
        }

        else -> {

        }
    }
}


