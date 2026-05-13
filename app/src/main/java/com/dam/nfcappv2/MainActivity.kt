package com.dam.nfcappv2

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var etId: EditText? = null
    private var etNombre: EditText? = null
    private var etSaldo: EditText? = null
    private var btnLeerNFC: Button? = null
    private var btnIniciarServicio: Button? = null
    private var btnDetenerServicio: Button? = null
    private var tvEstadoServicio: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etId = findViewById(R.id.etId)
        etNombre = findViewById(R.id.etNombre)
        etSaldo = findViewById(R.id.etSaldo)
        btnLeerNFC = findViewById(R.id.btnLeerNFC)
        btnIniciarServicio = findViewById(R.id.btnIniciarServicio)
        btnDetenerServicio = findViewById(R.id.btnDetenerServicio)
        tvEstadoServicio = findViewById(R.id.tvEstadoServicio)

        btnLeerNFC!!.setOnClickListener {
            btnLeerNFC!!.isEnabled = false
            val nfcService = NFCService()
            nfcService.leerNFC { dato ->
                etId?.setText(dato.id)
                etNombre?.setText(dato.nombre)
                etSaldo?.setText(dato.saldo.toString())
                btnLeerNFC!!.isEnabled = true
            }
        }

        btnIniciarServicio!!.setOnClickListener {
            val intent = Intent(this, NFCHttpService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            tvEstadoServicio?.text = getString(R.string.servicio_activo, 8080)
            btnIniciarServicio?.isEnabled = false
            btnDetenerServicio?.isEnabled = true
        }

        btnDetenerServicio!!.setOnClickListener {
            val intent = Intent(this, NFCHttpService::class.java)
            stopService(intent)
            tvEstadoServicio?.text = getString(R.string.servicio_inactivo)
            btnIniciarServicio?.isEnabled = true
            btnDetenerServicio?.isEnabled = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, NFCHttpService::class.java)
        stopService(intent)
    }
}
