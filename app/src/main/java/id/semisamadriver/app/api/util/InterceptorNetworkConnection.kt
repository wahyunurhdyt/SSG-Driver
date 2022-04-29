package id.semisamadriver.app.api.util

import android.content.Context
import id.semisamadriver.app.R
import id.semisamadriver.app.utilily.isInternetAvailable
import okhttp3.Interceptor
import okhttp3.Response
import java.net.SocketTimeoutException

class InterceptorNetworkConnection(private val context: Context): Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!context.isInternetAvailable())
            throw ConnectionException(context.getString(R.string.labelCantConnectToInternet))
        try {
            return chain.proceed(chain.request())
        } catch (e: SocketTimeoutException) {
            throw ConnectionException(context.getString(R.string.labelFailedToConnectToServer))
        } catch (e: Exception) {
            throw ConnectionException(context.getString(
                R.string.labelErrorString, e.message ?: ""
            ))
        }
    }

}