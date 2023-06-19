package com.example.estimateairpressuredecrease.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Sensors(
    @PrimaryKey(autoGenerate = true) val id: Int,
    var accId: Int,
    var locId: Int,
    var startDate: LocalDateTime,
    var stopDate: LocalDateTime,
    var airPressure: Int,

    )
