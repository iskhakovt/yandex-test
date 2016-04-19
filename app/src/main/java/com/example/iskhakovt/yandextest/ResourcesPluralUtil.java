/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.res.Resources;


public class ResourcesPluralUtil {
    public static String getQuantityStringZero(Resources resources, int resId, int zeroResId, int quantity) {
        if (quantity == 0) {
            return resources.getString(zeroResId);
        } else {
            return resources.getQuantityString(resId, quantity, quantity);
        }
    }

    public static String getQuantityStringZero(Resources resources, int resId, int zeroResId, int quantity, Object... formatArgs) {
        if (quantity == 0) {
            return resources.getString(zeroResId);
        } else {
            return resources.getQuantityString(resId, quantity, formatArgs);
        }
    }
}
