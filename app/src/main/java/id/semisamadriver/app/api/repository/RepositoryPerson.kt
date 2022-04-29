package id.semisamadriver.app.api.repository

import android.content.Context
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerNetwork
import id.semisamadriver.app.base.BaseRepository

class RepositoryPerson(
    context: Context,
    private val managerNetwork: ManagerNetwork
): BaseRepository(context) {

    suspend fun getUser(): ResponseUser? {
        if (!isTokenOk()) return null
        return apiRequest { managerNetwork.servicePerson.getUser() }
    }

    suspend fun setPassword(password: String): Unit? {
        if (!isTokenOk()) return null
        val req = RequestPassword(password)
        return apiRequest { managerNetwork.servicePerson.setPassword(req) }
    }

    suspend fun changePassword(oldPassword: String, newPassword: String): Unit? {
        if (!isTokenOk()) return null
        val req = RequestChangePassword(oldPassword, newPassword)
        return apiRequest { managerNetwork.servicePerson.changePassword(req) }
    }

    suspend fun patchImage(image: String): Unit? {
        if (!isTokenOk()) return null
        val req = RequestPatch(null, null, null, image)
        return apiRequest { managerNetwork.servicePerson.patchImage(req) }
    }

    suspend fun updateUser(
        fullName: String?,
        email: String?,
        phone: String?
    ): Unit? {
        if (!isTokenOk()) return null
        val req = RequestPatch(fullName, email, phone, null)
        return apiRequest { managerNetwork.servicePerson.updateUser(req) }
    }

    suspend fun updateDrivingMode(
        drivingMode: Boolean
    ): Unit? {
        if (!isTokenOk()) return null
        val req = RequestUpdateDrivingMode(drivingMode)
        return apiRequest { managerNetwork.servicePerson.updateDrivingMode(req) }
    }

    suspend fun subscribeFcm(fcmToken: String): Unit? {
        if (!isTokenOk()) return null
        val req = RequestSubscribeFcm(fcmToken, "android")
        return apiRequest { managerNetwork.servicePerson.subscribeFcm(req) }
    }

    suspend fun unsubscribeFcm(): Unit? {
        return apiRequest { managerNetwork.servicePerson.unsubscribeFcm() }
    }

    suspend fun checkToken(){
        isTokenOk()
    }
}