## pip install pandas
import pandas as pd  # type: ignore
import os
import math
from pyproj import Transformer

    
class LocHeader:
    def __init__(self,time="time(s)",lat="lat(°)",lon="lon(°)",dis="dis(km)", speed="speed(km/h)"):
        self.time = time
        self.lat = lat
        self.lon = lon
        self.dis = dis
        self.speed = speed 

class AccHeader:
    def __init__(self,time="time(s)",x="x(m/s^2)",y="y(m/s^2)",z="z(m/s^2)"):
        self.time = time
        self.x = x
        self.y = y
        self.z = z           

lh = LocHeader()
ah = AccHeader()

# java.util.ArrayList(1次元)をlistの型に変換
def changeJavaList(javaList):
    pyList = [javaList.get(i) for i in range(javaList.size())]                    
    return pyList

# 位置情報に関するデータを全て取得
def getAllLocDfs(filePath, sensingDates):
    resLocDfs = []    
    for sd in sensingDates:
        locDf = pd.read_csv(f"{filePath}/{sd}/loc.csv")         
        resLocDfs.append(locDf)        
    return resLocDfs

# 加速度データを全て取得
def getAllAccDfs(filePath, filePaths):
    res = []
    for sd in filePaths:
        accDf = pd.read_csv(f"{filePath}/{sd}/acc.csv") 
        res.append(accDf)
    return res

# 走行区間を抽出
def extractLocDf(locDf, startTime, stopTime):
    res = locDf[(startTime <= locDf[lh.time]) & (locDf[lh.time] <= stopTime)]
    return res.reset_index(drop=True)

# 区間内の加速度を抽出
def extractAccDf(accDf, startTime, stopTime):
    res = accDf[(startTime <= accDf[ah.time]) & (accDf[ah.time] <= stopTime)]
    return res.reset_index(drop=True)

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

# 共通地点を計算
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


def cereateExtractAccCsv(accDf, startTime, stopTime, filePath, sensingDate):
    siAccDf = extractAccDf(accDf, startTime, stopTime)
    if not os.path.isdir(f"{filePath}/siAcc"):
        os.makedirs(f"{filePath}/siAcc")
    siAccDf.to_csv(f"{filePath}/siAcc/{sensingDate}.csv", index=False)


# 推定に使用する加速度データを抽出
def extractAccData(availableFileNameArray, siFileName, siStartTime, siStopTime, filePath2):
    print("extractAccData: 開始")
    availableFileNames = changeJavaList(availableFileNameArray)
    print(availableFileNames) 

    # デバック    
    # スクリプトのディレクトリに移動
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    filePath = "./sensorData"          
    
    # 推定に使用する位置情報データを取得
    locDfs = getAllLocDfs(filePath, availableFileNames)   
    accDfs = getAllAccDfs(filePath, availableFileNames)

    siIndex = 0
    
    for i, fn in enumerate(availableFileNames):
        if fn == siFileName:            
            siIndex = i
            break

    # 安定区間のデータを抽出
    siLocDf = extractLocDf(locDfs[siIndex], siStartTime, siStopTime)

    for i, fn in enumerate(availableFileNames):
        print(fn)
        if fn == siFileName:            
            cereateExtractAccCsv(accDfs[siIndex], siStartTime, siStopTime, filePath2, fn)
            
        else:
            curtLocDf = locDfs[i]
            curtAccDf = accDfs[i]
            simPointsDis, simPointsTime = calcSimPoints(siLocDf, curtLocDf)                     
            startTime = simPointsTime[0]
            stopTime = simPointsTime[-1]
            if startTime > stopTime:
                tmp = startTime
                startTime = stopTime
                stopTime = tmp

            print(startTime)
            print(stopTime)
            cereateExtractAccCsv(curtAccDf, startTime, stopTime, filePath2, fn)            

    print("extractAccData: 終了")
    return True