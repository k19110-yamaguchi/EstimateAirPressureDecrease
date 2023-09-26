package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun InputAirPressure(viewModel: MainViewModel){
    Column(verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            // 入力欄
            TextField(
                modifier = Modifier
                    .width(240.dp)
                    .background(element),
                value = if (viewModel.airPressure == 0) "" else viewModel.airPressure.toString(),
                label = { Text("現在の空気圧(kPa)を入力", fontSize = 20.sp, color = Color.White) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                onValueChange = {
                    try {
                        viewModel.airPressure = it.toInt()
                    } catch (e: NumberFormatException) {
                        viewModel.airPressure = 0
                    }
                }
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = {
                    viewModel.checkAirPressure()

                }) {
                Text(text = "入力完了", fontSize = 30.sp)
            }
        }
    }

}