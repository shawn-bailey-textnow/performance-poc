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

        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.append(it)
        })

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
            val alias = "nameHere"

            val keystoreResult = measureTimeMillis {

                val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_EC,
                    "AndroidKeyStore"
                )
                val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).run {
                    setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    build()
                }

                kpg.initialize(parameterSpec)

                val kp = kpg.generateKeyPair()

                //load keys
                val keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
                val entry = keyStore.getEntry(alias, null)
                publicKey = keyStore.getCertificate(alias).publicKey
            }

            val encryptArray: ByteArray
            val encryptResult = measureTimeMillis {
                encryptArray = AES.encryptAes128Cbc("test".toByteArray(), publicKey.encoded, Padding.PKCS7Padding)
            }

            val decryptArray: ByteArray
            val decryptResult = measureTimeMillis {
                decryptArray = AES.decryptAes128Cbc(encryptArray, publicKey.encoded, Padding.PKCS7Padding)
            }

            textView.append("\n Krypto Keystore Load Time: " + keystoreResult + "ms")
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
