package com.example.estimateairpressuredecrease.components.pemissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
// パーミッションが許可されていない場合の処理
fun RequestPermissions(onRequestPermission:() -> Unit){
    LaunchedEffect(Unit) {
        onRequestPermission()
    }
}
