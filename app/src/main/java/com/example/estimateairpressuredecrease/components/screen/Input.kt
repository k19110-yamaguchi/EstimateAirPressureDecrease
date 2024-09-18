package com.example.estimateairpressuredecrease.components.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
fun Input(viewModel: MainViewModel){
    val common = Common()
    var labelText = ""

    // 画面の表示
    Box(modifier = Modifier.fillMaxSize()){
        // トップ
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ){
            Spacer(modifier = Modifier.height(common.space))

            when (viewModel.inputStatus){
                // 適正空気圧入力
                common.inputProperPressureNum -> {
                    Text(text = "適正空気圧入力画面", fontSize = common.largeFont)
                    labelText = "適正空気圧(kPa)"

                    Spacer(modifier = Modifier.height(common.space))

                    // 最小適正空気圧を計算するための要素を入力
                    Text(text = "最小適正空気圧がわからない場合は\n以下の情報を入力", fontSize = common.smallFont, textAlign = TextAlign.Center)
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

                    Spacer(modifier = Modifier.height(common.space/4))

                    // 最小適正空気圧を計算
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = element,
                            contentColor = Color.White
                        ),
                        onClick = {
                            common.log("体重(kg): ${viewModel.editingBodyWeight}")
                            common.log("自転車質量(kg): ${viewModel.editingBicycleWeight}")
                            common.log("タイヤ幅(mm): ${viewModel.editingTireWidth}")
                            viewModel.calcMinProperPressure()
                        }) {
                        Text(text = "最小適正空気圧計算", fontSize = common.smallFont)
                    }

                    Spacer(modifier = Modifier.height(common.space/2))

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
                }

                // 測定空気圧入力
                common.inputPressureNum -> {
                    Text(text = "測定空気圧入力画面", fontSize = common.largeFont)
                    labelText = "測定空気圧(kPa)"
                }

                else -> {
                }
            }

        }


        if(viewModel.inputStatus == common.inputPressureNum){
            // センター
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
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
            }
        }

        // ボトム
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            // エラーの表示
            Text(text = viewModel.errorInputAirPressure, fontSize = common.smallFont, color = Color.Red)
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = element,
                    contentColor = Color.White
                ),
                onClick = {
                    common.log("${labelText}: ${viewModel.editingAirPressure}")
                    viewModel.inputAirPressure()
                }) {
                Text(text = "入力完了", fontSize = common.normalFont)
            }
            Spacer(modifier = Modifier.height(common.space))
        }
    }
}