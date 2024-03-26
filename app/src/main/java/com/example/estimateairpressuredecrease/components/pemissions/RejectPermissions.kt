package com.example.estimateairpressuredecrease.components.pemissions

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
// パーミッションが拒否されたり、永続的に拒否されたりした場合の処理
fun RejectPermissions(){
    Text(text = "権限が拒否されたためアプリを使用できません")
    Text(text = "アプリの設定から権限を許可してください")
}