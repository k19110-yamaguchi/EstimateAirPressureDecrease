package com.example.estimateairpressuredecrease.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.regex.Pattern

@Composable
fun executionConfirmation(viewModel: MainViewModel = hiltViewModel()) {
    // homeのデータを取得する(ない場合はempty)
    val home by viewModel.home.collectAsState(initial = emptyList())

    // emptyじゃない場合は実行
    if(home.isNotEmpty()) {
        // データベースの値を挿入
        viewModel.setHome(home[0])

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 状態: 学習状態or推定状態
            Text(text = "status: ${showStatus(viewModel.isTrainingState)}", fontSize = 30.sp)

            // ボタンで状態を切り替える(今後は日付やデータ数で自動で切り替え、学習しながら推定する？)
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    // 状態を反転
                    viewModel.isTrainingState = !viewModel.isTrainingState
                    // データベースに保存
                    viewModel.updateHome()
                }
            ) {
                Text(text = "状態変更")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 最小適正空気圧: 4桁以下の数字
            val context = LocalContext.current

            Text(text = "最小適正空気圧: ${showMinProperPressure(viewModel.minProperPressure)}", fontSize = 30.sp)

            // 入力欄
            TextField(
                modifier = Modifier.width(240.dp),
                value = viewModel.editingMinProperPressure,
                label = { Text("最小適正空気圧を入力(kPa)") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                onValueChange = { viewModel.editingMinProperPressure = it }
            )

            // 入力欄のデータを確認、保存
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    // 数字かつ4桁未満か確認して挿入
                    viewModel.minProperPressure = changeMinProperPressure(viewModel.editingMinProperPressure)
                    // 入力欄を空欄に
                    viewModel.editingMinProperPressure = ""
                    // 最小適正空気圧を保存
                    viewModel.updateHome()
                }
            ) {
                Text(text = "保存")
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 注入した日付(MM月dd日)
            Text(text = "空気注入日:${showInflatedDate(viewModel.inflatedDate)}", fontSize = 30.sp)

            // 現在の日付を保存
            Button(
                modifier = Modifier.width(240.dp),
                onClick = {
                    // 現在の日付を取得
                    viewModel.inflatedDate = LocalDateTime.now()
                    // 注入した日付を保存
                    viewModel.updateHome()
                }
            ) {
                Text(text = "空気を注入した!!")
            }
        }

    // emptyの場合
    }else{
        // データベースの作成が必要か
        checkWhetherFirstTime(viewModel)
    }
}

// アプリ起動時に
fun checkWhetherFirstTime(viewModel: MainViewModel) {
    viewModel.viewModelScope.launch {
        viewModel.checkWhetherFirstTime()
    }
}

// 状態の表示テキスト
fun showStatus(isTrainingState: Boolean): String {
    var res = ""

    // 学習状態: true, 推定状態: false
    if (isTrainingState){
        res = "学習状態"
    }else{
        res = "推定状態"
    }
    return res
}

// 最小適正空気圧の表示テキスト
fun showMinProperPressure(minProperPressure: Int): String {
    var res = ""

    // 最小適正空気圧が0出ない場合
    if(minProperPressure != 0){
        res = minProperPressure.toString() + "kPa"
    }
    return res
}

// 注入の日付の表示テキスト
fun showInflatedDate(inflatedDate: LocalDateTime): String{
    var res = ""

    // 初期の日付でない場合
    if(inflatedDate != LocalDateTime.of(2000, 1, 1, 0, 0, 0)){
        // MM月dd日
        res = "${inflatedDate.monthValue}月${inflatedDate.dayOfMonth}日"
    }
    return res
}

// 最小適正空気圧をString->Intに変換
fun changeMinProperPressure(text: String): Int {
    var res = 0
    val regex = "\\A[-]?[0-9]+\\z"
    val p = Pattern.compile(regex)
    val isNumber = p.matcher(text).find()
    // 数字の場合
    if(isNumber){
        // 4桁未満の場合
        if(Integer.parseInt(text) < 1000){
            res = Integer.parseInt(text)
        }
    }
    return res
}





