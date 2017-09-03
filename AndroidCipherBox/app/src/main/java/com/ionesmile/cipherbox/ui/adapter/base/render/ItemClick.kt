package com.ionesmile.cipherbox.ui.adapter.base.render

import android.view.View

/**
 * Created by Onesmile on 2016/12/18.
 */
interface ItemClick {

    fun setItemClick(itemClickListener: ItemClickListener, position: Int)

    interface ItemClickListener {
        /**
         * 在 ItemView 内面的某个 View 点击的回调
         * @param view
         * *
         * @param position
         */
        fun onItemClick(view: View, position: Int)
    }
}
