package com.ionesmile.cipherbox.ui.adapter.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

/**
 * Created by iOnesmile on 2017/4/18
 */
abstract class BaseView : FrameLayout, View.OnClickListener {

    constructor(context: Context) : super(context) {
        initBase()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initBase()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initBase()
    }

    protected fun initBase() {
        LayoutInflater.from(context).inflate(layoutId, this)
        initView()
        initData()
        initListener()
    }

    protected abstract val layoutId: Int

    protected abstract fun initView()

    protected abstract fun initData()

    protected abstract fun initListener()

    override fun onClick(view: View) {

    }
}
