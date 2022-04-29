package id.semisamadriver.app.ui.route.allroute

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.ResponseRoute
import id.semisamadriver.app.api.data.User
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.ui.product.search.ActivitySearch
import id.semisamadriver.app.ui.route.manage.ActivityManageRoute
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.launchNewActivity
import id.semisamadriver.app.utilily.tempAddress

class ViewModelRoute(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val title = MutableLiveData(Application.getString(R.string.labelSeeRoute))
    val user = MutableLiveData<User>()
    val address = MutableLiveData(
        tempAddress
    )

    fun View.onClickedManageRoute(){
        context.launchNewActivity(ActivityManageRoute::class.java)
    }

    val routes = MutableLiveData<ResponseRoute>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val emptyVisibility = MutableLiveData(View.GONE)

    init{
        getRoutes()
    }


    fun getRoutes() {
        loadingVisibility.postValue(View.VISIBLE)
        emptyVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                routes.postValue(managerRepository.repositoryRoute.getRoutes())
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                emptyVisibility.postValue(View.VISIBLE)
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun updateDrivingMode(drivingMode: Boolean) {
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.updateDrivingMode(drivingMode)
                if (drivingMode){
                    bridge?.showSnackbarLong("Berhasil Memulai Keliling")
                    bridge?.startService()
                }else{
                    bridge?.showSnackbarLong("Berhasil Berhenti Keliling")
                    bridge?.stopService()
                }
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
            }
        }
    }

    interface Bridge {
        fun onClickedUpButton()
        fun startService()
        fun stopService()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}