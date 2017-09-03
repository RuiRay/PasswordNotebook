package com.ionesmile.cipherbox.ui.adapter.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView

import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.manager.CommonManager
import com.ionesmile.cipherbox.model.table.CipherTable
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemRender

/**
 * Created by ionesmile on 08/06/2017.
 */

class CipherItemView : BaseView, ItemRender<CipherTable> {

    private lateinit var iconIv: ImageView
    private lateinit var nameTv: TextView
    private lateinit var descTv: TextView

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override val layoutId: Int
        get() = R.layout.item_cipher

    override fun initView() {
        iconIv = findViewById(R.id.iv_icon) as ImageView
        nameTv = findViewById(R.id.tv_name) as TextView
        descTv = findViewById(R.id.tv_describe) as TextView
    }

    override fun initData() {

    }

    override fun initListener() {

    }

    override fun renderItem(position: Int, data: CipherTable) {
        CommonManager.setLogoImage(iconIv, data.icon)
        nameTv.text = data.name
        descTv.text = data.url
    }
}
