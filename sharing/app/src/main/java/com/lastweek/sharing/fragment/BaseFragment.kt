package com.lastweek.sharing.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(getLayoutResId(), container, false)

        initView(rootView)
        return rootView
    }

    open fun initView(rootView: View) {

    }

    abstract fun getLayoutResId(): Int
}