package com.example.pixeldungeons.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    /**
     * Ajusta el margen superior del toggle para que quede justo debajo de la
     * barra de estado en dispositivos edge-to-edge (Samsung, Android 15+).
     * Llámalo en onCreate() después de setContentView, pasando la vista raíz
     * del include (R.id.theme_toggle).
     */
    public static void adjustMarginForStatusBar(View toggleView) {
        ViewCompat.setOnApplyWindowInsetsListener(toggleView, (v, insets) -> {
            int statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            ViewGroup.MarginLayoutParams params =
                    (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = statusBarHeight + dpToPx(v.getContext(), 4);
            v.setLayoutParams(params);
            return insets;
        });
    }

    private static int dpToPx(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
