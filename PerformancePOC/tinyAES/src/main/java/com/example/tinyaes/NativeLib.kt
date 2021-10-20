package com.example.tinyaes

class NativeLib {

    /**
     * A native method that is implemented by the 'tinyaes' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'tinyaes' library on application startup.
        init {
            System.loadLibrary("tinyaes")
        }
    }
}
