
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

def extractCommonIntervals(sensingDatesArray, filePath):    
    print("extractCommonInterval: 開始")  
    # JavaList→Listに変換       
    sensingDates = changeJavaList(sensingDatesArray)
    
    # 保存した位置情報，区間データを取得
    locDfs, intervalsDfs = getAllLocDfs(filePath, sensingDates)
    print(locDfs)

    print("extractCommonInterval: 終了")    
    return True        