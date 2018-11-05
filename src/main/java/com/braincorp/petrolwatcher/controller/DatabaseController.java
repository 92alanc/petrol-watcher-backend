package com.braincorp.petrolwatcher.controller;

import com.braincorp.petrolwatcher.callback.AveragePriceCallback;
import com.braincorp.petrolwatcher.model.Prediction;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseController {

    private static final String KEY_AVERAGE_PRICES = "average_prices";
    private static final String KEY_PREDICTIONS = "predictions";

    private Logger logger = LoggerFactory.getLogger(DatabaseController.class);
    private DatabaseReference reference;

    public DatabaseController() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    public void fetchAveragePrices(AveragePriceCallback callback) {
        // TODO
    }

    /**
     * Updates the prediction in the database
     * @param prediction the new prediction
     */
    public void updatePrediction(Prediction prediction) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(KEY_PREDICTIONS).exists())
                    update(prediction);
                else
                    insert(prediction);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Error updating database", databaseError.toException());
            }
        });
    }

    private void insert(Prediction prediction) {
        reference.child(KEY_PREDICTIONS)
                .setValue(prediction.toMap(), completionListener);
    }

    private void update(Prediction prediction) {
        reference.child(KEY_PREDICTIONS)
                .updateChildren(prediction.toMap(), completionListener);
    }

    private DatabaseReference.CompletionListener completionListener = (databaseError, databaseReference) ->
            logger.info("Predictions updated");

}
