package com.example.estimateairpressuredecrease.data

data class AccData(
    // X軸の加速度
    var xAccList: List<Double>,
    // Y軸の加速度
    var yAccList: List<Double>,
    // X軸の加速度
    var zAccList: List<Double>,
    // 加速度を取得した時間
    var timeList: List<Double>,

)
