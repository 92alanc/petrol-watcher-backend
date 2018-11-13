package com.braincorp.petrolwatcher;

import com.braincorp.petrolwatcher.controller.DatabaseController;
import com.braincorp.petrolwatcher.controller.PredictionController;
import com.braincorp.petrolwatcher.utils.CsvHelper;
import com.braincorp.petrolwatcher.utils.PropertiesReader;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        initialiseFirebaseApp();
        LOGGER.info("Backend started");
        DatabaseController databaseController = new DatabaseController();
        databaseController.fetchAveragePrices((prices, city, country) -> {
            String dataSetFile = CsvHelper.getFileFor(prices);
            if (dataSetFile != null) {
                PredictionController predictionController = new PredictionController();
                predictionController.runAiScript(dataSetFile, city, country);
                predictionController.getPrediction(prediction -> {
                    LOGGER.info("Predictions ready");
                    databaseController.updatePrediction(prediction, (databaseError, databaseReference) -> LOGGER.info("Predictions exported"));
                });
            }
        });
        Scanner s = new Scanner(System.in);
        s.next();
    }

    private static void initialiseFirebaseApp() {
        try {
            PropertiesReader propertiesReader = new PropertiesReader();
            FileInputStream serviceAccount = new FileInputStream(propertiesReader.getFirebaseKeyFile());
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(propertiesReader.getFirebaseDatabaseUrl())
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            LOGGER.error("Error", e);
        }
    }

}
