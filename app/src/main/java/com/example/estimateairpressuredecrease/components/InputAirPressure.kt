package com.example.estimateairpressuredecrease.components

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.FontSize
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.room.entities.FeatureValueData
import com.example.estimateairpressuredecrease.ui.theme.element

@Composable
fun InputAirPressure(
    isFirst: Boolean, isProperPressure: Boolean, spaceSize: Dp,
    fontSize: FontSize = FontSize(), viewModel: MainViewModel = hiltViewModel()
) {
    var labelText = ""
    var airPressureTextFieldSize = 300.dp

    // 最小適正空気圧入力時
    if(isProperPressure){
        labelText = "最小適正空気圧(kPa)を入力"
    // 学習状態・空気圧入力時
    }else{
        labelText = "空気圧(kPa)を入力"
    }

    // エラーが発生した時
    if(viewModel.errorInputAirPressure!= ""){
        Text(text = viewModel.errorInputAirPressure, fontSize = fontSize.small, color = Color.Red)

        Spacer(modifier = Modifier.height(spaceSize))

    }
    // 入力欄の色変更に必要
    val interactionSource = remember { MutableInteractionSource() }

    // 入力欄
    OutlinedTextField(
        modifier = Modifier.width(airPressureTextFieldSize),
        value = viewModel.editingAirPressure,
        label = { Text(labelText, fontSize = fontSize.small, fontWeight = FontWeight.Bold, color = element) },
        textStyle = TextStyle(fontSize = fontSize.normal, color = Color.Black, textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
        interactionSource = interactionSource,
        onValueChange = {
            viewModel.editingAirPressure = it
            Log.d("editingText", viewModel.editingAirPressure)
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
        cursorColor = element, // カーソルの色を設定
        focusedBorderColor = element, // フォーカスされた際の枠の色を設定
        unfocusedBorderColor = element // フォーカスが外れた際の枠の色を設定
        )
    )

    Spacer(modifier = Modifier.height(spaceSize))

    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = element,
            contentColor = Color.White
        ),
        onClick = { viewModel.inputAirPressure(isFirst, isProperPressure) }
    ) {
        Text(text = "入力完了", fontSize = fontSize.normal)
    }

}