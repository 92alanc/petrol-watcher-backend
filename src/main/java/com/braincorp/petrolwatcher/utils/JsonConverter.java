package com.braincorp.petrolwatcher.utils;

import com.braincorp.petrolwatcher.model.Prediction;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonConverter {

    private Gson gson;
    private String json;

    public JsonConverter(String jsonFile) {
        gson = new Gson();
        try {
            byte[] jsonBytes = Files.readAllBytes(Paths.get(jsonFile));
            json = new String(jsonBytes);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(JsonConverter.class);
            logger.error("JSON file not found", e);
        }
    }

    public Prediction toPrediction() {
        return gson.fromJson(json, Prediction.class);
    }

}
