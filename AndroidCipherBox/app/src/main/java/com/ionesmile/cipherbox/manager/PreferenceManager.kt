package com.ionesmile.cipherbox.manager

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log

/**
 * Created by ionesmile on 03/09/2017.
 */

object PreferenceManager {

    val KEY_SWITCH_LOCK_TRACK = "switch_lock_track"
    val KEY_EDIT_PASSWORD_PREF = "edit_password_pref"
    val KEY_LIST_SHOW_OPTIONS = "list_show_options"

    fun getLockPatternVisible(context: Context): Boolean {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(KEY_SWITCH_LOCK_TRACK, true)
    }

    fun getLockPatternPassword(context: Context): String? {
        return android.preference.PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_EDIT_PASSWORD_PREF, null)
    }

    @JvmStatic fun getListShowOptions(context: Context): Int {
        var values = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY_LIST_SHOW_OPTIONS, null)
        Log.v("PreferencesTAG", "getListShowOptions() values = " + values)
        if (values != null){
            return values.toInt()
        }
        return 1
    }
}
