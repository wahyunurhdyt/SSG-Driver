package id.semisamadriver.app.api.repository

import android.content.Context
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerNetwork
import id.semisamadriver.app.base.BaseRepository

class RepositoryRoute(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {


    suspend fun getRoutes(): ResponseRoute? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.servieceApi.getRoutes() }
    }

    suspend fun getRegionLocation(): ResponseGetRegionLocation? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.servieceApi.getRegionLocation() }
    }

    suspend fun getDistrict(lat: Double, lng: Double): ResponseGetDistrict? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.servieceApi.getDistrict(lat, lng) }
    }

    suspend fun updateRoutes(req: RequestUpdateRoute): ResponseRoute? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.servieceApi.updateRoutes(req) }
    }
}