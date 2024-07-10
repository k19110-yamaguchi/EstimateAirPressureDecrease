package com.example.estimateairpressuredecrease.components.pemissions

import android.Manifest
import androidx.compose.runtime.Composable
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.components.MainContent
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Gps
import com.google.accompanist.permissions.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
// パーミッションの処理
fun CheckPermissions(acc: Accelerometer, gra: Gravity, loc: Gps, bar: Barometric, common: Common = Common()){
    // パーミッションの状態を取得
    val permissionsStates: MultiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    // 全てのパーミッションが許可されている場合
    if(permissionsStates.allPermissionsGranted){
        common.log("パーミッション許可")
        // アプリ画面を表示
        MainContent(acc, gra, loc, bar)

    // パーミッションが拒否されたり、永続的に拒否されたりした場合
    }else if(permissionsStates.shouldShowRationale){
        common.log("パーミッション拒否")
        // メッセージの表示
        RejectPermissions()

    // パーミッションが許可されていない場合
    }else{
        common.log("パーミッション要求")
        // パーミッションをリクエスト
        RequestPermissions(onRequestPermission = permissionsStates::launchMultiplePermissionRequest)

    }
}