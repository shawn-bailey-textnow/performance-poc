package com.example.performancepoc.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tinyaes.NativeLib
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.system.measureTimeMillis

class HomeViewModel : ViewModel() {

    private val nativeLib = NativeLib()

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }

    val text: LiveData<String> = _text

    //Tests the implementation on a background thread
    fun testTinyAes() = viewModelScope.launch(Dispatchers.Default) {
        val loadResult = measureTimeMillis {
            nativeLib.initialize()
        }
        val testResult = measureTimeMillis {
            nativeLib.encrypt()
        }

        _text.postValue("\n TinyAES Load: " + loadResult + "ms - " + "Test: " + testResult + "ms")
    }

}
