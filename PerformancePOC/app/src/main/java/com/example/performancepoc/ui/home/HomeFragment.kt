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
import com.example.tinyaes.NativeLib
import kotlin.system.measureTimeMillis

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val nativeLib = NativeLib()

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
        val tinyaesButton: Button = binding.buttonTinyaes
        val kryptoButton: Button = binding.buttonKrypto
        val tankerButton: Button = binding.buttonTanker
        val tinkButton: Button = binding.buttonTink


        tinyaesButton.setOnClickListener {
            val loadResult = measureTimeMillis {
                nativeLib.initialize()
            }
            val testResult = measureTimeMillis {
                nativeLib.encrypt()
            }

            textView.append("\n TinyAES Load Time: " + loadResult + "ms")
            textView.append("\n TinyAES Test Time: " + testResult + "ms")
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
