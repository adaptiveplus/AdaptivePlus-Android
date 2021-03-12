package com.sprintsquads.adaptiveplusqaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sprintsquads.adaptiveplusqaapp.R


class MockFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = MockFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mock_page, container, false)
    }
}