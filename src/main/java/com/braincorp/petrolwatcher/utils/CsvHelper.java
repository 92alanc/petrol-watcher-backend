package com.braincorp.petrolwatcher.utils;

import com.braincorp.petrolwatcher.model.AveragePrice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvHelper.class);

    @Nullable
    public static String getFileFor(@NotNull List<AveragePrice> prices) {
        List<String> fuels = new ArrayList<>();
        List<String> values = new ArrayList<>();

        prices.forEach(price -> {
            fuels.add(price.getFuel());
            values.add(price.getPrice());
        });

        try {
            String fileName = String.format("precos_%1$s_%2$s.csv",
                    prices.get(0).getCity().replace(" ", "").toLowerCase(),
                    prices.get(0).getCountry().replace(" ", "").toLowerCase());
            File file = new File(fileName);
            FileWriter writer = null;

            if (!file.exists()) {
                writer = new FileWriter(fileName);
                String headers = String.join(",", fuels);
                writer.write(headers);
            }

            if (writer == null)
                writer = new FileWriter(fileName);
            String line = String.format("\n%s", String.join(",", values));
            writer.append(line);
            writer.close();

            return fileName;
        } catch (IOException e) {
            LOGGER.error("Error writing to CSV file", e);
            return null;
        }
    }

}
