package com.example.estimateairpressuredecrease.components.screen.datamanagement

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
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun DataManagement(viewModel: MainViewModel){
    val common = Common()
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())

    // 画面表示
    Box(modifier = Modifier.fillMaxSize()){
        // トップ
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Spacer(modifier = Modifier.height(common.space))
            Text(text = "データ管理画面", fontSize = common.largeFont)
        }

        // センター
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            if (sensorData.isNotEmpty()) {
                ShowDataList(sensorData, viewModel)
            }
        }

        // ボトム
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(text = viewModel.dataManagementMessage)
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = {
                    viewModel.screenStatus = common.homeNum
                }) {
                Text(text = "ホーム画面", fontSize = common.smallFont)
            }

            Spacer(modifier = Modifier.height(common.space))
        }
    }
}