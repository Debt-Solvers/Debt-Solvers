package com.example.loginapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class GroupDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_detail, container, false)
        val tvGroupDetail: TextView = view.findViewById(R.id.tvGroupDetail)

        val groupName = arguments?.getString("groupName")
        val groupDescription = arguments?.getString("groupDescription")

        tvGroupDetail.text = "$groupName\n\n$groupDescription"
        return view
        //return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }
}