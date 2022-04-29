package id.semisamadriver.app.api.util

import android.content.Context
import android.os.Looper
import android.util.Log
import id.semisamadriver.app.R
import id.semisamadriver.app.base.BaseResponse.Companion.CODE_AUTH_TOKEN_INVALID
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.tempAuth
import id.semisamadriver.app.utilily.toast
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


abstract class SafeApiRequest(val context: Context) {


    suspend fun <T: Any> apiRequest(call: suspend () -> Response<T>): T? {
        val response = call.invoke()
        return checkForError(response)
    }

    fun <T: Any> apiRequestBlocking(call: suspend () -> Response<T>): T? {
        val response = runBlocking { call.invoke() }
        return checkForError(response)
    }

    private fun <T> checkForError(response: Response<T>): T? {
        if (response.isSuccessful) {
            return response.body()
        } else {
            @Suppress("BlockingMethodInNonBlockingContext")
            val error = response.errorBody()?.string()
            var code = response.code()
            var message = ""

            if (code >= 500) {
                message = context.getString(R.string.labelServerDown)
            } else {
                error?.let {
                    try {
                        code = JSONObject(it)
                            .getInt("code")
                        message = JSONObject(it)
                            .getString("message")
                    } catch (e: JSONException) {
                        try {
                            message = it
                        } catch (e: Exception) {
                            code = response.code()
                            message = "Error: ${response.message()}"
                        }
                    }
                }
            }

            if (code == CODE_AUTH_TOKEN_INVALID) {
                Coroutines.io {
                    val token = tempAuth?.tokens?.access?.token
                    if (token != null) {
                        try {
                            Looper.prepare()
                            context.toast(context.getString(R.string.labelTokenInvalid))
                            //Logout
                        } catch (e: ApiException) {
                            Log.e("Logout SAR","${e.message}")
                        } catch (e: ConnectionException) {
                            Log.e("logout SAR","${e.message}")
                        }
                    }
                }
            }

            throw ApiException(code, message)
        }
    }

}