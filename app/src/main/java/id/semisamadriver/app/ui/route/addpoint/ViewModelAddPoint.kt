package id.semisamadriver.app.ui.route.addpoint

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.RequestUpdateRoute
import id.semisamadriver.app.api.data.ResponseProduct
import id.semisamadriver.app.api.data.ResponseProductSRecommendations
import id.semisamadriver.app.api.data.ResponseRoute
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.ui.product.search.ActivitySearch
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.launchNewActivity
import id.semisamadriver.app.utilily.tempAuth
import id.semisamadriver.app.utilily.toast

class ViewModelAddPoint(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val title = MutableLiveData(Application.getString(R.string.labelAddPoint))

    val address = MutableLiveData<String>()
    val center = MutableLiveData<LatLng>()
    val loadingVisibility = MutableLiveData(View.GONE)
    val buttonEnabled = MutableLiveData(true)


    fun updateRoute(req: RequestUpdateRoute) {
        loadingVisibility.postValue(View.VISIBLE)
        buttonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryRoute.updateRoutes(req)
                Application.getContext().toast("Berhasil Menambah Titik")
                bridge?.onClickedUpButton()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                buttonEnabled.postValue(true)
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    fun getDistrictName(lat: Double, lng: Double) {
        loadingVisibility.postValue(View.VISIBLE)
        buttonEnabled.postValue(false)
        Coroutines.main {
            try {
                address.postValue(managerRepository.repositoryRoute.getDistrict(lat, lng)?.data?.name!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                buttonEnabled.postValue(true)
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