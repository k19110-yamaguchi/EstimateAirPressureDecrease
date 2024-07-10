package com.example.estimateairpressuredecrease.data

data class LocData(
    // 緯度
    var latList: List<Double>,
    // 経度
    var lonList: List<Double>,
    // 位置情報を取得した時間
    var timeList: List<Double>,
    // 移動した距離
    var disList: List<Double>,
    // 移動した時速
    var speedList: List<Double>,

    )
