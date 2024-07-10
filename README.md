# EstimateAirPressureDecrease

## データベース

SensorData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| startDate | LocalDateTime |　 データを取得を開始した時間 |　yyyy-MM-dd hh：mm：ss |
| stopDate |  LocalDateTime |　 データを取得を終了した時間 |　yyyy-MM-dd hh：mm：ss |
| sensingAirPressure | Int | 測定時空気圧 | |
| estimatedAirPressure | Int | 推定空気圧 | |
| sensorDataPath | String | センサデータのパス | |

HomeData
| 名前 | データタイプ | 役割 | 備考 |
| ---- | ---- | ---- | ---- |
| id | Int | データベースがレコードを管理するための項目 | プライマリキー |
| isTrainingState | Boolean | 学習状態かどうか | false:推定状態 |
| minProperPressure | Int | 最小適正空気圧 |  |
| inflatedDate | LocalDateTime | 空気を注入した時間 |

　
