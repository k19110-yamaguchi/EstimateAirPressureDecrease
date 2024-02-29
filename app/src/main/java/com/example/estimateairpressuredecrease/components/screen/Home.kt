package com.example.estimateairpressuredecrease.components.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel

@Composable
fun Home(viewModel: MainViewModel, common:Common = Common()) {
    common.log("ホーム画面")
    Text(text = "ホーム画面", fontSize = common.largeFont)

    Spacer(modifier = Modifier.height(common.space))

    // ホーム画面の情報を取得
    val homeData by viewModel.homeData.collectAsState(initial = emptyList())
    val featureValueData by viewModel.featureValueData.collectAsState(initial = emptyList())
    if(homeData.isNotEmpty()){
        viewModel.setHome(homeData[0])
    }

    // 学習→推定状態に移るかどうか
    viewModel.checkState(featureValueData)

    // 状態の表示
    if(viewModel.isTrainingState) {
        common.log("学習状態")
        Text(text = "学習状態", fontSize = common.largeFont)
    } else {
        common.log("推定状態")
        Text(text = "推定状態", fontSize = common.largeFont)
        // todo 推定結果を表示
    }

    Spacer(modifier = Modifier.height(common.space))

    // 最小適正空気圧表示、変更
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "最小適正空気圧: ${viewModel.minProperPressure}kPa", fontSize = common.smallFont)

        Spacer(modifier = Modifier.width(common.space))

        Button(onClick = {
            viewModel.screenStatus = common.inputNum
            viewModel.inputStatus = common.inputProperPressureNum
        }) {
            Text(text = "変更", fontSize = common.smallFont)
        }
    }

    Spacer(modifier = Modifier.height(common.space))

    Button(onClick = {viewModel.screenStatus = common.sensingNum}) {
        Text(text = "センシングへ", fontSize = common.largeFont)
    }
}