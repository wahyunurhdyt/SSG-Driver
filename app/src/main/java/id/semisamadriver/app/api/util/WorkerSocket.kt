//package id.semisamadriver.app.api.util
//
//import android.content.Context
//import android.util.Log
//import androidx.constraintlayout.widget.Constraints
//import com.google.android.datatransport.cct.internal.NetworkConnectionInfo
//import com.google.gson.Gson
//import id.semisamadriver.app.BuildConfig
//import id.semisamadriver.app.api.data.RequestUpdateLocation
//import id.semisamadriver.app.api.data.UserLocation
//import id.semisamadriver.app.api.repository.RepositoryAuth
//import id.semisamadriver.app.api.repository.RepositoryLocation
//import id.semisamadriver.app.utilily.Coroutines
//import id.semisamadriver.app.utilily.tempAuth
//import io.socket.client.IO
//import io.socket.client.Socket
//import org.kodein.di.KodeinAware
//import org.kodein.di.android.closestKodein
//import org.kodein.di.generic.instance
//import java.util.concurrent.TimeUnit
//import kotlin.coroutines.resume
//import kotlin.coroutines.suspendCoroutine
//
//class WorkerSocket(
//    private val context: Context,
//    params: WorkerParameters
//): CoroutineWorker(context, params), KodeinAware {
//
//    override val kodein by closestKodein { context }
//
//    private val repositoryAuth: RepositoryAuth by instance()
//    private val repositoryLocation: RepositoryLocation by instance()
//
//    companion object {
//
//        private const val TAG = "WorkerSocket"
//        private const val JOB_INTERVAL = 15L
//        private const val JOB_ID_SOCKET = "JOB_ID_SOCKET"
//        private const val SOCKET_EVENT = "user.tracks"
//
//        fun startWorker(context: Context) {
//            val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkConnectionInfo.NetworkType.CONNECTED)
//                .build()
//
//            val work = PeriodicWorkRequest
//                .Builder(WorkerSocket::class.java, JOB_INTERVAL, TimeUnit.MINUTES)
//                .setConstraints(constraints)
//                .build()
//
//            val workManager = WorkManager.getInstance(context)
//            workManager.enqueueUniquePeriodicWork(
//                JOB_ID_SOCKET,
//                ExistingPeriodicWorkPolicy.REPLACE,
//                work
//            )
//        }
//    }
//
//    override suspend fun doWork(): Result {
//        return if (repositoryAuth.isUserLoggedIn()) {
//            Log.d(TAG, "User logged in, send data")
//            if (connectToSocket()) Result.success() else Result.retry()
//        } else {
//            Log.d(TAG, "User logged out, cancel work")
//            val workManager = WorkManager.getInstance(context)
//            workManager.cancelUniqueWork(JOB_ID_SOCKET)
//            Result.success()
//        }
//    }
//
//    private suspend fun connectToSocket(): Boolean {
//        return suspendCoroutine { cont ->
//            val options = IO.Options()
//            options.reconnection = true
//            options.forceNew = true
//
//            val headers: Map<String, List<String>> = mutableMapOf(
//                Pair("X-Api-Key", listOf(BuildConfig.API_KEY)),
//                Pair("Authorization", listOf("Bearer ${tempAuth.tokens.access.token}")),
//            )
//            options.extraHeaders = headers
//
//            val socket = IO.socket(BuildConfig.BASE_URL_SOCKET)
//            socket.connect()
//                .on(Socket.EVENT_CONNECT) {
//                    Log.d(TAG, "Socket connected")
//                    Coroutines.io {
//                        if (!sendToSocket(socket)) {
//                            cont.resume(false)
//                            socket.disconnect()
//                        }
//                    }
//                }
//                .on(Socket.EVENT_DISCONNECT) {
//                    try {
//                        Log.d(TAG, "Socket disconnected")
//                        cont.resume(true)
//                    } catch (e: IllegalStateException) {
//                        Log.e(TAG, "${e.message}")
//                    }
//                }
//                .on(Socket.EVENT_CONNECT_ERROR) {
//                    try {
//                        Log.d(TAG, "Socket connection error")
//                        socket.disconnect()
//                        cont.resume(false)
//                    } catch (e: IllegalStateException) {
//                        Log.e(TAG, "${e.message}")
//                    }
//                }
//        }
//    }
//
//    private suspend fun sendToSocket(socket: Socket): Boolean {
//        return suspendCoroutine { cont ->
//            Coroutines.io {
//                val isTokenOk = repositoryAuth.checkAccessToken()
//                if (isTokenOk) {
//                    val location = repositoryLocation.getLatestLocationNonLive()?.withOutDbId()
//                    if (location != null) {
//                        val coordinates: ArrayList<Double> = ArrayList()
//                        coordinates.add(location.lng)
//                        coordinates.add(location.lat)
//                        val req = RequestUpdateLocation(UserLocation("Point", coordinates))
//                        socket.emit(SOCKET_EVENT, "Data : ${Gson().toJson(req)}")
//                        Log.d(TAG, "Socket send data \"Data : ${Gson().toJson(req)}")
//                        cont.resume(true)
//                    } else {
//                        Log.d(TAG, "Socket send data cancelled due to location empty")
//                        cont.resume(false)
//                    }
//                } else {
//                    Log.d(TAG, "Socket send data cancelled due to token not ok")
//                    cont.resume(false)
//                }
//                socket.disconnect()
//            }
//        }
//    }
//
//}