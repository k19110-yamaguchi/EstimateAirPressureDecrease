import numpy as np
import pandas as pd
import os
from geopy.distance import geodesic
from statistics import stdev


# 特徴量のヘッダー
AIR_PRESSURE_HEDDER = "air_pressure"
SUST_SPEED_HEDDER = "sust_speed"

# センサデータのヘッダー
accHeader = ["x(m/s)", "y(m/s)", "z(m/s)", "t(s)"]
graHeader = ["x(m/s)", "y(m/s)", "z(m/s)", "t(s)"]
locHeader = ["lat", "lon", "t(s)"]
barHeader = ["bar(Pa)", "t(s)"]
sensorHeader = [accHeader, graHeader, locHeader, barHeader]

# スクリプトのディレクトリに移動
os.chdir(os.path.dirname(os.path.abspath(__file__)))
# 初期の特徴量データのファイルパス
initFilePath = "initial_feature_value.csv"

def estimateAirPressure():
    # 初期の特徴量データの取得
    df = pd.read_csv(initFilePath)
    print("df")
    print(df)
    print()

    # 追加で集めた特徴量データの取得


    # 説明・目的変数に分割
    # 説明変数の取得
    ev = df.drop(SUST_SPEED_HEDDER, axis=1).drop(AIR_PRESSURE_HEDDER, axis=1)
    ov = df[AIR_PRESSURE_HEDDER]
    print("説明変数")
    print(ev)
    print()

    print("目的変数")
    print(ov)
    print()


def createSensorDataCSV(columnList, dataList, date, sensor):
    # scv出力するデータフレームの作成
    df = pd.DataFrame(columns = columnList)
    # 時間ごとのデータに変更
    for i in range(len(dataList[0])):        
        data = []
        for j in range(len(dataList)):
            data.append(dataList[j][i])
        
        # 配列をDataFrameの型にする
        df1 = pd.DataFrame([data], columns=columnList)        
        # 出力するDataFrameに結合
        df = pd.concat([df, df1], ignore_index=True)
                
    # 出力するファイル名の作成
    fileName = str(date) + "_" + sensor

    # csv出力
    df.to_csv("sensorData/"+ fileName + ".csv", index = False)


def createSampleData():
    # 加速度データのcsvの読み込み
    df = pd.read_csv("sample_acc.csv")       
    # 加速度関係の取得
    accTime = df.iloc[:, 0].values.flatten() 
    xAcc = df.iloc[:, 1].values.flatten()
    yAcc = df.iloc[:, 2].values.flatten()
    zAcc = df.iloc[:, 3].values.flatten()  
    accData = []
    for i in range(len(accTime)):
        accData.append([xAcc[i], yAcc[i], zAcc[i], accTime[i]])

    # 位置情報データのcsvの読み込み
    df = pd.read_csv("sample_loc.csv")
    # 位置情報関係の取得
    locTime = df.iloc[:, 0].values.flatten()    
    lat = df.iloc[:, 1].values.flatten() 
    lon = df.iloc[:, 2].values.flatten()    
    # locTime = np.insert(locTime, 0, 0.0)     


    locData = [] 
    for i in range(len(locTime)):
        locData.append([lat[i], lon[i], locTime[i]])
    
    return([accData, locData])


def getFeatureValues(accDataArray, graDataArray, locDataArray, barDataArray, airPressure): 
    if accDataArray == 0:
        sampleData = createSampleData()
        accData = sampleData[0]
        locData = sampleData[1]
    else:
        # java.util.ArrayListをlistの型に変換
        accData = [[accDataArray.get(i).get(j) for j in range(accDataArray.get(i).size())] 
                for i in range(accDataArray.size())]   
        graData = [[graDataArray.get(i).get(j) for j in range(graDataArray.get(i).size())] 
                for i in range(graDataArray.size())]    
        locData = [[locDataArray.get(i).get(j) for j in range(locDataArray.get(i).size())] 
                for i in range(locDataArray.size())]
        barData = [[barDataArray.get(i).get(j) for j in range(barDataArray.get(i).size())] 
                for i in range(barDataArray.size())]
    
    locSize = len(locData)   
    # 距離を求める
    disData = [0.0]
    for i in range(locSize - 1):          
        dis = geodesic([locData[i][0], locData[i][1]], [locData[i+1][0], locData[i+1][1]]).km
        disData.append(dis)
    
    # 時速を求める
    speedData = [0.0]
    for i in range(locSize - 1):
        speed = (disData[i] / (locData[i+1][2] - locData[i][2])) * 3600
        speedRound = round(speed, 1)        
        speedData.append(speedRound)

    print("speedData")
    print(speedData)
    # 走行開始・終了時間を求める
    startTime = 0.0
    stopTime = 0.0
    drivingThreshold = 5.0
    for i in range(locSize):
        if startTime == 0.0 and speedData[i] >= drivingThreshold:
            startTime = locData[i][2]
        if speedData[i] <= drivingThreshold and speedData[i-1] >= drivingThreshold:
            stopTime = locData[i][2]  

    # 走行区間の加速度を取得    
    accSize = len(accData)    
    
    drivingAccData = [] 
    drivingAccData = accData
    for i in range(accSize):
        if accData[i][3] >= startTime and accData[i][3] <= stopTime:
            drivingAccData.append([accData[i][0], accData[i][1], accData[i][2], accData[i][3]])

    
    
    drivingAccSize = len(drivingAccData)

    # 加速度標準偏差の取得
    drivingYAcc = []
    for i in range(drivingAccSize):
        drivingYAcc.append(drivingAccData[i][1])

    yAccSd = stdev(drivingYAcc)

    # FFT
    fft = np.fft.fft(drivingYAcc)
    fftAbs = np.abs(fft)
    fftSize = len(fftAbs)

    # 元の信号の振幅に合わせる
    fftAmpAbs = fftAbs / fftSize * 2
    
    # 周波数軸データの作成
    # 加速度センサのサンプリング周期 0.0025s(2.5Hz)
    dtSum = 0.0
    for i in range(drivingAccSize - 1):
        dt = drivingAccData[i+1][3] - drivingAccData[i][3]
        dtSum += dt        
    dtAve = dtSum / (drivingAccSize - 1)
    fq = np.linspace(0, 1.0/dtAve, fftSize)

    # 周波数の範囲
    fqWidth = 0.5    
    fqMax = 40
    ampSpec = []
    ampSum = 0
    fqNow = 0
    count = 0    
    for i in range(fftSize):
        if fqNow <= fq[i] and fq[i] < fqNow + fqWidth:
            ampSum = ampSum + fftAmpAbs[i]
            count = count + 1

        else:            
            ampSpec.append(ampSum / count)
            ampSum = 0
            count = 0
            fqNow = fqNow + fqWidth

            if fqNow >= fqMax:
                break
    
    futureValues = [yAccSd]
    for i in range(len(ampSpec)):
        futureValues.append(ampSpec[i])    

    return futureValues


def printDate(date):        
    date = date.split('-', 1)    
    year = date[0]
    date = date[1].split('-', 1)
    month = date[0]
    date = date[1].split('T', 1)
    day = date[0]
    date = date[1].split(':', 1)
    hour = date[0]
    date = date[1].split(':', 1)
    minute = date[0]
    date = date[1].split('.', 1)
    sec = date[0]
    res = year + month + day + hour + minute + sec
    return res


#fv = getFeatureValues(0,0,0,0,300)
#print(fv)
#fileName = printDate("2023-09-15T23:22:58.090723")
#print(fileName)


