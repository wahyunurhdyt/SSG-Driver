package id.semisamadriver.app.api.repository

import android.content.Context
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerNetwork
import id.semisamadriver.app.base.BaseRepository
import id.semisamadriver.app.utilily.tempProductId

class RepositoryProduct(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun getCategories(): ResponseCategories? {
        return apiRequest { managerNetwork.serviceSrv.getCategories() }
    }

    suspend fun getProduct(): ResponseProduct? {
        return apiRequest { managerNetwork.serviceSrv.getProduct() }
    }

    suspend fun getProductsRecommendations(): ResponseProductSRecommendations? {
        return apiRequest { managerNetwork.serviceSrv.getProductsRecommendations() }
    }

    suspend fun getProducts(
        region: String?,
        categories: String?,
        search: String?,
        isSelected: Boolean?,
        page: Int?,
        limit: Int?,
        sortBy: String?,
    ): ResponseProducts? {
        return apiRequest { managerNetwork.serviceSrv.getProducts(region, categories, search, isSelected, page, limit, sortBy) }
    }

    suspend fun updateStock(qty: Int): Unit? {
        isTokenOk()
        val req = RequestUpdateStock(qty)
        return apiRequest { managerNetwork.serviceSrv.updateStock(tempProductId!!, req) }
    }
}