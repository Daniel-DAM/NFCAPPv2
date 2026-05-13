package com.dam.nfcappv2

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.dam.nfcappv2.http.NFCHttpServer

class NFCHttpService : Service() {

    private var nfcHttpServer: NFCHttpServer? = null
    private val TAG = "NFCHttpService"
    private val PORT = 8080
    private val CHANNEL_ID = "nfc_http_service"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Starting HTTP server on port $PORT")

        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        if (nfcHttpServer == null) {
            nfcHttpServer = NFCHttpServer(this, PORT)
            val success = nfcHttpServer?.start()
            if (success != true) {
                Log.e(TAG, "Failed to start server")
                stopSelf()
            } else {
                Log.i(TAG, "Server started successfully")
            }
        } else {
            Log.w(TAG, "Server already running")
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "Stopping HTTP server")
        nfcHttpServer?.stop()
        nfcHttpServer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "NFC HTTP Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificación del servicio HTTP NFC"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val builder = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("NFC HTTP Service")
            .setContentText("Servicio activo en puerto: $PORT")
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setOngoing(true)
        return builder.build()
    }
}
