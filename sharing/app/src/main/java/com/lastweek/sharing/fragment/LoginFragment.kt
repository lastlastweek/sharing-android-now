package com.lastweek.sharing.fragment

import android.view.View
import androidx.navigation.fragment.findNavController
import com.lastweek.sharing.R
import com.lastweek.sharing.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment() {
    override fun getLayoutResId(): Int {
        return R.layout.fragment_login
    }

    override fun initView(rootView: View) {
        super.initView(rootView)

        // 假设你的布局叫 fragment_login.xml，ViewBinding 自动生成的类名就是 FragmentLoginBinding
        val binding = FragmentLoginBinding.bind(rootView)

        binding.toRegisterPage.setOnClickListener {
            findNavController().navigate(R.id.to_register_fragment)
        }

        binding.toForgetPage.setOnClickListener {
            findNavController().navigate(R.id.to_forget_fragment)
        }
    }
}