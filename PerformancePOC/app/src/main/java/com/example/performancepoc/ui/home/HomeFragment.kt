package com.example.performancepoc.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.performancepoc.R
import com.example.performancepoc.databinding.FragmentHomeBinding
import kotlin.system.measureTimeMillis

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    private external fun encrypt(): Int

    private external fun encryptSample(sample: String): Int

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.results
        val startButton: Button = binding.buttonStart


        startButton.setOnClickListener(View.OnClickListener {
            val loadResult = measureTimeMillis {
                System.loadLibrary("tinyaes")
            }
            val testResult = measureTimeMillis {
                encrypt()
            }

            textView.append("\n result: " + loadResult + "ms")
            textView.append("\n result: " + testResult + "ms")
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
