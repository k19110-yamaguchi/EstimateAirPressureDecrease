package com.example.estimateairpressuredecrease
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.estimateairpressuredecrease.room.dao.HomeDao
import com.example.estimateairpressuredecrease.room.entities.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val homeDao: HomeDao) : ViewModel(){

    // Home
    var isTrainingState by mutableStateOf(true)
    // テキストフィールドに入力された最小適正空気圧
    var editingMinProperPressure by mutableStateOf("")
    var minProperPressure by mutableStateOf(0)
    var inflatedDate: LocalDateTime by mutableStateOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0))


    // Acc
    // 加速度データのid
    // X軸の加速度
    var xAcc: Double by mutableStateOf(0.0)
    // Y軸の加速度
    var yAcc: Double by mutableStateOf(0.0)
    // X軸の加速度
    var zAcc: Double by mutableStateOf(0.0)
    var isSensing: Boolean by mutableStateOf(false)

    var lat: Double by mutableStateOf(0.0)
    var lon: Double by mutableStateOf(0.0)

    // 重力加速度
    var xGra: Double by mutableStateOf(0.0)
    var yGra: Double by mutableStateOf(0.0)
    var zGra: Double by mutableStateOf(0.0)

    var bar: Double by mutableStateOf(0.0)





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
    fun setHome(home: HomeData){
        Log.d("setHome", home.isTrainingState.toString() +home.minProperPressure.toString() + home.inflatedDate.toString())
        isTrainingState = home.isTrainingState
        minProperPressure = home.minProperPressure
        inflatedDate = home.inflatedDate
    }

    // データベースを作成
    private fun createHome() {
        viewModelScope.launch {
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.createHomeDB(newHome)
        }
    }

    // データベースを更新
    fun updateHome() {
        viewModelScope.launch {
            Log.d("updateHome", isTrainingState.toString() + minProperPressure.toString() + inflatedDate.toString())
            val newHome = HomeData(isTrainingState = isTrainingState, minProperPressure = minProperPressure, inflatedDate = inflatedDate)
            homeDao.updateHomeData(newHome)
        }
    }
}