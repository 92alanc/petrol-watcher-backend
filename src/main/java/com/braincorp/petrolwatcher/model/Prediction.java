package com.braincorp.petrolwatcher.model;

import com.braincorp.petrolwatcher.utils.MapBuilder;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Map;

public class Prediction {

    private static final String KEY_DIESEL = "diesel";
    private static final String KEY_ETHANOL = "ethanol";
    private static final String KEY_PETROL_REGULAR = "petrol_regular";
    private static final String KEY_PETROL_PREMIUM = "petrol_premium";
    private static final String KEY_AREA = "area";

    @SerializedName("DIESEL")
    private double diesel;

    @SerializedName("ETANOL")
    private double ethanol;

    @SerializedName("GASOLINA COMUM")
    private double regularPetrol;

    @SerializedName("GASOLINA ADITIVADA")
    private double premiumPetrol;

    @JsonIgnore
    private String area;

    /**
     * Converts the object to a map in order
     * to be processed by the Firebase database
     * engine
     * @return the current object as a map
     */
    public Map<String, Object> toMap() {
        MapBuilder<String, Object> mapBuilder = new MapBuilder<>();
        return mapBuilder.put(KEY_DIESEL, diesel)
                .put(KEY_ETHANOL, ethanol)
                .put(KEY_PETROL_REGULAR, regularPetrol)
                .put(KEY_PETROL_PREMIUM, premiumPetrol)
                .put(KEY_AREA, area)
                .build();
    }

    public String getArea() {
        return area;
    }

    /* **********************************************************
     * These getters and setters below are not explicitly being *
     * used but they are necessary for when the prediction JSON *
     * file is parsed into this object                          *
     ************************************************************/

    @SuppressWarnings("unused")
    public double getDiesel() {
        return diesel;
    }

    @SuppressWarnings("unused")
    public void setDiesel(double diesel) {
        this.diesel = diesel;
    }

    @SuppressWarnings("unused")
    public double getEthanol() {
        return ethanol;
    }

    @SuppressWarnings("unused")
    public void setEthanol(double ethanol) {
        this.ethanol = ethanol;
    }

    @SuppressWarnings("unused")
    public double getRegularPetrol() {
        return regularPetrol;
    }

    @SuppressWarnings("unused")
    public void setRegularPetrol(double regularPetrol) {
        this.regularPetrol = regularPetrol;
    }

    @SuppressWarnings("unused")
    public double getPremiumPetrol() {
        return premiumPetrol;
    }

    @SuppressWarnings("unused")
    public void setPremiumPetrol(double premiumPetrol) {
        this.premiumPetrol = premiumPetrol;
    }

}
