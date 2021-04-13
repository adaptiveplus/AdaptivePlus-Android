package com.sprintsquads.adaptiveplusqaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sprintsquads.adaptiveplus.sdk.data.APCustomActionListener
import com.sprintsquads.adaptiveplusqaapp.R
import kotlinx.android.synthetic.main.fragment_mock_page.*


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mock1APView.setAPCustomActionListener(object: APCustomActionListener {
            override fun onRun(params: HashMap<String, Any>) {
                Toast.makeText(
                    context, params["name"].toString(), Toast.LENGTH_SHORT
                ).show()
            }
        })
        mock2APView.setAPCustomActionListener(object: APCustomActionListener {
            override fun onRun(params: HashMap<String, Any>) {
                Toast.makeText(
                    context, params["name"].toString(), Toast.LENGTH_SHORT
                ).show()
            }
        })
        mock3APView.setAPCustomActionListener(object: APCustomActionListener {
            override fun onRun(params: HashMap<String, Any>) {
                Toast.makeText(
                    context, params["name"].toString(), Toast.LENGTH_SHORT
                ).show()
            }
        })
        mock4APView.setAPCustomActionListener(object: APCustomActionListener {
            override fun onRun(params: HashMap<String, Any>) {
                Toast.makeText(
                    context, params["name"].toString(), Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}