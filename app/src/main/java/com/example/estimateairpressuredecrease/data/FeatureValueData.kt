package com.example.estimateairpressuredecrease.data

data class FeatureValueData(
    // 加速度標準偏差
    var accSd: Double,
    // 振幅スペクトル
    var ampSptList: List<Double>,
    // 特徴量に使用した開始時刻
    var startGetFv: List<Double>,
    // 特徴量に使用した終了時刻
    var stopGetFv: List<Double>,

    )