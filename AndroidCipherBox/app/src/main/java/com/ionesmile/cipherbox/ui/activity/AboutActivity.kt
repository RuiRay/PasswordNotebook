package com.ionesmile.cipherbox.ui.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import org.jetbrains.anko.find

/**
 * Created by ionesmile on 02/09/2017.
 */
class AboutActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CommonManager.setTranslucentBar(window)
        setContentView(R.layout.activity_about)

        initView()
        initToolbar()
    }

    private fun initView() {
        var tvVersion = find<TextView>(R.id.tv_version)
        tvVersion.text = "v${getVersionName()}"
    }

    fun initToolbar() {
        var toolbar: Toolbar = find(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    fun getVersionName() : String{
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            val versionName = packageInfo.versionName
            return versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.00"
    }
}