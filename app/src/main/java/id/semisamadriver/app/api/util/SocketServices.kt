package id.semisamadriver.app.api.util

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.RequestUpdateLocation
import id.semisamadriver.app.api.data.UserLocation
import id.semisamadriver.app.ui.navigation.ActivityNavigation
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.tempAuth
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class SocketServices: Service() {


    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var socket: Socket


//    private lateinit var resultPendingIntent: PendingIntent
    companion object {
        const val TAG_NOTIFICATION = "SEMISAMA_NOTIFICATION"
        const val CHANNEL_ID = "id.semisamadriver.app"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        Log.d(TAG_NOTIFICATION, "created")
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.disconnect()
        socket.off(Socket.EVENT_CONNECT)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG_NOTIFICATION, "started")

        createNotificationChannel()
        if (Build.VERSION.SDK_INT >= 26) {
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Semisama Service")
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.splash_screen)
                .build()

            startForeground(1, notification)
        }

//        val intentNotification = Intent(this, ActivityNavigation::class.java)
//        resultPendingIntent = TaskStackBuilder.create(this).run {
//            addNextIntentWithParentStack(intentNotification)
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
        getLocation()
        return START_STICKY
    }
    @SuppressLint("MissingPermission")
    private fun getLocation() {
        buildLocationRequest()
        buildLocationCallBack()
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.myLooper()!!
        )

    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        val lat = location.latitude
                        val long = location.longitude
                        Coroutines.main {
                            sendLocation(lat, long)
                        }
                    } else {
                        println("Can't Get Your Location")
                    }
                }
            }
        }
    }

    @SuppressLint("HardwareIds")
    fun sendLocation(lat: Double, lng: Double) {
        val expires = tempAuth?.tokens?.access?.expires
        val currentTime = Date().time
        if (currentTime < (expires ?: 0) ){
            try {
                val token = tempAuth?.tokens?.access?.token
                val options = IO.Options()
                options.reconnection = true
                options.forceNew = true

                Log.d("TOKEN", token!!)

                if (token.isEmpty()) {
                    val headers: Map<String, List<String>> = mutableMapOf(
                        Pair("X-Api-Key", listOf(BuildConfig.API_KEY)),
                    )
                    options.extraHeaders = headers
                } else {
                    val headers: Map<String, List<String>> = mutableMapOf(
                        Pair("X-Api-Key", listOf(BuildConfig.API_KEY)),
                        Pair("Authorization", listOf("Bearer $token")),
                    )
                    options.extraHeaders = headers
                }

                socket = IO.socket(BuildConfig.BASE_URL_SOCKET, options)
                socket.connect()
                    .on(Socket.EVENT_CONNECT) {
                        Log.d("SOCKET_STATUS", "Connect")
                        if (token.isNotEmpty()) {
                            val coordinates: ArrayList<Double> = ArrayList()
                            coordinates.add(lng)
                            coordinates.add(lat)
                            val req = RequestUpdateLocation(UserLocation("Point", coordinates))
                            socket.emit("updateDriverLocation", Gson().toJson(req))
                            println("Data : ${Gson().toJson(req)}")
                        }
                    }
                    .on(Socket.EVENT_DISCONNECT) {
                        Log.d("SOCKET_STATUS", "Not Connect")
                    }
                    .on(Socket.EVENT_CONNECT_ERROR) {
                        Log.d("SOCKET_STATUS", "Error Connect = ${Gson().toJson(it)}")
                    }
            } catch (e: URISyntaxException) {
                Log.d("SOCKET_BOY", e.toString())
                throw RuntimeException(e)
            }
        }
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.smallestDisplacement = 5f
    }

    private fun isOreoOrUp(): Boolean{
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun createNotificationChannel() {
        if (isOreoOrUp()) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Semi Sama Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}