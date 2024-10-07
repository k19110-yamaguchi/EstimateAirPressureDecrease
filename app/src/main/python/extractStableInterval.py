## pip install pandas
import pandas as pd  # type: ignore


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

# 推定区間に必要な距離(km)
thresholdDis = 0.4  

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

# 走行区間から特徴量を抽出
def extractLocDf(locDf, startTime, stopTime):
    res = locDf[(startTime <= locDf[lh.time]) & (locDf[lh.time] <= stopTime)]
    return res.reset_index(drop=True)

# 区間距離を計算
def calcIntervalsDistance(locDf, startTime, stopTime):    
    res = 0        
    for i, row in locDf.iterrows():
        if startTime <= row[lh.time] and row[lh.time] <= stopTime:
            res += row[lh.dis]        
    return res

# 最大共通区間数を取得
# csvに保存された共通時間をListに変換
def convertCsvToList(csvDf, intervalsCounts):
    resCommTimeList = []
    intervalsNum1 = 0
    num1 = 0
    __res = [] 
    resCommonCounts = []
    for i, row in csvDf.iterrows():
        commonCount = 0
        if i != 0:
            _res = []                     
            __res2 = []           
            intervalsNum2 = 0
            num2 = 0
            for j, cell in enumerate(row):
                if j != 0:                    
                    if num2 >= intervalsCounts[intervalsNum2]:
                        num2 = 0
                        intervalsNum2 = intervalsNum2 + 1
                        _res.append(__res2)
                        # 共通の個数を調べる
                        for r in __res2:
                            if r > -1:
                                commonCount = commonCount + 1 
                                break
                        __res2 = []
                                             
                        
                    __res2.append(float(cell))  
                                 
                    num2 = num2 + 1
            # 共通の個数を調べる        
            for r in __res2:
                if r > -1:
                    commonCount = commonCount + 1 
                    break
            _res.append(__res2)    
            if num1 >= intervalsCounts[intervalsNum1]:
                num1 = 0
                intervalsNum1 = intervalsNum1 + 1
                resCommTimeList.append(__res)
                __res = []                     
                        
            __res.append(_res)                
            num1 = num1 + 1            
            resCommonCounts.append([intervalsNum1, num1-1, commonCount]) 
            print(f"[{intervalsNum1}][{num1-1}]の共通区間数: {commonCount}")
                                      
    resCommTimeList.append(__res)
    return resCommTimeList, resCommonCounts

# 最大共通区間数の取得
def getMaxCommonIntervalsCount(commonCounts):    
    res = 0
    for count in commonCounts:    
        if float(count[2]) > res:        
            res = float(count[2])
    return res

'''
def withinAvailableRouteCount():
    # 適正内，外の利用できるルートの数
    tmp = -1
    withinAvailableRouteCount = 0
    outOfAvailableRouteCount = 0
    for srn, sin  in zip(stableRouteNums, stableIntervalNums):       
        if tmp != srn:            
            tmp = srn                        
            if sensingAirPressures[srn] >= minProperPressure:
                withinAvailableRouteCount = withinAvailableRouteCount + 1            
            else:
                outOfAvailableRouteCount = outOfAvailableRouteCount + 1

        print(f"[{srn}][{sin}]->", end="")
    print("")

    print(f"withinAvailableRouteCount: {withinAvailableRouteCount}")
    print(f"outOfAvailableRouteCount: {outOfAvailableRouteCount}")

    # 安定区間を抽出するのに必要なデータ数があるか
    if withinAvailableRouteCount >= requiredRouteCount and outOfAvailableRouteCount >= requiredRouteCount:
        # todo: 安定区間の抽出    
        print("安定区間の抽出")
    else: 
        print("データ数が足りない")
'''

