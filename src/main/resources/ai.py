import pandas as pd
import numpy as np
from keras.layers import LSTM, Dense
from keras.models import Sequential
import tensorflow as tf
import time
import sys

np.random.seed(2)
tf.set_random_seed(2)

RESULTS = []

def read_csv(csv_file):
    return pd.read_csv(csv_file, sep=',')
    
def create_model(input_size):
    model = Sequential()
    model.add(LSTM(input_size,return_sequences=True))
    model.add(Dense(1))
    model.compile(loss='mean_squared_error', optimizer='adam')
    return model

def train(DATA):
    fuels = ['ETANOL', 'GASOLINA COMUM', 'GASOLINA ADITIVADA', 'DIESEL']
    weeks = np.array([ [float(d)] for d in DATA['SEMANA']])
    weeks = weeks.reshape((len(weeks),1,1))

    for fuel in fuels:

        prices = np.array([ [float(d)] for d in DATA[fuel]]).reshape((len(weeks),1,1))

        model = create_model(len(weeks))
        model.fit(weeks, prices, epochs=300, batch_size=2, shuffle=False, verbose=0)

        next_week = np.array([max(weeks)[0] + 1]).reshape((1,1,1))
        fuel_price_pred = model.predict(next_week, verbose=0)[0][0]
        append_result(fuel, fuel_price_pred)
        
def append_result(fuel, price):
    obj = '"%s":%.4f' % (fuel, price)
    RESULTS.append(obj)

def send_results():
    open('results.json','w').write(str(RESULTS)
    .replace("'",'')
    .replace('[','{')
    .replace(']','}'))

if __name__ == '__main__':
    filename = sys.argv[1]
    data = read_csv(filename)
    train(data)
    send_results()