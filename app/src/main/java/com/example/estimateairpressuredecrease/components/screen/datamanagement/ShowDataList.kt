package com.example.estimateairpressuredecrease.components.screen.datamanagement

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
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
import com.example.estimateairpressuredecrease.ui.theme.element


@Composable
fun ShowDataList(sensorData: List<SensorData>){
    val common = Common()
    LazyColumn{
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
                        /*TODO*/
                    }) {
                    Text(text = "削除", fontSize = common.smallFont)
                }
            }
        }
    }
}