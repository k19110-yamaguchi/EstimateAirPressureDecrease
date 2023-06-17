# EstimateAirPressureDecrease

## データベース

sensorData

| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| startDate |  Long? |　 データを取得を開始した時間 |　yyyy-MM-dd hh：mm：ss |
| stopDate |  Long? |　 データを取得を終了した時間 |　yyyy-MM-dd hh：mm：ss |
| yAccList | ArrayList(Double) | 加速度データ | |
| accTimeList | ArrayList(Long) | 加速度を取得した時間 | |
| latList | ArrayList(Double) | 緯度データ | |
| lonList | ArrayList(Double) | 経度データ | |
| lcationTimeList | ArrayList(Long?) | 位置情報を取得した時間 | |
| airPressure | Int | 空気圧 | |

featureData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| sencorId | Int | 特徴量を抽出したデータのid |  |
| accSd | Double | 加速度標準偏差 |  |
| ampSptList | ArrayList(Double) | 0~40Hzの振幅スペクトル |

色々Data
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| isTrainingState | Boolean | 学習状態かどうか | false:推定状態 |
| minProperPressure | Int | 最小適正空気圧 |  |
| inflatedDate | Long | 空気を注入した時間 |

　
