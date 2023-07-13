import numpy as np

def create_random_number():
    return np.random.rand()

def getCsv():
    filePath = "/storage/emulated/0/Android/data/com.example.estimateairpressuredecrease/files/output.csv"

    with open(filePath, 'r') as file:
        lines = file.readlines()


    return lines