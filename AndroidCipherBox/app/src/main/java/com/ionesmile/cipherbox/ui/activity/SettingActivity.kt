package com.ionesmile.cipherbox.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.ui.fragment.SettingFragment

/**
 * Created by ionesmile on 21/08/2017.
 */
class SettingActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonManager.setTranslucentBar(window)
        setContentView(R.layout.activity_setting)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        fragmentManager.beginTransaction()
                .replace(R.id.layout_setting_replace_root, SettingFragment())
                .commit()
    }

    companion object {
        val DEFAULT_LOCK_KEY =  byteArrayOf(1,4,7,8,9)
    }
}