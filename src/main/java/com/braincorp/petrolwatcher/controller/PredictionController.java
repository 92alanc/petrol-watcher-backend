package com.braincorp.petrolwatcher.controller;

import com.braincorp.petrolwatcher.callback.PredictionCallback;
import com.braincorp.petrolwatcher.utils.JsonConverter;
import com.braincorp.petrolwatcher.utils.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;

public class PredictionController {

    private Logger logger = LoggerFactory.getLogger(PredictionController.class);
    private PropertiesReader propertiesReader = new PropertiesReader();

    /**
     * Runs the AI script to train the neural network
     * with the data set provided
     */
    public void runAiScript(String dataSetFile, String city, String country) {
        URL scriptUrl = getClass().getClassLoader().getResource("ai.py");
        if (scriptUrl == null) {
            logger.error("Wrong AI script path!");
            return;
        }

        String aiScript = scriptUrl.toString();
        String command = String.format("python3 %1$s %2$s %3$s %4$s",
                aiScript,
                dataSetFile,
                city,
                country);

        new Thread() {
            @Override
            public synchronized void start() {
                super.start();
                try {
                    logger.info("Running AI script...");
                    Runtime.getRuntime().exec(command);
                } catch (IOException e) {
                    logger.error("Error running AI script.", e);
                }
            }
        }.start();
    }

    /**
     * Watches the directory where the prediction JSON file
     * will be generated. Once the file is ready, the JSON
     * is parsed into a {@link com.braincorp.petrolwatcher.model.Prediction}
     * object
     * @param callback the callback to be triggered once the
     *                 prediction JSON file is generated and
     *                 parsed
     */
    public void getPrediction(PredictionCallback callback) {
        new Thread() {
            @Override
            public synchronized void start() {
                super.start();
                try {
                    WatchService watchService = FileSystems.getDefault().newWatchService();
                    Path predictionsDir = Paths.get(System.getProperty("user.dir"));
                    predictionsDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            logger.info(event.kind().name());
                            String predictionsFile = propertiesReader.getPredictionsFile();
                            JsonConverter jsonConverter = new JsonConverter(predictionsFile);
                            callback.onNewPrediction(jsonConverter.toPrediction());
                        }
                        key.reset();
                    }
                } catch (IOException | InterruptedException e) {
                    logger.error("Error finding or parsing results file.", e);
                }
            }
        }.start();
    }

}
