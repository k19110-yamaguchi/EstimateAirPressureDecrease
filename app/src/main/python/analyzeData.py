import numpy as np # type: ignore
import pandas as pd  # type: ignore
from statistics import stdev

class AccHeader:
    def __init__(self,time="time(s)",x="x(m/s^2)",y="y(m/s^2)",z="z(m/s^2)"):
        self.time = time
        self.x = x
        self.y = y
        self.z = z      

ah = AccHeader()

# java.util.ArrayList(1次元)をlistの型に変換
def changeJavaList(javaList):
    pyList = [javaList.get(i) for i in range(javaList.size())]                    
    return pyList

# 加速度データを全て取得
def getAllSiAccDfs(filePath, sensingDates):
    res = []
    for sd in sensingDates:
        accDf = pd.read_csv(f"{filePath}/siAcc/{sd}.csv") 
        res.append(accDf)
    return res

# 走行中のy軸方向の加速度を取得
def getDrivingYAcc(siAccDf):    
    res = []        
    for i, row in siAccDf.iterrows():                    
        # 重力加速度成分を除く
        res.append(row[ah.y]-9.8)        

    return res
    
# 振幅スペクトルを取得 
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
    fqMax = 25
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
    
# 特徴量のファイル作成    
def cereateFvCsv(featureValues, filePath):     
    header = ["accSd(m/s^2)"] 
    # 周波数の範囲
    fqWidth = 0.5    
    fqMax = 25
    for i in range(int(fqMax/fqWidth)):
        header.append(str(i*fqWidth) + "~" + str((i+1)*fqWidth) + "(Hz)")
    header.append("airPressure(kPa)")

    fvDf = pd.DataFrame(columns = header)
    #print(len(fvData))
    for fv in featureValues:        
        s = pd.Series(fv, index=header)
        fvDf = pd.concat([fvDf, s.to_frame().T])    

    print(fvDf)

    # CSV ファイル (employee.csv) として出力
    fvDf.to_csv(f"{filePath}/featureValue.csv", index = False)
    
    print("hoge")  
    

def createFeatureValue(availableFileNameArray, sensingAirPressuresArray, filePath): 
    print("createFeatureValue: 開始")        
    # java.util.ArrayListをlistの型に変換
    availableFileNames = changeJavaList(availableFileNameArray) 
    sensingAirPressures = changeJavaList(sensingAirPressuresArray)     
    sensingAirPressures = [
        300, 300, 300, 300,
        273, 273, 263, 263, 
        263, 263, 235, 213, 
        153, 294,294, 261
    ]      

    siAccDfs = getAllSiAccDfs(filePath, availableFileNames)  
    featureValues = []
    print(siAccDfs[0]) 
    for i in range(len(siAccDfs)):
        drivingYAcc = getDrivingYAcc(siAccDfs[i])  
        print(f"長さ：{len(drivingYAcc)}")
    
        # 加速度標準偏差の取得
        if len(drivingYAcc) >= 2:
            AccSd = stdev(drivingYAcc)        

            # 振幅スペクトルを取得 
            ampSpec = getAmpSpec(drivingYAcc)
                    
            # 特徴量の行を作成
            featureValue = [AccSd]
            for j in range(len(ampSpec)):
                featureValue.append(ampSpec[j])  
            featureValue.append(sensingAirPressures[i])          
            print(featureValue)
            featureValues.append(featureValue)
    
    # 特徴量のファイル作成
    cereateFvCsv(featureValues, filePath)
            
    
    print("createFeatureValue: 終了")        
    return True