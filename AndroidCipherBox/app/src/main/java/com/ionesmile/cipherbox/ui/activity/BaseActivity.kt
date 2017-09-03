package com.ionesmile.cipherbox.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.ionesmile.cipherbox.R
import org.jetbrains.anko.find

/**
 * Created by ionesmile on 08/06/2017.
 */

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBase()
        setContentView(layoutId)
        initToolbar()
        initUI()
        initData()
        initListener()
    }

    protected open fun initToolbar() {
        var toolbar: Toolbar = find(R.id.toolbar)
        toolbar?.let {
            setSupportActionBar(toolbar)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    protected open fun initBase() {

    }

    protected abstract val layoutId: Int

    protected abstract fun initUI()

    protected abstract fun initData()

    protected abstract fun initListener()

    override fun onClick(v: View) {

    }
}
