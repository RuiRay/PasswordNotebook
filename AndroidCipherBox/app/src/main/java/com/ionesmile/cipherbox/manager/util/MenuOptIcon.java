package com.ionesmile.cipherbox.manager.util;

import android.util.Log;
import android.view.Menu;

import java.lang.reflect.Method;

/**
 * Created by ionesmile on 01/09/2017.
 */

public class MenuOptIcon {

    public static void setMenuIconShow(Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e("MenuOptIcon", "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
    }
}
