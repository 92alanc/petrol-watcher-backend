package com.braincorp.petrolwatcher.controller;

import com.braincorp.petrolwatcher.callback.AveragePriceCallback;
import com.braincorp.petrolwatcher.model.AveragePrice;
import com.braincorp.petrolwatcher.model.Prediction;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseController {

    private static final String KEY_AVERAGE_PRICES = "average_prices";
    private static final String KEY_PREDICTIONS = "predictions";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseController.class);

    private String city, country;
    private List<AveragePrice> prices;

    public void fetchAveragePrices(AveragePriceCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(KEY_AVERAGE_PRICES);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AveragePrice> allPrices = new ArrayList<>();
                List<String> citiesAndCountries = new ArrayList<>();
                prices = new ArrayList<>();
                dataSnapshot.getChildren().forEach(it -> {
                    AveragePrice averagePrice = new AveragePrice(it);
                    allPrices.add(averagePrice);
                    LOGGER.info("Price found: " + averagePrice.getPrice());
                    String cityAndCountry = String.format("%1$s_%2$s",
                            averagePrice.getCity(),
                            averagePrice.getCountry());
                    LOGGER.info("City and country: " + cityAndCountry);
                    if (!citiesAndCountries.contains(cityAndCountry))
                        citiesAndCountries.add(cityAndCountry);
                });

                citiesAndCountries.forEach(cityAndCountry -> {
                    allPrices.forEach(price -> {
                        String cityAndCountry2 = String.format("%1$s_%2$s",
                                price.getCity(),
                                price.getCountry());
                        if (cityAndCountry2.equals(cityAndCountry)) {
                            prices.add(price);
                            LOGGER.info("Price in city and country: " + price.getPrice());
                        }
                    });
                    city = cityAndCountry.split("_")[0];
                    if (cityAndCountry.split("_")[1].equals("Brazil"))
                        country = "Brasil";
                    else
                        country = cityAndCountry.split("_")[1];
                    LOGGER.info("Area: " + cityAndCountry);
                });

                LOGGER.info("Prices found: " + prices.size());
                if (prices.size() == 4)
                    callback.onAveragePricesReceived(prices, city, country);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.error("Error fetching average prices", databaseError.toException());
            }
        });
    }

    /**
     * Updates the prediction in the database
     * @param prediction the new prediction
     */
    public void updatePrediction(Prediction prediction) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(KEY_PREDICTIONS);
        reference.child(prediction.getArea()).setValueAsync(prediction.toMap());
    }

}
