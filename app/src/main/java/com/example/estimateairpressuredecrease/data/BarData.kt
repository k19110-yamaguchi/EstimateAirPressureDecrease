package com.example.estimateairpressuredecrease.data

data class BarData(
    // 気圧
    var barList: List<Double>,
    // 気圧を取得した時間
    var timeList: List<Double>,
)
