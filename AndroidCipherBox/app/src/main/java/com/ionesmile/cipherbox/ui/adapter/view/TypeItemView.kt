package com.ionesmile.cipherbox.ui.adapter.view

import android.content.Context
import android.widget.TextView
import com.ionesmile.cipherbox.R
import com.ionesmile.cipherbox.model.table.TypeTable
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemClick
import com.ionesmile.cipherbox.ui.adapter.base.render.ItemRender
import org.jetbrains.anko.find
import org.jetbrains.anko.onClick

/**
 * Created by ionesmile on 21/08/2017.
 */
class TypeItemView(context: Context) : BaseView(context), ItemRender<TypeTable>, ItemClick {

    lateinit var nameTv: TextView
    lateinit var descTv: TextView

    var itemClickListener: ItemClick.ItemClickListener? = null
    var position: Int = -1

    override val layoutId: Int
        get() = R.layout.item_type

    override fun initView() {
        nameTv = find(R.id.tv_name)
        descTv = find(R.id.tv_describe)
    }

    override fun initData() {
    }

    override fun initListener() {
        findViewById(R.id.view_delete).onClick { v ->
            itemClickListener?.onItemClick(v!!, position)
        }
    }

    override fun renderItem(position: Int, data: TypeTable) {
        nameTv.text = data.title
        descTv.text = data.description
    }

    override fun setItemClick(itemClickListener: ItemClick.ItemClickListener, position: Int) {
        this.itemClickListener = itemClickListener
        this.position = position
    }

}