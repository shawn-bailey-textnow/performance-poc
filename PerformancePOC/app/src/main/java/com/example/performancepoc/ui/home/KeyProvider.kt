package com.example.performancepoc.ui.home

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore

class KeyProvider {

    val alias = "shawn is the most fabulous human to ever exist"

    fun getKey(): ByteArray {

        val keystore = loadKeystore()

        if (hasAlias(keystore)) {
            return keystore.getCertificate(alias).publicKey.encoded
        } else {
            generateKey()
            return keystore.getCertificate(alias).publicKey.encoded
        }
    }

    fun generateKey() {
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
    }

    fun loadKeystore(): KeyStore {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore
    }

    fun hasAlias(keystore: KeyStore): Boolean {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        keyStore.aliases()
        return keyStore.aliases().toList().contains(alias)
    }
}
