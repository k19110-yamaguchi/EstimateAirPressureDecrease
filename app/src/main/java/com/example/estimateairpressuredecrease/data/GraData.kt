package com.example.estimateairpressuredecrease.data

data class GraData(
    // X軸の重力加速度
    var xGraList: List<Double>,
    // Y軸の重力加速度
    var yGraList: List<Double>,
    // X軸の重力加速度
    var zGraList: List<Double>,
    // 重力加速度を取得した時間
    var timeList: List<Double>,

    )
