/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.content.res.Resources;


/**
 * Plural resource loader useful for english locale, which doesn't use zero quantity by default
 */
public class ResourcesPluralUtil {
    /**
     * Get formatted quantity string
     * @param resources Manager of resources
     * @param resId Quantity string resource id
     * @param zeroResId Zero string resource id
     * @param quantity Quantity to format
     * @return Formatted resource string
     */
    public static String getQuantityStringZero(Resources resources, int resId, int zeroResId, int quantity) {
        if (quantity == 0) {
            return resources.getString(zeroResId);
        } else {
            return resources.getQuantityString(resId, quantity, quantity);
        }
    }

    /**
     * Get formatted quantity string
     * @param resources Manager of resources
     * @param resId Quantity string resource id
     * @param zeroResId Zero string resource id
     * @param quantity Quantity to format
     * @param formatArgs Same formatting args as for resources.getQuantityString
     * @return Formatted resource string
     */
    public static String getQuantityStringZero(Resources resources, int resId, int zeroResId, int quantity, Object... formatArgs) {
        if (quantity == 0) {
            return resources.getString(zeroResId);
        } else {
            return resources.getQuantityString(resId, quantity, formatArgs);
        }
    }
}
