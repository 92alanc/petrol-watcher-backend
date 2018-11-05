package com.braincorp.petrolwatcher.callback;

import com.braincorp.petrolwatcher.model.AveragePrice;

import java.util.List;

public interface AveragePriceCallback {

    void onAveragePricesReceived(List<AveragePrice> prices, String city, String country);

}
