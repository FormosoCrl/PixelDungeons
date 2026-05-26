package com.example.pixeldungeons;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Application singleton. Aplica el tema guardado una única vez al arrancar,
 * antes de que se cree cualquier Activity. Así el primer render ya usa el
 * modo correcto y no hay parpadeo de posición en el toggle.
 */
public class PixelDungeonsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        boolean dark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES
                     : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
