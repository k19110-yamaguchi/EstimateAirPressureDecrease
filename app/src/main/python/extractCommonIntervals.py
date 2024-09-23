
# ライブラリの読み込み
## pip install pandas
import pandas as pd  # type: ignore
## pip install pyproj
from pyproj import Transformer
import numpy as np # type: ignore
import csv
import math

# Headerクラス
class LocHeader:
    def __init__(self,time="time(s)",lat="lat(°)",lon="lon(°)",dis="dis(km)", speed="speed(km/h)"):
        self.time = time
        self.lat = lat
        self.lon = lon
        self.dis = dis
        self.speed = speed  
class IntervalsHeader:
    def __init__(self,startTime="startTime(s)",stopTime="stopTime(s)"):
        self.startTime = startTime
        self.stopTime = stopTime   

lh = LocHeader()
ih = IntervalsHeader()

# 閾値
## 同じ地点かどうかの推定に必要な距離(m)
thresholdSimPointDis = 10.0 
## 推定区間に必要な距離(km)
thresholdDis = 0.4  


# java.util.ArrayList(1次元)をlistの型に変換
def changeJavaList(javaList):
    pyList = [javaList.get(i) for i in range(javaList.size())]                    
    return pyList

# 平面座標系(m)に変換
def convertToRCS(locDf):
    wgs84_epsg, rect_epsg = 4326, 6677
    tr = Transformer.from_proj(wgs84_epsg, rect_epsg)
    xList = []
    yList = []
    for i, row in locDf.iterrows():        
        x, y = tr.transform(row[lh.lat], row[lh.lon])
        # x軸が逆だったので反転
        x = x*-1   
        xList.append(x)
        yList.append(y)         
    return [xList, yList]

# 走行区間から特徴量を抽出
def extractLocDf(locDf, startTime, stopTime):
    res = locDf[(startTime <= locDf[lh.time]) & (locDf[lh.time] <= stopTime)]
    return res.reset_index(drop=True)

# 位置情報に関するデータを全て取得 
def getAllLocDfs(filePath, sensingDates):
    resLocDfs = []
    resIntervalsDfs = []
    for sd in sensingDates:
        locDf = pd.read_csv(f"{filePath}/{sd}/loc.csv") 
        intervalsDf = pd.read_csv(f"{filePath}/{sd}/intervals.csv")           
        resLocDfs.append(locDf)
        resIntervalsDfs.append(intervalsDf)
    return [resLocDfs, resIntervalsDfs]

# 似た地点を計算
# todo: 最初は全部で調べる，次からその周辺で調べる
def calcSimPoints(locDf1, locDf2):
    resDis = []                      
    resTime = []
    x1, y1 = convertToRCS(locDf1)                             
    x2, y2 = convertToRCS(locDf2) 

    for i in range(len(locDf1)):
        minDis = 0
        minTime = 0
        for j in range(len(locDf2)):                                    
            xDis = pow(x1[i]-x2[j], 2.0)
            yDis = pow(y1[i]-y2[j], 2.0)   
            dis =  math.sqrt(xDis+yDis) 
            if j == 0:
                minDis = dis
                minTime = locDf2[lh.time][j]
            else:
                if dis < minDis:
                    minDis = dis
                    minTime = locDf2[lh.time][j]
        resDis.append(minDis)
        resTime.append(minTime)
    return [resDis, resTime]

# 区間位置情報データを比較
def compareLocDfs(curtExLocDfs, compExLocDfs):
    curtStartTimeList = []
    curtStopTimeList = []
    compStartTimeList = []
    compStopTimeList = []
    curtRouteNum = len(compExLocDfs)
    for curtIndex, curtExLocDf in enumerate(curtExLocDfs):
        startTimeList1 = []
        startTimeList2 = []
        stopTimeList1 = []
        stopTimeList2 = []
        for i in range(len(compExLocDfs)):
            for compIndex, compExLocDf in enumerate(compExLocDfs[i]):
                print(f"route[{curtRouteNum}][{curtIndex}]-rute[{i}][{compIndex}]")
                simPointsDis, simPointsTime = calcSimPoints(curtExLocDf, compExLocDf)
                startTime1 = -1
                startTime2 = -1                                    
                stopTime1 = -1
                stopTime2 = -1
                dis = 0
                # 共通区間が複数ある場合に必要
                tmpStartTime1 = 0
                tmpStartTime2 = 0
                tmpStopTime1 = 0
                tmpStopTime2 = 0
                tmpDis = 0  
                isCommon = False
                for n in range(len(curtExLocDf)):                                             
                    if simPointsDis[n] < thresholdSimPointDis: 
                        isCommon = True                       
                        if startTime1 == -1:
                            startTime1 = curtExLocDf[lh.time][n]
                            startTime2 = simPointsTime[n]
                        stopTime1 = curtExLocDf[lh.time][n]
                        stopTime2 = simPointsTime[n]
                        dis += curtExLocDf[lh.dis][n]
                    elif simPointsDis[n] >= 10 and isCommon:
                        isCommon = False                        
                        if tmpDis == 0 or tmpDis < dis:
                            tmpStartTime1 = startTime1
                            tmpStartTime2 = startTime2
                            tmpStopTime1 = stopTime1
                            tmpStopTime2 = stopTime2
                            tmpDis = dis  
                    
                if tmpDis != 0 and tmpDis > dis:
                    startTime1 = tmpStartTime1
                    startTime2 = tmpStartTime2 
                    stopTime1 = tmpStopTime1
                    stopTime2 = tmpStopTime2
                    dis = tmpDis 
                                                                                
                if startTime1 == -1:
                    print(f"共通区間なし")    

                elif dis < thresholdDis:
                    print(f"共通区間距離が短い: {round(dis, 5)}km")
                    startTime1 = -1
                    startTime2 = -1
                    stopTime1 = -1
                    stopTime2 = -1

                else:                        
                    print(f"共通区間距離: {round(dis, 5)}km")                                                

                startTimeList1.append(startTime1)    
                startTimeList2.append(startTime2)    
                stopTimeList1.append(stopTime1)    
                stopTimeList2.append(stopTime2)    
        curtStartTimeList.append(startTimeList1)
        curtStopTimeList.append(stopTimeList1)
        compStartTimeList.append(startTimeList2)
        compStopTimeList.append(stopTimeList2)
    return [curtStartTimeList, curtStopTimeList, compStartTimeList, compStopTimeList]


