package id.semisamadriver.app.api.repository

import android.content.Context
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerNetwork
import id.semisamadriver.app.base.BaseRepository
import id.semisamadriver.app.utilily.tempAuth

class RepositoryAuth(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {


    suspend fun postLogin(
        email: String?,
        password: String?
    ): ResponseAuth? {
        val req = RequestLogin(email, password)
        return apiRequest { managerNetwork.serviceAuth.postLogin(req) }
    }

    suspend fun postLogout(): Unit? {
        val req = RequestRefreshToken(tempAuth?.tokens?.refresh?.token)
        return apiRequest { managerNetwork.serviceAuth.logout(req) }
    }
}