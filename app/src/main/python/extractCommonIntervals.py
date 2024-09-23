
# ライブラリの読み込み
## pip install pandas
import pandas as pd  # type: ignore
## pip install pyproj
from pyproj import Transformer
import numpy as np # type: ignore
import csv

# java.util.ArrayList(1次元)をlistの型に変換
def changeJavaList(javaList):
    pyList = [javaList.get(i) for i in range(javaList.size())]                    
    return pyList

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

# 共通区間のファイルを更新
def updateCommonTimeFile():
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
    sensingDates = ["20240709073515"]
    
    # 保存した位置情報，区間データを取得
    # デバック用
    locDfs, intervalsDfs = getAllLocDfs("sensorData", sensingDates)    
    #locDfs, intervalsDfs = getAllLocDfs(filePath, sensingDates)    
    # 新しく取得したルートの番号
    curtRouteNum = len(locDfs)-1
    print(intervalsDfs)
    if len(locDfs) >= 2:
        # 2回目以降センシングした場合
        updateCommonTimeFile()
    else:
        # 初めてセンシングした場合
        # デバック用        
        createCommonTimeFile(intervalsDfs, filePath)

    print("extractCommonInterval: 終了")    
    return True        