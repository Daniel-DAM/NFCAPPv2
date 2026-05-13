package com.dam.nfcappv2.http


import android.content.Context
import android.util.Log
import com.dam.nfcappv2.modelo.NFCDato
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class NFCHttpServer(private val context: Context, private val port: Int = 8080) {

    private var server: EmbeddedServer<*, *>? = null
    private val isReading = AtomicBoolean(false)
    private val lastResult = AtomicReference<NFCDato?>(null)

    companion object {
        private const val TAG = "NFCHttpServer"
    }

    fun start(): Boolean {
        if (server != null) {
            Log.w(TAG, "Server already running")
            return false
        }

        return try {

            server = embeddedServer(CIO, port = port, host = "0.0.0.0") {

                install(ContentNegotiation) {
                    json()
                }

                routing {


                    get("/api/status") {

                        call.respondText(
                            """{"status":"running","message":"NFC HTTP Server is running on port $port","reading":${isReading.get()}}""",
                            ContentType.Application.Json
                        )
                    }

                    get("/api/nfc/leer") {

                        if (isReading.get()) {

                            call.respondText(
                                """{"success":false,"data":null,"error":"Lectura NFC en progreso"}""",
                                ContentType.Application.Json,
                                HttpStatusCode.Conflict
                            )

                            return@get
                        }

                        try {

                            isReading.set(true)
                            lastResult.set(null)

                            Thread.sleep(1200)

                            val dato = NFCDato(
                                "1234567890",
                                "Usuario NFC",
                                100.0
                            )

                            lastResult.set(dato)
                            isReading.set(false)

                            val json = """
                                        {
                                            "success": true,
                                            "data": {
                                                "id": "${dato.id}",
                                                "nombre": "${dato.nombre}",
                                                "saldo": ${dato.saldo}
                                            },
                                            "error": null
                                        }
                                        """.trimIndent()

                            call.respondText(
                                json,
                                ContentType.Application.Json,
                                HttpStatusCode.OK
                            )

                        } catch (e: Exception) {

                            isReading.set(false)

                            call.respondText(
                                """{"success":false,"data":null,"error":"${e.message}"}""",
                                ContentType.Application.Json,
                                HttpStatusCode.InternalServerError
                            )
                        }

                    }

                    get("/") {

                        call.respondText(
                            """
                                    <html>
                                        <head>
                                            <title>NFC HTTP Server</title>
                                        </head>
                                        
                                        <body>
                                        
                                            <h1>NFC HTTP Server</h1>
                                            
                                            <p>Endpoints:</p>
                                            
                                            <ul>
                                                
                                                <li>GET /api/status - Server status</li>
                                                <li>GET /api/nfc/leer - Read NFC tag</li>
                                            </ul>
                                            
                                            <p>
                                                Usage:
                                                Include header:
                                                Authorization: Bearer YOUR_TOKEN
                                            </p>
                                            
                                        </body>
                                    </html>
                                    """.trimIndent(),
                            ContentType.Text.Html
                        )
                    }
                }
            }

            server?.start(wait = false)

            Log.i(TAG, "HTTP Server started on port $port")

            true

        } catch (e: Exception) {

            Log.e(TAG, "Failed to start server: ${e.message}")

            server = null

            false
        }
    }

    fun stop() {

        try {

            server?.stop(1000, 2000)

            Log.i(TAG, "HTTP Server stopped")

        } catch (e: Exception) {

            Log.e(TAG, "Error stopping server", e)

        } finally {

            server = null
        }
    }

    fun isRunning(): Boolean = server != null
}
