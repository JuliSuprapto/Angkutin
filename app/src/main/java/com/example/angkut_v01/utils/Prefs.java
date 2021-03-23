package com.example.angkut_v01.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import net.grandcentrix.tray.TrayPreferences;

import org.json.JSONObject;

public class Prefs extends TrayPreferences {
    public static final String PREF_IS_LOGEDIN = "is.login";
    public static final String PREF_STORE_PROFILE = "pref.store.profile";
    public static final String PREF_STORE_STATUS = "pref.store.status";
    public Prefs(@NonNull Context context) {
        super(context, "myAppPreferencesModule", 1);
    }
}
