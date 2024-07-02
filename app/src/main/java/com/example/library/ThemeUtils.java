package com.example.library;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {
    private static final String PREFS_NAME = "ThemePrefs";
    private static final String THEME_MODE = "ThemeMode";

    public static void applyTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(THEME_MODE, false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static void toggleTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(THEME_MODE, false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(THEME_MODE, !isDarkTheme);
        editor.apply();
    }
}
