package com.example.estimateairpressuredecrease.components.screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun DataManagement(viewModel: MainViewModel){
    var common = Common()
    Text(text = "データ管理画面", fontSize = common.largeFont)
    Spacer(modifier = Modifier.height(common.space))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
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
    }


}