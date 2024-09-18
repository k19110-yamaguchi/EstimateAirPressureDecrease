# 仮想環境をアクティブ
## python3 -m venv path/to/venv  
## source path/to/venv/bin/activate

# ライブラリの読み込み
## pip install pandas
import pandas as pd 
## pip install pyproj
from pyproj import Transformer
import numpy as np
import csv

#import os
# スクリプトのディレクトリに移動
#os.chdir(os.path.dirname(os.path.abspath(__file__)))

# Headerクラス      
## 位置情報ヘッダー
class LocHeader:
    def __init__(self,time="time(s)",lat="lat(°)",lon="lon(°)",dis="dis(km)", speed="speed(km/h)"):
        self.time = time
        self.lat = lat
        self.lon = lon
        self.dis = dis
        self.speed = speed    
    
## 区間ヘッダー
class IntervalsHeader:
    def __init__(self,startTime="startTime(s)",stopTime="stopTime(s)"):
        self.startTime = startTime
        self.stopTime = stopTime   

# 閾値
## 走行推定に必要な速度(km)
thresholdSpeed = 5.0
## 急な角推定に必要な曲率半径(m)
thresholdRadius = 15
## 推定区間に必要な距離(km)
thresholdDis = 0.4
## 同じ地点かどうかの推定に必要な距離(m)
thresholdSimPointDis = 10.0   

# ヘッダーの取得
lh = LocHeader()
ih = IntervalsHeader()

# 「緯度経度(°)」→「平面座標系(m)」に変換
def convertToRCS(locDf):    
    wgs84_epsg, rect_epsg = 4326, 6677
    tr = Transformer.from_proj(wgs84_epsg, rect_epsg)
    resXList = []
    resYList = []
    for i, row in locDf.iterrows():        
        x, y = tr.transform(row[lh.lat], row[lh.lon])
        # x軸が逆だったので反転
        x = x*-1   
        resXList.append(x)
        resYList.append(y)         
    return [resXList, resYList]

# 取得したデータのみの分析
# 走行区間の抽出
def extractRidingIntervals(locDf):    
    isRiding = False
    resStartTime = []
    resStopTime = []    

    for i, row in locDf.iterrows():                               
        if not isRiding and row[lh.speed] >= thresholdSpeed:
            # 走行開始時刻の取得            
            resStartTime.append(row[lh.time])
            isRiding = True
        
        if isRiding and locDf[lh.speed][i] < thresholdSpeed:
            # 走行終了時刻の取得
            resStopTime.append(locDf[lh.time][i-1])        
            isRiding = False

    return [resStartTime, resStopTime]

# 急な曲がり角とその曲率半径を抽出
def extractCornerPoint(locDf, startTime, stopTime):
    resCornerTime = []  
    resCornerRadiusCurvature = []  
    
    # 平面座標系(m)に変換
    x, y = convertToRCS(locDf) 
    t = locDf[lh.time]
    # x(t) と y(t) の1次および2次導関数を計算
    xPrime = np.gradient(x, t)
    yPrime = np.gradient(y, t)
    xDoublePrime = np.gradient(xPrime, t)
    yDoublePrime = np.gradient(yPrime, t)
    
    # 曲率半径(m)を計算
    numerator = (xPrime**2 + yPrime**2)**(3/2)
    denominator = np.abs(xPrime * yDoublePrime - yPrime * xDoublePrime)
    # 分母がゼロに近い場合に対処       
    epsilon = 1e-10
    denominator = np.where(denominator < epsilon, epsilon, denominator)    
    radiusCurvature = numerator / denominator

    # 急な曲がり角を抽出
    cornerTime = []
    cornerRadiusCurvature = []
    for i in range(len(radiusCurvature)):
        if radiusCurvature[i] <= thresholdRadius:
            cornerTime.append(t[i])
            cornerRadiusCurvature.append(radiusCurvature[i])

    # 区間の急な曲がり角を抽出
    for i in range(len(startTime)):
        for j in range(len(cornerTime)):
            if startTime[i] <= cornerTime[j] and cornerTime[j] <= stopTime[i]:
                resCornerTime.append(cornerTime[j])
                resCornerRadiusCurvature.append(cornerRadiusCurvature[j])
    
    return [resCornerTime, resCornerRadiusCurvature]

# 急な曲がり角から推定に適した区間を抽出
def extractIntervalsByCorner(startTime, stopTime, cornerTime):    
    resStartTime = []
    resStopTime = []    
    
    for i in range(len(startTime)):        
        resStartTime.append(startTime[i])
        for j in range(len(cornerTime)):
                if startTime[i] <= cornerTime[j] and cornerTime[j] <= stopTime[i]:                                        
                    resStopTime.append(cornerTime[j])  
                    resStartTime.append(cornerTime[j])                
                        
        resStopTime.append(stopTime[i])
    return [resStartTime, resStopTime]

# 区間距離を計算
def calcIntervalsDistance(locDf, startTime, stopTime):    
    res = []
    for i in range(len(startTime)):
        dis = 0
        for j, row in locDf.iterrows():
            if startTime[i] <= row[lh.time] and row[lh.time] <= stopTime[i]:
                dis += row[lh.dis]
        res.append(dis)
    return res

# 距離から推定に適した区間を抽出
def extractIntervalsByDis(startTime, stopTime, ridingDis):        
    resStartTime = []
    resStopTime = []
    for i in range(len(startTime)):    
        if ridingDis[i] >= thresholdDis:
            resStartTime.append(startTime[i])            
            resStopTime.append(stopTime[i])
    return [resStartTime, resStopTime]  

# 走行区間から特徴量を抽出
def extractLocDf(locDf, startTime, stopTime):
    res = locDf[(startTime <= locDf[lh.time]) & (locDf[lh.time] <= stopTime)]
    return res.reset_index(drop=True)

## 安定区間に使えそうな区間を抽出
def extractIntervals(sensingDate, filePath):
    print("extractIntervals: 開始")
        
    # 取得したデータ
    locDf = pd.read_csv(f"{filePath}/{sensingDate}/loc.csv")    
        
    # 走行区間の抽出    
    startTime, stopTime = extractRidingIntervals(locDf)    
    
    # 区間の急な曲がり角と曲率を抽出
    cornerTime, cornerRadiusCurvature = extractCornerPoint(locDf, startTime, stopTime)   
    
    # 曲がり角から推定に適した区間を抽出
    startTime, stopTime = extractIntervalsByCorner(startTime, stopTime, cornerTime)
    
    # 区間距離を計算
    intervalDis = calcIntervalsDistance(locDf, startTime, stopTime)

    # 区間距離から区間を抽出
    startTime, stopTime = extractIntervalsByDis(startTime, stopTime, intervalDis)
    
    # 区間の開始，終了時刻をcsvに保存
    columns = [ih.startTime, ih.stopTime]
    data = []
    for i in range(len(startTime)):
        row = [startTime[i], stopTime[i]]
        data.append(row)
    intervalsDf = pd.DataFrame(data, columns=columns)    
    intervalsDf.to_csv(f"{filePath}/{sensingDate}/intervals.csv", index=False)
    
    print("extractIntervals: 終了")    
    return True    