package com.braincorp.petrolwatcher.callback;

import com.braincorp.petrolwatcher.model.Prediction;

public interface PredictionCallback {
    void onNewPrediction(Prediction prediction, String area);
}
