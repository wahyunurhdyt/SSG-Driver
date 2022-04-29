package id.semisamadriver.app.ui.route.manage

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.ui.product.search.ActivitySearch
import id.semisamadriver.app.ui.route.addpoint.ActivityAddPoint
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.launchNewActivity
import id.semisamadriver.app.utilily.tempAddress
import id.semisamadriver.app.utilily.tempRoutes

class ViewModelManageRoute(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val onClickedUpdateRoute = View.OnClickListener { updateRoutes(RequestUpdateRoute(newRoute.value)) }
    val title = MutableLiveData(Application.getString(R.string.labelManageRoute))
    val address = MutableLiveData(tempAddress)

    val routes = MutableLiveData<ResponseRoute>()
    val routeJson = MutableLiveData<String>()
    val newRoute = MutableLiveData<MutableList<Route>>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val emptyVisibility = MutableLiveData(View.GONE)
    val isButtonEnabled = MutableLiveData(false)

    fun View.onClickedAddPoint(){
        if (routes.value != null){
            tempRoutes = routes.value!!.data
            context.launchNewActivity(ActivityAddPoint::class.java)
        }
    }


    init{
        getRoutes()
    }

    fun getRoutes() {
        loadingVisibility.postValue(View.VISIBLE)
        emptyVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                routes.postValue(managerRepository.repositoryRoute.getRoutes())
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

    fun updateRoutes(req: RequestUpdateRoute) {
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                routes.postValue(managerRepository.repositoryRoute.updateRoutes(req))
                isButtonEnabled.postValue(false)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun onClickedUpButton()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}