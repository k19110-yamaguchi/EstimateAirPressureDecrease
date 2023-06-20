package com.example.estimateairpressuredecrease

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.entities.Home
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val homeDao: HomeDao) : ViewModel(){
    var isTrainingState by mutableStateOf(true)
    // テキストフィールドに入力された最小適正空気圧
    var editingMinProperPressure by mutableStateOf("")
    var minProperPressure by mutableStateOf(0)
    var inflatedDate: LocalDateTime by mutableStateOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0))

    // Homeのデータを取得
    val home = homeDao.getHomeData().distinctUntilChanged()

    // 初めての起動かどうか確認
    suspend fun checkWhetherFirstTime() {
        // id:0 のhomeがnullだった場合
        if(homeDao.getHomeById(0) == null){
            // データベースを作成
            createHome()
        }
    }

    // 初期値を設定
    fun setHome(home: Home){
        Log.d("setHome", home.isTrainingState.toString() +home.minProperPressure.toString() + home.inflatedDate.toString())
        isTrainingState = home.isTrainingState
        minProperPressure = home.minProperPressure
        inflatedDate = home.inflatedDate
    }

    // データベースを作成
    private fun createHome() {
        viewModelScope.launch {
            val newHome = Home(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.createHomeDB(newHome)
        }
    }

    // データベースを更新
    fun updateHome() {
        viewModelScope.launch {
            Log.d("updateHome", isTrainingState.toString() + minProperPressure.toString() + inflatedDate.toString())
            val newHome = Home(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }
}