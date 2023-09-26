package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.room.entities.AccData
import com.example.estimateairpressuredecrease.room.entities.FeatureValueData
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun Test(featureValueData: List<FeatureValueData>, viewModel: MainViewModel = hiltViewModel()){
    Column() {
        Text(text = "データ数: ${featureValueData.size}")
        if(featureValueData.isNotEmpty()){
            Text(text = "加速度標準偏差: " + featureValueData[0].accSd.toString())
        }

        Button(
            colors = ButtonDefaults.buttonColors(
                backgroundColor = element,
                contentColor = Color.White
            ),
            onClick = { viewModel.runPython() }) {
            Text(text = "python実行")
        }
    }
}