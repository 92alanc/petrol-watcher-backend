package com.braincorp.petrolwatcher.model;

import com.google.firebase.database.DataSnapshot;
import org.jetbrains.annotations.NotNull;

public class AveragePrice {

    private static final String KEY_CITY = "city";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_FUEL_TYPE = "fuel_type";
    private static final String KEY_FUEL_QUALITY = "fuel_quality";
    private static final String KEY_PRICE = "price";

    private String city;
    private String country;
    private String fuel;
    private String price;

    public AveragePrice(@NotNull DataSnapshot snapshot) {
        city = snapshot.child(KEY_CITY).getValue().toString();
        country = snapshot.child(KEY_COUNTRY).getValue().toString();
        price = snapshot.child(KEY_PRICE).getValue().toString();
        String fuelType = snapshot.child(KEY_FUEL_TYPE).getValue().toString();

        if (fuelType.equals("PETROL")) {
            fuel = String.format("%1$s %2$s",
                    fuelType,
                    snapshot.child(KEY_FUEL_QUALITY).getValue().toString());
        } else {
            fuel = fuelType;
        }

        if (fuel.equals("PETROL REGULAR")) {
            fuel = "GASOLINA COMUM";
        } else if (fuel.equals("ETHANOL")) {
            fuel = "ETANOL";
        }
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getFuel() {
        return fuel;
    }

    public String getPrice() {
        return price;
    }

}
