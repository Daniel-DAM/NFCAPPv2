package com.dam.nfcappv2

import android.os.Handler
import android.os.Looper
import com.dam.nfcappv2.modelo.NFCDato

class NFCService {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun leerNFC(callback: (NFCDato) -> Unit) {
        Thread {
            Thread.sleep(1200)
            val dato = NFCDato(
                id = "1234567890",
                nombre = "Usuario NFC",
                saldo = 100.0
            )
            mainHandler.post { callback(dato) }
        }.start()
    }
}
