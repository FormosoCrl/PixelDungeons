package com.example.pixeldungeons.ui;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Guarda la sesión iniciada (id y nombre de usuario) en SharedPreferences
 * para que se mantenga aunque se cierre la app.
 */
public class SessionManager {

    private static final String PREF = "session_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";

    /** Guarda la sesión tras un login o registro correcto. */
    public static void save(Context ctx, int userId, String username) {
        prefs(ctx).edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .apply();
    }

    /** True si hay una sesión guardada. */
    public static boolean isLoggedIn(Context ctx) {
        return prefs(ctx).getInt(KEY_USER_ID, -1) != -1;
    }

    public static int getUserId(Context ctx) {
        return prefs(ctx).getInt(KEY_USER_ID, -1);
    }

    public static String getUsername(Context ctx) {
        return prefs(ctx).getString(KEY_USERNAME, null);
    }

    /** Borra la sesión (cerrar sesión). */
    public static void clear(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
