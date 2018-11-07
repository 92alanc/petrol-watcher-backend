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

    public void fetchAveragePrices(AveragePriceCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(KEY_AVERAGE_PRICES);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<AveragePrice> allPrices = new ArrayList<>();
                List<String> citiesAndCountries = new ArrayList<>();
                dataSnapshot.getChildren().forEach(it -> {
                    AveragePrice averagePrice = new AveragePrice(it);
                    allPrices.add(averagePrice);
                    String cityAndCountry = String.format("%1$s_%2$s",
                            averagePrice.getCity(),
                            averagePrice.getCountry());
                    if (!citiesAndCountries.contains(cityAndCountry))
                        citiesAndCountries.add(cityAndCountry);
                });

                citiesAndCountries.forEach(cityAndCountry -> {
                    List<AveragePrice> pricesInCityAndCountry = new ArrayList<>();
                    allPrices.forEach(price -> {
                        String cityAndCountry2 = String.format("%1$s_%2$s",
                                price.getCity(),
                                price.getCountry());
                        if (cityAndCountry2.equals(cityAndCountry))
                            pricesInCityAndCountry.add(price);
                    });
                    String city = cityAndCountry.split("_")[0];
                    String country = cityAndCountry.split("_")[1];
                    callback.onAveragePricesReceived(pricesInCityAndCountry, city, country);
                });
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
     * @param area the area (city_country)
     */
    public void updatePrediction(Prediction prediction, String area) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(KEY_PREDICTIONS);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(area).exists())
                    update(prediction, reference);
                else
                    insert(prediction, reference);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                LOGGER.error("Error updating database", databaseError.toException());
            }
        });
    }

    private void insert(Prediction prediction, DatabaseReference reference) {
        reference.child(prediction.getArea())
                .setValue(prediction.toMap(), completionListener);
    }

    private void update(Prediction prediction, DatabaseReference reference) {
        reference.child(prediction.getArea())
                .updateChildren(prediction.toMap(), completionListener);
    }

    private DatabaseReference.CompletionListener completionListener = (databaseError, databaseReference) -> {
        if (databaseError != null)
            LOGGER.error("Error updating database", databaseError.toException());
        else
            LOGGER.info("Predictions updated");
    };

}
