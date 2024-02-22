package com.example.estimateairpressuredecrease.components.pemissions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
// パーミッションが拒否されたり、永続的に拒否されたりした場合の処理
fun RejectPermissions(){
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "権限が拒否されたためアプリを使用できません")
        Text(text = "アプリの設定から権限を許可してください")
    }
}