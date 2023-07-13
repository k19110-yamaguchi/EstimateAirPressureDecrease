package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.ui.theme.element


@Composable
fun Home (viewModel: MainViewModel = hiltViewModel()) {
    if (viewModel.isHome){
        Text(text = "ホーム画面")
    }else{
        Text(text = "設定画面")

    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly

            //horizontalArrangement = Alignment.CenterHorizontally,
        ) {
            // Homeボタン
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = { viewModel.isHome  = true }
            ) {
                Text(text = "ホーム画面")

            }

            // 設定ボタン
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = { viewModel.isHome = false }
            ) {
                Text(text = "設定画面")

            }
        }
    }

}