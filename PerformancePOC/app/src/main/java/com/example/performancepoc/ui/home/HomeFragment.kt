package com.example.performancepoc.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.room.Database
import androidx.room.Room
import com.example.performancepoc.databinding.FragmentHomeBinding
import com.example.tinyaes.NativeLib
import com.google.crypto.tink.Aead
import com.google.crypto.tink.Config
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.soywiz.krypto.AES
import com.soywiz.krypto.Padding
import net.sqlcipher.database.SupportFactory
import java.security.PublicKey
import kotlin.system.measureTimeMillis



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
        val tinkButton: Button = binding.buttonTink
        val sqlCipherButton: Button = binding.buttonSqlcipher
        val deleteButton: Button = binding.buttonDelete

        homeViewModel.text.observe(viewLifecycleOwner, {
            textView.append(it)
        })

        tinkButton.setOnClickListener {

            val aead: Aead

            val loadResult = measureTimeMillis {
                AeadConfig.register()

                aead = AndroidKeysetManager.Builder()
                    .withSharedPref(
                        context, "keysetname", "preferencename"
                    )
                    .withKeyTemplate(AeadKeyTemplates.AES128_GCM)
                    .withMasterKeyUri("android-keystore://hello_world_master_key")
                    .build()
                    .keysetHandle.getPrimitive(Aead::class.java)
            }

            val ciphertext: ByteArray
            val encryptResult = measureTimeMillis {
                ciphertext = aead.encrypt("pancakes".toByteArray(), null)
            }

            val decryptResult = measureTimeMillis {
                val decrypted = aead.decrypt(ciphertext, null)
            }

            textView.append("\n Tink Initialize: " + loadResult + "ms")
            textView.append("\n Tink Encrypt: " + encryptResult + "ms")
            textView.append("\n Tink decrypt: " + decryptResult + "ms")
        }

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

        sqlCipherButton.setOnClickListener {

            val databaseTest: DatabaseTest

            val databasecreation = measureTimeMillis {
                val builder = Room.databaseBuilder(
                    requireContext().applicationContext,
                    DatabaseTest::class.java, "encrypted.db"
                )
                val factory = SupportFactory("PassPhrase".toByteArray())
                builder.openHelperFactory(factory)
                builder.allowMainThreadQueries()
                databaseTest = builder.build()
            }

            val setTime = measureTimeMillis {
                databaseTest.vesselDao().setBlocking(VesselEntity("Test", "Test"))
            }

            val getTime = measureTimeMillis {
                databaseTest.vesselDao().getBlocking("Test")
            }

            textView.append("\n Database Create: " + databasecreation + "ms")
            textView.append("\n Database set: " + setTime + "ms")
            textView.append("\n Database get: " + getTime + "ms")


        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
