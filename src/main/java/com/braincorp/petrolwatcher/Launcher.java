package com.braincorp.petrolwatcher;

import com.braincorp.petrolwatcher.controller.DatabaseController;
import com.braincorp.petrolwatcher.controller.PredictionController;
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

    private static Logger logger = LoggerFactory.getLogger(Launcher.class);

    public static void main(String[] args) {
        initialiseFirebaseApp();
        logger.info("Backend started");
        PredictionController predictionController = new PredictionController();
        predictionController.runAiScript();
        predictionController.getPrediction((prediction) -> {
            logger.info("Predictions ready");
            logger.info("Updating database...");

            DatabaseController databaseController = new DatabaseController();
            databaseController.updatePrediction(prediction);
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
            logger.error("Error", e);
        }
    }

}
