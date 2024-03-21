package com.example.estimateairpressuredecrease.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.estimateairpressuredecrease.Common
import com.example.estimateairpressuredecrease.MainViewModel
import com.example.estimateairpressuredecrease.components.screen.Home
import com.example.estimateairpressuredecrease.components.screen.Input
import com.example.estimateairpressuredecrease.components.screen.sensing.Sensing
import com.example.estimateairpressuredecrease.sensors.Accelerometer
import com.example.estimateairpressuredecrease.sensors.Barometric
import com.example.estimateairpressuredecrease.sensors.Gravity
import com.example.estimateairpressuredecrease.sensors.Location

@Composable
fun MainContent(
    acc: Accelerometer, gra: Gravity, loc: Location, bar: Barometric,
    viewModel: MainViewModel = hiltViewModel(), common: Common = Common()
){

    // 初回起動か調べる
    viewModel.checkIsInitialization()

    when (viewModel.screenStatus) {
        // ホーム画面を表示
        common.homeNum -> {
            Home(acc, gra, loc, bar, viewModel)
        }

        // センシング画面を表示
        common.sensingNum -> {
            Sensing(acc, gra, loc, bar, viewModel)
        }

        // 入力画面を表示
        common.inputNum -> {
            Input(viewModel)
        }

        else -> {
            Text(text = "画面の遷移で異常", fontSize = common.smallFont)
        }
    }

    /*

    val spaceSize = 20.dp

    // ホーム画面の情報を取得
    val homeData by viewModel.homeData.collectAsState(initial = emptyList())
    val sensorData by viewModel.sensorData.collectAsState(initial = emptyList())
    val accData by viewModel.accData.collectAsState(initial = emptyList())
    val locationData by viewModel.locData.collectAsState(initial = emptyList())
    // 特徴量データを取得
    val featureValueData by viewModel.featureValueData.collectAsState(initial = emptyList())
    viewModel.fv = featureValueData

    Test(sensorData, accData, locationData, featureValueData)

    Column(
        // 上下の真ん中
        verticalArrangement = Arrangement.Center,
        // 左右の真ん中
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Log.d("homeData.isEmpty()", homeData.isEmpty().toString())
        Log.d("viewModel.isInputtingAirPressure", viewModel.isInputtingAirPressure.toString())
        Log.d("viewModel.isInputtingMinProperPressure", viewModel.isInputtingMinProperPressure.toString())

        // 空気圧入力画面
        if(homeData.isEmpty() || viewModel.isInputtingAirPressure || viewModel.isInputtingMinProperPressure){

            viewModel.checkIsFirst()
            Log.d("inputAirPressure", "")
            if(viewModel.isFirst){
                Log.d("isFirst", "")
                InputAirPressure(viewModel.isFirst, true, spaceSize)


            }else{
                // 最小適正空気圧入力時
                if(viewModel.isInputtingMinProperPressure){
                    Log.d("isInputtingMinProperPressure", "")
                    InputAirPressure(false, true, spaceSize)


                }else if(viewModel.isInputtingAirPressure){
                    InputAirPressure(false, false, spaceSize)
                }
            }

            // ホーム画面
        }else{
            Log.d("home", "")
            viewModel.setHome(homeData[0])

            // 学習状態から推定状態に変更できるか調べる
            if(featureValueData.isNotEmpty()) {
                if(viewModel.isTrainingState){
                    Log.d("MainContent", "featureValueDataあり")
                    viewModel.checkStatus(featureValueData)
                }
            }

            if (!viewModel.isTrainingState){
                // 危険かの判断
                if(0 < viewModel.estimatedAirPressure && viewModel.estimatedAirPressure < viewModel.minProperPressure){
                    Text(text = "推定空気圧: " + viewModel.estimatedAirPressure.toString() + "kPa 危険", fontSize = fontSize.normal, color = Color(255, 0, 0), fontWeight = FontWeight.Bold)
                }else if(viewModel.minProperPressure <= viewModel.estimatedAirPressure){
                    Text(text = "推定空気圧: " + viewModel.estimatedAirPressure.toString() + "kPa 安全", fontSize = fontSize.normal)

                }
            }

            Spacer(modifier = Modifier.height(spaceSize))


            // 最小適正空気圧の欄
            Row(
                // 上下の真ん中
                verticalAlignment = Alignment.CenterVertically,
            ) {

                // 最小適正空気圧表示欄
                Text(text = "最小適正空気圧：" + viewModel.minProperPressure.toString() + "kPa", fontSize = fontSize.small)

                Spacer(modifier = Modifier.width(spaceSize))


                // センシング中の場合
                if(viewModel.isSensing){
                    // 編集する場合のボタン作成
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White
                        ),
                        onClick = {
                            Log.d("MainContent", "センシンング中のため無効")
                        }
                    ) {
                        Text(text = "変更", fontSize = fontSize.small)
                    }
                }else{
                    // 編集する場合のボタン作成
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = element,
                            contentColor = Color.White
                        ),
                        onClick = {
                            viewModel.isInputtingMinProperPressure = true
                        }
                    ) {
                        Text(text = "変更", fontSize = fontSize.small)
                    }
                }
            }

            // 学習状態
            if (viewModel.isTrainingState){
                Text(text = "学習状態", fontSize = fontSize.large)

                // 推定状態
            }else{
                // viewModel.estimateAirPressure(featureValueData)
                Text(text = "推定状態", fontSize = fontSize.large)

            }

            Spacer(modifier = Modifier.height(spaceSize))

            // センサデータ欄
            Sensor(acc, gra, loc, bar, spaceSize)

        }
    }
     */

}


