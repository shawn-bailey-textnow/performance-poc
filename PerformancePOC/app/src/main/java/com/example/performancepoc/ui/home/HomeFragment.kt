package com.example.performancepoc.ui.home

import android.os.Bundle
import android.security.keystore.KeyProperties
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
import javax.crypto.KeyGenerator
import kotlin.system.measureTimeMillis
import android.security.keystore.KeyGenParameterSpec
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val nativeLib = NativeLib()
    private val keyProvider = KeyProvider()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //These are the buttons we can use to test different encryption libraries
        val textView: TextView = binding.results
        val tinyaesButton: Button = binding.buttonTinyaes
        val kryptoButton: Button = binding.buttonKrypto
        val tankerButton: Button = binding.buttonTanker
        val tinkButton: Button = binding.buttonTink
        val deleteButton: Button = binding.buttonDelete

        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.append(it)
        })

        deleteButton.setOnClickListener {
            keyProvider.deleteKey()
            textView.append("\n Key Deleted")
        }

        tinyaesButton.setOnClickListener {
            //homeViewModel.testTinyAes() // test running in a coroutine
            
            val loadResult = measureTimeMillis {
                nativeLib.initialize()
            }
            val testResult = measureTimeMillis {
                nativeLib.encrypt()
            }

            textView.append("\n TinyAES Load Time: " + loadResult + "ms")
            textView.append("\n TinyAES Test Time: " + testResult + "ms")
        }

        //From comments in AES -> Based on CryptoJS
        kryptoButton.setOnClickListener {
            val publicKey: PublicKey

            val keystoreResult = measureTimeMillis {
                publicKey = keyProvider.getKey()
            }

            val encryptArray: ByteArray
            val encryptResult = measureTimeMillis {
                encryptArray = AES.encryptAes128Cbc("testString".toByteArray(), publicKey.encoded, Padding.PKCS7Padding)
            }

            val decryptArray: ByteArray
            val decryptResult = measureTimeMillis {
                decryptArray = AES.decryptAes128Cbc(encryptArray, publicKey.encoded, Padding.PKCS7Padding)
            }

            textView.append("\n Android Keystore Load Time: " + keystoreResult + "ms")
            textView.append("\n Krypto Encrypt Time: " + encryptResult + "ms")
            textView.append("\n Krypto Decrypt Time: " + decryptResult + "ms")
            textView.append("\n Krypto Encrypt Result: " + encryptArray.decodeToString())
            textView.append("\n Krypto Decrypt Result: " + decryptArray.decodeToString())
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
