package com.example.performancepoc.ui.home

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey

class KeyProvider {

    val alias = "shawn_is_the_most_fabulous_human_to_ever_exist"

    fun getKey(): PublicKey {

        val keystore = loadKeystore()

        if (hasAlias(keystore)) {
            return keystore.getCertificate(alias).publicKey
        } else {
            generateKey()
            return keystore.getCertificate(alias).publicKey
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

    fun hasAlias(keyStore: KeyStore): Boolean {
        keyStore.load(null)
        keyStore.aliases()
        return keyStore.aliases().toList().contains(alias)
    }

    fun deleteKey() {
        val keyStore = loadKeystore()
        keyStore.load(null)
        keyStore.deleteEntry(alias)
    }
}
