package com.example.estimateairpressuredecrease.room.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// RoomでList<Double>を使用できるように
// List<String> <-> Stringに変換
class ListConverter {
    @TypeConverter
    fun stringToStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun stringListToString(value: List<String>): String {
        return Gson().toJson(value)
    }
}