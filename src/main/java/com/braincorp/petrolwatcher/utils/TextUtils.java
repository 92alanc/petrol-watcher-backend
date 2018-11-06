package com.braincorp.petrolwatcher.utils;

import java.text.Normalizer;

public class TextUtils {

    private TextUtils() { }

    public static String normalise(String text) {
        text = Normalizer.normalize(text.replace(" ", "").toLowerCase(), Normalizer.Form.NFD);
        return text.replaceAll("\\p{M}", "");
    }

}
