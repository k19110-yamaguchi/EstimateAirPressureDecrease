
# java.util.ArrayList(1次元)をlistの型に変換
def changeJavaList(javaList):
    pyList = [javaList.get(i) for i in range(javaList.size())]                    
    return pyList

def extractAccData(availableFileNameArray, siFileName, siStartTime, siStopTime, filePath):
    print("extractAccData: 開始")
    availableFileNames = changeJavaList(availableFileNameArray)
    print(availableFileNames)    
    
    print("extractAccData: 終了")
    return True