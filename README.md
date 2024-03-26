# EstimateAirPressureDecrease

## データベース

SensorData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| startDate | LocalDateTime |　 データを取得を開始した時間 |　yyyy-MM-dd hh：mm：ss |
| stopDate |  LocalDateTime |　 データを取得を終了した時間 |　yyyy-MM-dd hh：mm：ss |
| airPressure | Int | 空気圧 | |
| estimatedAirPressure | Int | 推定空気圧 | |

AccData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| xAccList | List(Double) | x軸加速度データ | |
| yAccList | List(Double) | y軸加速度データ | |
| zAccList | List(Double) | z軸加速度データ | |
| timeList | List(Double) | 加速度を取得した時間 | |

GraData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| xAccList | List(Double) | x軸重力加速度データ | |
| yAccList | List(Double) | y軸重力加速度データ | |
| zAccList | List(Double) | z軸重力加速度データ | |
| timeList | List(Double) | 加速度を取得した時間 | |

LocData
| 名前 | データタイプ | 役割 | 備考 |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| ---- | ---- | ---- | ---- |
| latList | List(Double) | 緯度データ | |
| lonList | List(Double) | 経度データ | |
| timeList | List(Double) | 位置情報を取得した時間 | |


featureValueData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| sensorsId | Int | 特徴量を抽出したデータのid |  |
| accSd | Double | 加速度標準偏差 |  |
| ampSptList | List(Double) | 0~40Hzの振幅スペクトル |

HomeData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| isTrainingState | Boolean | 学習状態かどうか | false:推定状態 |
| minProperPressure | Int | 最小適正空気圧 |  |
| inflatedDate | LocalDateTime | 空気を注入した時間 |

　
