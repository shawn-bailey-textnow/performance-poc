package com.example.tanker

import android.content.Context
import io.tanker.api.EncryptionOptions
import io.tanker.api.Tanker
import io.tanker.api.TankerFuture
import io.tanker.api.TankerOptions

object TankerEncrypt {

    fun initialize(context: Context): Tanker {
        val writablePath: String = context.filesDir.absolutePath
        val options = TankerOptions()
        options.setAppId("your-app-id")
            .setWritablePath(writablePath)
        return Tanker(options)
    }
}
