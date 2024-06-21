import numpy as np
from geopy.distance import geodesic
from statistics import stdev

# java.util.ArrayListをlistの型に変換
def changeJavaList(javaList):
    pyList = [[javaList.get(i).get(j) for j in range(javaList.get(i).size())] 
                for i in range(javaList.size())]   
    return pyList


# 走行中の加速度を取得
def getDrivingAccData(accData, locData):
    locSize = len(locData)   
    # 距離を求める
    disData = [0.0]
    for i in range(locSize - 1):          
        dis = geodesic([locData[i][0], locData[i][1]], [locData[i+1][0], locData[i+1][1]]).km
        disData.append(dis)
    
    # 時速を求める
    speedData = [0.0]
    for i in range(locSize - 1):
        speed = (disData[i+1] / (locData[i+1][2] - locData[i][2])) * 3600
        speedRound = round(speed, 1)        
        speedData.append(speedRound)

    # 走行開始・終了時間を求める
    startTime = 0.0
    stopTime = 0.0
    drivingThreshold = 5.0
    for i in range(locSize):
        if startTime == 0.0 and speedData[i] >= drivingThreshold:
            startTime = locData[i][2]
        if speedData[i] <= drivingThreshold and speedData[i-1] >= drivingThreshold:
            stopTime = locData[i][2]  
        
    #### デバック用
    #startTime = locData[0][2]
    #stopTime = locData[locSize-1][2]

    # 走行区間の加速度を取得    
    accSize = len(accData)    
    
    res = []     
    for i in range(accSize):
        if accData[i][3] >= startTime and accData[i][3] <= stopTime:
            res.append([accData[i][0], accData[i][1], accData[i][2], accData[i][3]])

    return res


# 走行中のy軸方向の加速度を取得
def getDrivingYAcc(drivingAccData):
    drivingAccSize = len(drivingAccData)
    res = []
    for i in range(drivingAccSize):
        # 重力加速度成分を除く
        res.append(drivingAccData[i][1]-9.8)

    return res
    

def getAmpSpec(drivingYAcc):    
    # FFT
    fft = np.fft.fft(drivingYAcc)
    fftAbs = np.abs(fft)
    fftSize = len(fftAbs)

    # 元の信号の振幅に合わせる
    fftAmpAbs = fftAbs / fftSize * 2
    
    # 周波数軸データの作成
    # 加速度センサのサンプリング周期 0.02s(50Hz)
    dt = 0.02
    fq = np.linspace(0, 1.0/dt, fftSize)

    # 周波数の範囲
    fqWidth = 0.5    
    fqMax = 40
    res = []
    ampSum = 0
    fqNow = 0
    count = 0 
    for i in range(fftSize):        
        if fqNow <= fq[i] and fq[i] < fqNow + fqWidth:            
            ampSum = ampSum + fftAmpAbs[i]
            count = count + 1
            
        else:    
            if count == 0:
                res.append(0)
            else:
                res.append(ampSum / count)
                ampSum = 0
                count = 0
                   
            fqNow = fqNow + fqWidth

            if fqNow >= fqMax:
                break
    
    return res
    

def createFeatureValue(accDataArray, graDataArray, locDataArray, barDataArray):         
    # java.util.ArrayListをlistの型に変換
    accData = changeJavaList(accDataArray) 
    graData = changeJavaList(graDataArray)
    locData = changeJavaList(locDataArray)
    barData = changeJavaList(barDataArray)

    #走行中の加速度を取得
    drivingAccData = getDrivingAccData(accData, locData)
    # デバック用
    #drivingAccData = accData
    
    # 走行中のy軸方向の加速度を取得
    drivingYAcc = getDrivingYAcc(drivingAccData)  

    # 加速度標準偏差の取得
    if len(drivingYAcc) >= 2:
        AccSd = stdev(drivingYAcc)

    else:
        return 0

    # 振幅スペクトルを取得 
    ampSpec = getAmpSpec(drivingYAcc)
    
    if ampSpec == 0:
        return 0
    
    # 特徴量の行を作成
    futureValues = [AccSd]
    for i in range(len(ampSpec)):
        futureValues.append(ampSpec[i])    

    return futureValues