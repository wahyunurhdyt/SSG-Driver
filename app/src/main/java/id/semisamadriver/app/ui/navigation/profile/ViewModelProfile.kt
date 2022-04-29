package id.semisamadriver.app.ui.navigation.profile

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import id.semisamadriver.app.api.data.User
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.ui.person.edit.ActivityEdit
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.launchNewActivity
import id.semisamadriver.app.utilily.tempAddress

class ViewModelProfile(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    val user = MutableLiveData<User>()
    val city = MutableLiveData(tempAddress)
    val textInitial = MutableLiveData("")
    val loadingVisibility = MutableLiveData(View.GONE)

    fun View.onClickedLogout() {
        bridge?.showLogoutDialog()
    }

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }


    fun logoutAccount(){
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryAuth.postLogout()
                bridge?.logout()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                loadingVisibility.postValue(View.GONE)
            }
        }

    }
    fun View.onClickedEdit(){
        context.launchNewActivity(ActivityEdit::class.java)
    }


    interface Bridge {
        fun logout()
        fun showLogoutDialog()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}