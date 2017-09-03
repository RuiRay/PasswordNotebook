package com.ionesmile.cipherbox.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by ionesmile on 18/08/2017.
 */
open abstract class BaseFragment : Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater?.inflate(getLayoutId(), container, false) as View
        initView()
        initData()
        return rootView
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()

    abstract fun initData()

    fun findViewById(id: Int) : View {
        return rootView.findViewById(id)
    }

}