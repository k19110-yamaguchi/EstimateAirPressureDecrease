## pip install pandas
import pandas as pd  # type: ignore

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

    print("extractStableInterval: 終了")  
    return True