package com.example.estimateairpressuredecrease.components

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.components.screen.Home
import com.example.estimateairpressuredecrease.components.screen.Input
import com.example.estimateairpressuredecrease.components.screen.datamanagement.DataManagement
import com.example.estimateairpressuredecrease.components.screen.sensing.Sensing
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Gps

@Composable
fun MainContent(
    acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric,
    viewModel: MainViewModel = hiltViewModel()
){
    val common = Common()
    // 初回起動か調べる
    viewModel.checkIsInitialization()

    when (viewModel.screenStatus) {
        // ホーム画面を表示
        common.homeNum -> {
            Home(viewModel)
        }
        // センシング画面を表示
        common.sensingNum -> {
            Sensing(acc, gra, loc, bar, viewModel)
        }
        // 入力画面を表示
        common.inputNum -> {
            Input(viewModel)
        }
        // データ管理画面を表示
        common.dataManagementNum -> {
            DataManagement(viewModel)
        }

        else -> {

        }
    }
}


