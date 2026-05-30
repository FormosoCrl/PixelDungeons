package com.example.pixeldungeons.ui;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

public class ThemeHelper {

    private static final String PREF = "theme_prefs";
    private static final String KEY  = "dark_mode";

    /** Llámalo en el onCreate() de la Activity, antes de setContentView. */
    public static void apply(Context ctx) {
        boolean dark = prefs(ctx).getBoolean(KEY, false);
        AppCompatDelegate.setDefaultNightMode(
                dark ? AppCompatDelegate.MODE_NIGHT_YES
                     : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static boolean isDark(Context ctx) {
        return prefs(ctx).getBoolean(KEY, false);
    }

    /**
     * Vincula el SwitchCompat con el sistema de temas.
     * Llámalo en onCreate() después de setContentView.
     */
    public static void setup(AppCompatActivity activity, SwitchCompat sw) {
        sw.setOnCheckedChangeListener(null);
        sw.setChecked(isDark(activity));
        sw.setOnCheckedChangeListener((btn, checked) -> {
            if (checked == isDark(activity)) return;
            prefs(activity).edit().putBoolean(KEY, checked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
            activity.recreate();
        });
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
