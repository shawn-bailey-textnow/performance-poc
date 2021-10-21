package com.example.tinyaes

class NativeLib {

    /**
     * A native method that is implemented by the 'tinyaes' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    //These are just hooked into test-aes.c
    external fun encrypt(): Int

    external fun encryptSample(sample: String): Int

    fun initialize() {
        System.loadLibrary("tinyaes")
    }

}
