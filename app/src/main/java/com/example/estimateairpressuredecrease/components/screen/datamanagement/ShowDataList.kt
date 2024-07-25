package com.example.estimateairpressuredecrease.components.screen.datamanagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.room.entities.SensorData
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun ShowDataList(sensorData: List<SensorData>, viewModel: MainViewModel){
    val common = Common()
    LazyColumn(modifier = Modifier.height(500.dp)){
        items(sensorData){sd ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = sd.startDate.toString(), fontSize = common.smallFont)

                Spacer(modifier = Modifier.width(common.space))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = element,
                        contentColor = Color.White
                    ),
                    onClick = {
                        viewModel.deleteSensorData(sd)
                    }) {
                    Text(text = "削除", fontSize = common.smallFont)
                }
            }
        }
    }
}