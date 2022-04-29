package id.semisamadriver.app.api.service

import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.util.Client
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServiceAuth {

    @POST("$version/login")
    suspend fun postLogin(
        @Body request: RequestLogin
    ): Response<ResponseAuth>

    @HTTP(method = "DELETE", path = "$version/logout", hasBody = true)
    suspend fun logout(
        @Body request: RequestRefreshToken
    ): Response<Unit>

    @POST("$version/refresh-tokens")
    suspend fun refreshToken(
        @Body request: RequestRefreshToken
    ): Response<ResponseToken>

    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServiceAuth {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_AUTH)
                .client(client.provideClient())
                .build()
                .create(ServiceAuth::class.java)
        }
    }
}