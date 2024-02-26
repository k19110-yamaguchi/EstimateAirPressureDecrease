package com.example.estimateairpressuredecrease.components.input

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun InputPressure(viewModel: MainViewModel, common: Common = Common()){
    var labelText = ""

    when (viewModel.inputStatus){
        common.inputProperPressureNum -> {
            common.log("適正空気圧入力画面")
            Text(text = "適正空気圧入力画面", fontSize = common.largeFont)
            Spacer(modifier = Modifier.height(common.space))

            labelText = "適正空気圧(kPa)"
        }
        common.inputPressureNum -> {
            common.log("測定空気圧入力画面")
            Text(text = "測定空気圧入力画面", fontSize = common.largeFont)
            Spacer(modifier = Modifier.height(common.space))

            labelText = "測定空気圧(kPa)"
        }

        else -> {
            common.log("入力画面の遷移で異常")
        }
    }
    
    // エラーの表示
    Text(text = viewModel.errorInputAirPressure, fontSize = common.smallFont, color = Color.Red)

    // 空気圧入力欄
    OutlinedTextField(
        modifier = Modifier.width(common.textField),
        value = viewModel.editingAirPressure,
        label = { Text("${labelText}を入力", fontSize = common.smallFont, fontWeight = FontWeight.Bold, color = element) },
        textStyle = TextStyle(fontSize = common.normalFont, color = Color.Black, textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        onValueChange = {
            viewModel.editingAirPressure = it
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = element, // カーソルの色を設定
            focusedBorderColor = element, // フォーカスされた際の枠の色を設定
            unfocusedBorderColor = element // フォーカスが外れた際の枠の色を設定
        )
    )

    Button(onClick = {
        common.log("${labelText}: ${viewModel.editingAirPressure}")
        viewModel.inputAirPressure()
    }) {
        Text(text = "入力完了", fontSize = common.normalFont)

    }

    // 適正空気圧入力画面の場合
    if(viewModel.inputStatus == common.inputProperPressureNum){
        // 体重(kg)
        OutlinedTextField(
            modifier = Modifier.width(common.textField),
            value = viewModel.editingBodyWeight,
            label = { Text("体重(kg)", fontSize = common.smallFont, fontWeight = FontWeight.Bold, color = element) },
            textStyle = TextStyle(fontSize = common.normalFont, color = Color.Black, textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            onValueChange = {
                viewModel.editingBodyWeight = it
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = element, // カーソルの色を設定
                focusedBorderColor = element, // フォーカスされた際の枠の色を設定
                unfocusedBorderColor = element // フォーカスが外れた際の枠の色を設定
            )
        )

        // 自転車質量(kg)
        OutlinedTextField(
            modifier = Modifier.width(common.textField),
            value = viewModel.editingBicycleWeight,
            label = { Text("自転車質量(kg)", fontSize = common.smallFont, fontWeight = FontWeight.Bold, color = element) },
            textStyle = TextStyle(fontSize = common.normalFont, color = Color.Black, textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            onValueChange = {
                viewModel.editingBicycleWeight = it
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = element, // カーソルの色を設定
                focusedBorderColor = element, // フォーカスされた際の枠の色を設定
                unfocusedBorderColor = element // フォーカスが外れた際の枠の色を設定
            )
        )

        // タイヤ幅(mm)
        OutlinedTextField(
            modifier = Modifier.width(common.textField),
            value = viewModel.editingTireWidth,
            label = { Text("タイヤ幅(mm)", fontSize = common.smallFont, fontWeight = FontWeight.Bold, color = element) },
            textStyle = TextStyle(fontSize = common.normalFont, color = Color.Black, textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            onValueChange = {
                viewModel.editingTireWidth = it
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = element, // カーソルの色を設定
                focusedBorderColor = element, // フォーカスされた際の枠の色を設定
                unfocusedBorderColor = element // フォーカスが外れた際の枠の色を設定
            )
        )
        
        // 最小適正空気圧を計算
        Button(onClick = {
            common.log("体重(kg): ${viewModel.editingBodyWeight}")
            common.log("自転車質量(kg): ${viewModel.editingBicycleWeight}")
            common.log("タイヤ幅(mm): ${viewModel.editingTireWidth}")
            viewModel.calcMinProperPressure()
        }) {
            Text(text = "最小適正空気圧計算", fontSize = common.normalFont)
        }
    }
}