## 安定区間の抽出
def extractStableInterval(sensingDatesArray, filePath2):
    print("extractStableInterval: 開始")
    # JavaList→Listに変換       
    # sensingDates = changeJavaList(sensingDatesArray)         
     
    # デバック
    import os
    # スクリプトのディレクトリに移動
    os.chdir(os.path.dirname(os.path.abspath(__file__)))   
    # ファイルパスの取得     
    sensingDates = ["20240709073515", "20240709185604", "20240714074015", "20240716161222", 
                    "20240718073501", "20240718145520", "20240723091223", "20240723175341", 
                    "20240724073752", "20240724161042", "20240725073402", "20240730083658",
                    "20240730162752", "20240806182328", "20240821154530", "20240903082133",
                    "20240903133519", "20240910072425"
    ]
    sensingAirPressures = [300, 300, 300, 300,
                           273, 273, 263, 263,
                           263, 263, 263, 235,
                           235, 213, 153, 294,
                           294, 261
    ]  
    filePath = "./sensorData"          

    # 保存した位置情報，区間データを取得
    locDfs, intervalsDfs = getAllLocDfs(filePath, sensingDates)        

    # ルートごとの区間数
    intervalsCounts = []
    #全ての区間数
    allIntervalsCount = 0
    for i, intervalsDf in enumerate(intervalsDfs):
        intervalsCount = len(intervalsDf)
        allIntervalsCount = allIntervalsCount + intervalsCount
        intervalsCounts.append(intervalsCount)

    # 共通区間の開始，終了時間の取得
    commonStartTimeCsvDf = pd.read_csv(f"{filePath}/commonStartTime.csv", header=None)
    commonStopTimeCsvDf = pd.read_csv(f"{filePath}/commonStopTime.csv", header=None)

    # csvに保存された共通時間をListに変換
    # commonCounts = [ルート番号，区間番号，共通区間数]
    commonStartTimeList, commonIntervalsCounts = convertCsvToList(commonStartTimeCsvDf, intervalsCounts)
    commonStopTimeList, commonIntervalsCounts = convertCsvToList(commonStopTimeCsvDf, intervalsCounts)

    # 最大共通区間数の区間を抽出
    maxCommonIntervalsCount = getMaxCommonIntervalsCount(commonIntervalsCounts)
    print(maxCommonIntervalsCount)    

    # 最大共通区間数の区間を抽出    
    stableRouteNums = []
    stableIntervalNums = []
    for cic in commonIntervalsCounts:
        if cic[2] >= maxCommonIntervalsCount:
            stableRouteNums.append(cic[0])
            stableIntervalNums.append(cic[1])                 
    print(f"最大共通数: {round(maxCommonIntervalsCount)}/{len(locDfs)-1}")  

    # todo: 最大共通区間が同じルートに複数あった場合の処理
    # 共通している数が最大のものから推定区間を抽出    
    siStartTime = -1
    siStopTime = -1    
    for srn, sin  in zip(stableRouteNums, stableIntervalNums):
        if siStartTime == -1:        
            siStartTime = intervalsDfs[srn][ih.startTime][sin]
            siStopTime = intervalsDfs[srn][ih.stopTime][sin]
            print(f"{siStartTime} ~ {siStopTime}")
            if siStartTime > siStopTime:
                tmp = siStartTime
                siStartTime = siStopTime
                siStopTime = tmp
        else:      
            commonStartTime = commonStartTimeList[stableRouteNums[0]][stableIntervalNums[0]][srn][sin]
            commonStopTime = commonStopTimeList[stableRouteNums[0]][stableIntervalNums[0]][srn][sin]
            if commonStartTime > commonStopTime:
                tmp = commonStartTime
                commonStartTime = commonStopTime
                commonStopTime = tmp
            
            if commonStartTime > siStartTime:
                siStartTime = commonStartTime
            if commonStopTime < siStopTime:
                siStopTime = commonStopTime
    
    # 安定区間のデータを抽出
    siLocDf = extractLocDf(locDfs[stableRouteNums[0]], siStartTime, siStopTime)
    # 安定区間の距離を抽出
    siDis = calcIntervalsDistance(locDfs[stableRouteNums[0]], siStartTime, siStopTime)

    print(f"安定区間距離: {round(siDis*1000, 1)}m")    
    if siDis > thresholdDis:
        siStartLat = siLocDf[lh.lat].iloc[0]
        siStartLon = siLocDf[lh.lon].iloc[0]
        siStopLat = siLocDf[lh.lat].iloc[-1]
        siStopLon = siLocDf[lh.lon].iloc[-1]
        print(f"安定区間位置情報: [{siStartLat}, {siStartLon}] ~ [{siStopLat}, {siStopLon}]")     
        print("extractStableInterval: 終了")  
        return [sensingDates[stableRouteNums[0]], siStartTime, siStopTime]
    else:
        print(f"安定区間に必要な距離が足りない")
        print("extractStableInterval: 終了")  
        return True

def getAvailableRouteCount(sensingDatesArray, siFileName, siStartTime, siStopTime, sensingAirPressureArray, minProperPressure, requiredRouteCount, filePath2):    
    print("getAvailableRouteCount: 開始")
    # JavaList→Listに変換       
    # sensingDates = changeJavaList(sensingDatesArray)   
    # sensingAirPressures = changeJavaList(sensingAirPressureArray)
    
    # デバック
    import os
    # スクリプトのディレクトリに移動
    os.chdir(os.path.dirname(os.path.abspath(__file__)))   
    # ファイルパスの取得     
    sensingDates = ["20240709073515", "20240709185604", "20240714074015", "20240716161222", 
                    "20240718073501", "20240718145520", "20240723091223", "20240723175341", 
                    "20240724073752", "20240724161042", "20240725073402", "20240730083658",
                    "20240730162752", "20240806182328", "20240821154530", "20240903082133",
                    "20240903133519", "20240910072425"
    ]
    sensingAirPressures = [300, 300, 300, 300,
                           273, 273, 263, 263,
                           263, 263, 263, 235,
                           235, 213, 153, 294,
                           294, 261
    ]  
    filePath = "./sensorData"          
    print("extractStableInterval: 終了")  
    return True

    
