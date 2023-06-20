package com.example.estimateairpressuredecrease.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// RoomでList<Double>を使用できるように
// List<Double> <-> Stringに変換
class ListConverters {
    @TypeConverter
    fun stringToDoubleList(value: String): List<Double> {
        val listType = object : TypeToken<List<Double>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun doubleListToString(value: List<Double>): String {
        return Gson().toJson(value)
    }
}