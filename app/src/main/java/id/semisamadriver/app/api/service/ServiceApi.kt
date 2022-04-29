package id.semisamadriver.app.api.service

import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.util.Client
import id.semisamadriver.app.utilily.tempAuth
import id.semisamadriver.app.utilily.tempProductId
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServiceApi {

    @GET("$version/regions/{id}/sell-routes")
    suspend fun getRoutes(
        @Path("id") id: String = tempAuth?.user?.region?.id!!
    ): Response<ResponseRoute>

    @GET("$version/regions/{id}/location")
    suspend fun getRegionLocation(
        @Path("id") id: String = tempAuth?.user?.region?.id!!
    ): Response<ResponseGetRegionLocation>

    @GET("$version/regions/district")
    suspend fun getDistrict(
        @Query("lat") lat: Double?,
        @Query("lng") lng: Double?
    ): Response<ResponseGetDistrict>

    @PUT("$version/regions/{id}/sell-routes")
    suspend fun updateRoutes(
        @Body request: RequestUpdateRoute,
        @Path("id") id: String = tempAuth?.user?.region?.id!!
    ): Response<ResponseRoute>

    companion object {
        const val version = "v1"
        operator fun invoke(client: Client): ServiceApi {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BuildConfig.BASE_URL_API)
                .client(client.provideClient())
                .build()
                .create(ServiceApi::class.java)
        }
    }
}