package id.semisamadriver.app.ui.navigation.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.ui.product.search.ActivitySearch
import id.semisamadriver.app.ui.route.allroute.ActivityRoute
import id.semisamadriver.app.ui.route.manage.ActivityManageRoute

class ViewModelHome(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }
    val user = MutableLiveData<User>()
    val categories = MutableLiveData<MutableList<Category>>()
    val routes = MutableLiveData<ResponseRoute>()
    val driverLocation = MutableLiveData<LatLng>()
    val product = MutableLiveData<ResponseProducts>()
    val productRecommend = MutableLiveData<ResponseProducts>()
    val loadingVisibility = MutableLiveData(View.VISIBLE)
    val connectionErrorVisibility = MutableLiveData(View.GONE)


    fun View.onClickedSearch(){
        context.launchNewActivity(ActivitySearch::class.java)
    }

    fun View.onClickedManageRoute(){
        context.launchNewActivity(ActivityRoute::class.java)
    }


    val onClickedViewAllProduct = View.OnClickListener {
        bridge?.viewAllProduct(true)
    }

    val onClickedViewAllProductRecommend = View.OnClickListener {
        bridge?.viewAllProduct(null)
    }

    val onClickRefresh = View.OnClickListener {
        bridge?.refreshData()
    }

    fun getCategories() {
        loadingVisibility.postValue(View.VISIBLE)
        connectionErrorVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getCategories()?.data
                categories.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                connectionErrorVisibility.postValue(View.VISIBLE)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun getMe() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryPerson.getUser()
                user.postValue(response!!.data)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
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
               getMe()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
            }
        }
    }

    fun getProductSelected() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempAuth?.user?.region?.id,
                    null,
                    null,
                    true,
                    null,
                    4,
                    null,
                )
                product.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getProductRecommend() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempAuth?.user?.region?.id,
                    null,
                    null,
                    null,
                    null,
                    4,
                    null
                )
                productRecommend.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    private fun getRoutes() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryRoute.getRoutes()
                routes.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getRegionLocation() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryRoute.getRegionLocation()
                tempLocation = Location(response?.data?.location?.coordinates!![1], response.data.location.coordinates!![0])
                getRoutes()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }


    interface Bridge {
        fun viewAllProduct(isSelected: Boolean?)
        fun refreshData()
        fun startService()
        fun stopService()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}