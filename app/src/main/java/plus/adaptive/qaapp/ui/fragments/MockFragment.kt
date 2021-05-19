package plus.adaptive.qaapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import plus.adaptive.qaapp.R
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

        mock1APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }
        mock2APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }
        mock3APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }
        mock4APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }
        mock5APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }
        mock6APView.setAPCustomActionListener { params ->
            Toast.makeText(
                context, params["name"].toString(), Toast.LENGTH_SHORT
            ).show()
        }

        scrollToStartBtn.setOnClickListener {
            mock1APView.scrollToStart()
            mock2APView.scrollToStart()
            mock3APView.scrollToStart()
            mock4APView.scrollToStart()
            mock5APView.scrollToStart()
            mock6APView.scrollToStart()
        }
    }
}