# 共通区間のファイルを更新
def updateCommonTimeFile(locDfs, intervalsDfs, filePath):
    # 現在の区間データを取得
    curtLocDf = locDfs[-1]
    # 比較する位置情報データを取得
    compLocDfs = locDfs[0:-1]
    # 現在の区間データを取得
    curtIntervalsDf = intervalsDfs[-1]
    # 比較する区間データを取得
    compIntervalsDfs = intervalsDfs[0:-1]

    # 現在の区間位置情報データを取得
    curtExLocDfs = []
    for i, row in curtIntervalsDf.iterrows():
        exLocDf = extractLocDf(curtLocDf, row[ih.startTime], row[ih.stopTime])
        curtExLocDfs.append(exLocDf)         

    # 比較する区間位置情報データを取得
    compExLocDfs = []
    for i, compLocDf in enumerate(compLocDfs):        
        _compExLocDfs = []        
        for j, row in compIntervalsDfs[i].iterrows():            
            exLocDf = extractLocDf(compLocDf, row[ih.startTime], row[ih.stopTime])
            _compExLocDfs.append(exLocDf)
        compExLocDfs.append(_compExLocDfs)   

    # 区間位置情報データを比較
    curtStartTimeList, curtStopTimeList, compStartTimeList, compStopTimeList = compareLocDfs(curtExLocDfs, compExLocDfs)  
    print(curtStartTimeList)
    print(curtStopTimeList)
    


    return 0

# 最初に共通区間のファイルを作成
def createCommonTimeFile(intervalsDfs, filePath):
    header = [""]
    commonStartTime = []
    commonStopTime = []

    for i in range(len(intervalsDfs[0])):  
        first = 0
        second = i           
        header.append(f"[{first}][{second}]")
        commonStartTime.append(-1)
        commonStopTime.append(-1)    

    # 共通区間開始時間のファイルを作成
    f = open(f'{filePath}/commonStartTime.csv', 'w')    
    writer = csv.writer(f, lineterminator='\n')
    writer.writerow(header)
    for i in range(len(intervalsDfs[0])):
        row = [header[i+1]]     
        for i in range(len(commonStartTime)):
            row.append(commonStartTime[i])
        writer.writerow(row)
    f.close()

    # 共通区間終了時間のファイルを作成
    f = open(f'{filePath}/commonStopTime.csv', 'w')    
    writer = csv.writer(f, lineterminator='\n')
    writer.writerow(header)
    for i in range(len(intervalsDfs[0])):
        row = [header[i+1]]    
        for i in range(len(commonStopTime)):
            row.append(commonStopTime[i])            
        writer.writerow(row)
    f.close()  

    return 0


def extractCommonIntervals(sensingDatesArray, filePath):    
    print("extractCommonInterval: 開始")  
    # JavaList→Listに変換       
    sensingDates = changeJavaList(sensingDatesArray)

    # デバック用
    import os
    # スクリプトのディレクトリに移動
    os.chdir(os.path.dirname(os.path.abspath(__file__)))    
    sensingDates = ["20240709073515", "20240709185604"]
    
    # 保存した位置情報，区間データを取得
    # デバック用
    locDfs, intervalsDfs = getAllLocDfs("sensorData", sensingDates)    
    #locDfs, intervalsDfs = getAllLocDfs(filePath, sensingDates)    
    # 新しく取得したルートの番号
    curtRouteNum = len(locDfs)-1
    print(intervalsDfs)
    if len(locDfs) >= 2:
        # 2回目以降センシングした場合
        updateCommonTimeFile(locDfs, intervalsDfs, filePath)
    else:
        # 初めてセンシングした場合
        # デバック用        
        createCommonTimeFile(intervalsDfs, filePath)

    print("extractCommonInterval: 終了")    
    return True        

