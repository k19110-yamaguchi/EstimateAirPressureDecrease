package com.example.estimateairpressuredecrease.components.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel

@Composable
fun Sensing(viewModel: MainViewModel, common: Common = Common()) {
    common.log("センシング画面")
    Text(text = "センシング画面", fontSize = common.largeFont)
    Spacer(modifier = androidx.compose.ui.Modifier.height(common.space))

    Button(onClick = {
        viewModel.screenStatus = common.inputNum
        viewModel.inputStatus = common.inputPressureNum
    }) {
        Text(text = "測定空気圧入力へ")
    }
}