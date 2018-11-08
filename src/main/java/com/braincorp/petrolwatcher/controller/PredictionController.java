package com.braincorp.petrolwatcher.controller;

import com.braincorp.petrolwatcher.callback.PredictionCallback;
import com.braincorp.petrolwatcher.model.Prediction;
import com.braincorp.petrolwatcher.utils.JsonConverter;
import com.braincorp.petrolwatcher.utils.PropertiesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;

import static com.braincorp.petrolwatcher.utils.TextUtils.normalise;

public class PredictionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredictionController.class);

    private PropertiesReader propertiesReader = new PropertiesReader();
    private String city;
    private String country;

    /**
     * Runs the AI script to train the neural network
     * with the data set provided
     */
    public void runAiScript(String dataSetFile, String city, String country) {
        city = normalise(city);
        country = normalise(country);

        this.city = city;
        this.country = country;

        String scriptFile = propertiesReader.getScriptFile();
        String command = String.format("python3 %1$s %2$s %3$s %4$s",
                scriptFile,
                dataSetFile,
                city,
                country);
        String commandMsg = String.format("Command: %s", command);
        LOGGER.info(commandMsg);

        try {
            LOGGER.info("Running AI script...");
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            LOGGER.error("Error running AI script.", e);
        }
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
                            LOGGER.info(event.kind().name());
                            String predictionsFile = String.format("results_%1$s_%2$s.json",
                                    city, country);
                            String area = String.format("%1$s_%2$s", city, country);
                            JsonConverter jsonConverter = new JsonConverter(predictionsFile);
                            Prediction prediction = jsonConverter.toPrediction();
                            prediction.setArea(area);
                            callback.onNewPrediction(prediction);
                        }
                        key.reset();
                    }
                } catch (IOException | InterruptedException e) {
                    LOGGER.error("Error finding or parsing results file.", e);
                }
            }
        }.start();
    }

}
