package com.example.estimateairpressuredecrease.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.OpenCsv
import com.example.estimateairpressuredecrease.room.entities.AccData
import com.example.estimateairpressuredecrease.room.entities.FeatureValueData
import com.example.estimateairpressuredecrease.room.entities.LocData
import com.example.estimateairpressuredecrease.room.entities.SensorData
import com.example.estimateairpressuredecrease.ui.theme.element
import java.time.LocalDateTime

@Composable
fun Test(sensorData: List<SensorData>,
         accData: List<AccData>,
         locData: List<LocData>,
         featureValueData: List<FeatureValueData>,
         viewModel: MainViewModel = hiltViewModel()){

    Column() {

        Text(text = "データ数: ${featureValueData.size}")
        Text(text = "必要特徴量サイズ: ${viewModel.requiredFvSize}")
        Text(text = "範囲内: ${viewModel.withinSize} 範囲外: ${viewModel.outOfSize}")
        Log.d("Test", viewModel.estimatedAirPressure.toString())

    }
}
