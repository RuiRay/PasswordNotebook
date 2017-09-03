package com.ionesmile.cipherbox.ui.adapter.base.render

/**
 * Created by iOnesmile on 2016/12/21 0021.
 */
interface ItemExpandControl {

    fun cancelItemExpand(position: Int)

    fun setItemExpanded(position: Int)

    fun isItemExpanded(position: Int): Boolean
}
