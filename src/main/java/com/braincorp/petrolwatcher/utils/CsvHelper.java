package com.braincorp.petrolwatcher.utils;

import com.braincorp.petrolwatcher.model.AveragePrice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static com.braincorp.petrolwatcher.utils.TextUtils.normalise;

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
            String city = normalise(prices.get(0).getCity());
            String country;
            if (normalise(prices.get(0).getCountry()).equals("Brazil"))
                country = "Brasil";
            else
                country = normalise(prices.get(0).getCountry());

            String fileName = String.format("precos_%1$s_%2$s.csv",
                    city,
                    country);
            File file = new File(fileName);
            String week = file.exists() ? String.valueOf(getNewWeekNumber(fileName)) : "1";

            if (!file.exists()) {
                if (file.createNewFile()) {
                    String headers = String.join(",", fuels).concat(",SEMANA");
                    Files.write(Paths.get(fileName), headers.getBytes(), StandardOpenOption.APPEND);
                }
            }

            String line = String.format("\n%1$s,%2$s",
                    String.join(",", values),
                    week);
            Files.write(Paths.get(fileName), line.getBytes(), StandardOpenOption.APPEND);

            return fileName;
        } catch (IOException e) {
            LOGGER.error("Error writing to CSV file", e);
            return null;
        }
    }

    private static int getNewWeekNumber(String fileName) throws IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];

            int readChars = is.read(c);
            if (readChars == -1) {
                // bail out if nothing to read
                return 0;
            }

            // make it easy for the optimizer to tune this loop
            int count = 0;
            while (readChars == 1024) {
                for (int i = 0; i < 1024; ) {
                    if (c[i++] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            // count remaining characters
            while (readChars != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
                readChars = is.read(c);
            }

            return count == 0 ? 1 : count + 1;
        }
    }

}
