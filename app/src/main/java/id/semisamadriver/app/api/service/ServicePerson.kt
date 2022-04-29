package id.semisamadriver.app.api.service

import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.util.Client
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServicePerson {

    @GET("$version/me")
    suspend fun getUser(): Response<ResponseUser>

    @POST("$version/me/password")
    suspend fun setPassword(
        @Body request: RequestPassword
    ): Response<Unit>

    @PATCH("$version/me/password")
    suspend fun changePassword(
        @Body request: RequestChangePassword
    ): Response<Unit>

    @PATCH("$version/me")
    suspend fun patchImage(
        @Body request: RequestPatch
    ): Response<Unit>


    @PATCH("$version/me")
    suspend fun updateUser(
        @Body request: RequestPatch
    ): Response<Unit>

    @PATCH("$version/me")
    suspend fun updateDrivingMode(
        @Body request: RequestUpdateDrivingMode
    ): Response<Unit>

    @PATCH("$version/me/notification")
    suspend fun subscribeFcm(
        @Body request: RequestSubscribeFcm
    ): Response<Unit>

    @DELETE("$version/me/notification")
    suspend fun unsubscribeFcm(): Response<Unit>

    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServicePerson {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_PERSON)
                .client(client.provideClient())
                .build()
                .create(ServicePerson::class.java)
        }
    }
}