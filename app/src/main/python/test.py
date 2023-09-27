import numpy as np
import pandas as pd
import os
from geopy.distance import geodesic
from statistics import stdev

from sklearn.ensemble import RandomForestRegressor
from sklearn.model_selection import KFold



# 特徴量の
AIR_PRESSURE_COLUMN = "air_pressure"
SUST_SPEED_COLUMN = "sust_speed"
ACC_SD_COLUMN = 'acc_sd'



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

# java.util.ArrayListをlistの型に変換
def changeJavaList(javaList):
    pyList = [[javaList.get(i).get(j) for j in range(javaList.get(i).size())] 
                for i in range(javaList.size())]   
    return pyList

def createEVColum():
    columList = []    
    columList.append(ACC_SD_COLUMN)
    for i in range(int(maxFq/fqWidth)):
        columList.append(str(i*fqWidth) + "~" + str((i+1)*fqWidth) + "Hz")    
    return columList
    

#回帰
def RFR(trainEV, trainOV, testEV):    
    # 学習モデルを作成(ランダムフォレスト：回帰)        
    model = RandomForestRegressor(random_state=0)

    # 学習モデルにテストデータを与えて学習させる
    model.fit(trainEV, trainOV)        

    # 学習させたモデルを使ってテストデータに対する予測を出力する
    pred = model.predict(testEV)                          




def estimateAirPressure(FeatureValueArray):    
    # 初期の特徴量データの取得
    df = pd.read_csv(initFilePath)    
    # 初期の説明変数の取得
    ev = df.drop(SUST_SPEED_COLUMN, axis=1).drop(AIR_PRESSURE_COLUMN, axis=1)
    # 初期の目的変数の取得
    ov = df[AIR_PRESSURE_COLUMN]
    # 目的変数の
    evColumn = createEVColum()

    # 追加で集めた特徴量データの取得
    #featureValueData = changeJavaList(FeatureValueArray)    
    featureValueData = [
        [5.685614395437727,0.019888394686261187,0.016582451940548595,0.02553055196464995,0.020591944675981133,0.027350864739222984,0.0426802219796937,0.059040812747360255,0.05504769131104991,0.04557572605537938,0.04971092090601084,0.07062814000806537,0.07116337307742647,0.07954960079206946,0.06883955206651306,0.08033591980753059,0.08576086994785694,0.11957613228483102,0.16596576001490254,0.1642359185323246,0.1784417658501968,0.18783876910054678,0.23094099333690987,0.2645792514932042,0.21046093059862342,0.29878647395591185,0.1981918444494015,0.2394964482843532,0.2921746013568906,0.3288902883618186,0.2990798842256273,0.36297419161015293,0.25967216560409506,0.2605316092863592,0.21296700177570477,0.24979664660873757,0.23929638225218045,0.19998316733199245,0.16195978302071248,0.13709209554559343,0.1873280436083882,0.18342684390350636,0.22585021080573384,0.18466529826982334,0.22622042910223522,0.20790566279420927,0.2160176422976639,0.1881509810968886,0.13240601755269518,0.08264573139158861,0.06601340087104923,0.03959221366195698,0.03703829545055714,0.02876137531846776,0.020674444117405554,0.024364388153634314,0.030310700731961897,0.030018582763258528,0.036461651654668865,0.031236608543463745,0.030875800002398136,0.02982254910896783,0.02382699522506412,0.023784174579222134,0.02101705026274706,0.01776176237687328,0.018319967184625332,0.018533445917499804,0.021521775664205853,0.01842650689929004,0.016727923366390746,0.014840463276667543,0.01304968425454703,0.012914455005471412,0.014610577837351443,0.012018047538355368,0.012308152027763204,0.012024660240016243,0.012511720539341522,0.01016777125519704,0.014888036652623423,300.0],
        [3.755254909246834,0.019028085411784062,0.019701211367060725,0.020363882749585827,0.021770964649775005,0.020362759449909968,0.023482448567674306,0.035851540754496015,0.04493423290044574,0.043029191685139245,0.04531534626898974,0.06034401338764481,0.048225410448257185,0.043903475887395335,0.05757836862564552,0.06985704443849963,0.057912633239444386,0.06635845026887977,0.08487228631040068,0.08297749209198113,0.11285280456827514,0.10667937346488718,0.09622103513548719,0.10928668029980126,0.10759796231815635,0.10175547874545064,0.10171188389588494,0.16088453494759716,0.16384084864313964,0.23763902915329785,0.20937909134885932,0.20265123278107353,0.15336276715640434,0.17767321101821176,0.14159692092301576,0.15823656508821737,0.14221249163928795,0.11449609774275198,0.10919169288554455,0.09657981894276217,0.13343757246282395,0.1419750874045952,0.13601476660488776,0.12018531396661494,0.12465175291926515,0.0992321051202511,0.09861640845726255,0.08003565588284996,0.0840839761626388,0.06265052461907186,0.03726816561320408,0.026540265580715452,0.018288319694122493,0.012311284988964475,0.008401977289731691,0.013934403717725091,0.024796364769191406,0.020422068211547934,0.019047615332334157,0.029934341080463167,0.018626602675551017,0.0212813517739906,0.014529625207838327,0.017638396501246573,0.015548317098657418,0.014885684601137953,0.01393400997747369,0.014478299067065983,0.012238847479411865,0.012208158133500933,0.010721199602276518,0.007712097163277704,0.008876107772453694,0.0066627044865283785,0.007861166139365262,0.007109552797914115,0.006760091793215622,0.0055138538417954415,0.006982858660533423,0.006233179791952892,0.006922007562183743,300.0]
    ]    
    featureValueSize = len(featureValueData)    
    
    for i in range(featureValueSize):  
        # 配列をDataFrameの型にする
        l = featureValueData[i]
        l.pop(-1)                
        df1 = pd.DataFrame([l], columns=evColumn)        
        # 出力するDataFrameに結合
        ev = pd.concat([ev, df1], ignore_index=True)      
        l = featureValueData[i][-1]
        df1 = pd.DataFrame([l], columns=[AIR_PRESSURE_COLUMN])
        # 出力するDataFrameに結合
        ov = pd.concat([ov, df1], ignore_index=True)     

            

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


estimateAirPressure([0])
#fv = getFeatureValues(0,0,0,0,300)
#print(fv)
#fileName = printDate("2023-09-15T23:22:58.090723")
#print(fileName)


