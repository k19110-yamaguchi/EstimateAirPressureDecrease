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
        Text(text = viewModel.errorData, color = Color.Red)
        Text(text = "データ数: ${featureValueData.size}")
        Log.d("Test", viewModel.estimatedAirPressure.toString())

        if(!viewModel.isTrainingState){
            // 危険かの判断
            if(0 < viewModel.estimatedAirPressure && viewModel.estimatedAirPressure < viewModel.minProperPressure){
                Text(text = "推定空気圧: " + viewModel.estimatedAirPressure.toString() + " 危険", fontSize = 20.sp)
            }else if(viewModel.minProperPressure <= viewModel.estimatedAirPressure){
                Text(text = "推定空気圧: " + viewModel.estimatedAirPressure.toString() + " 安全", fontSize = 20.sp)

            }
        }
    }
}
