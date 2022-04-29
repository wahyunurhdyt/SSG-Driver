package id.semisamadriver.app.ui.product.detail

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.ResponseProduct
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.utilily.Coroutines

class ViewModelDetail(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val title = MutableLiveData(Application.getString(R.string.labelTitleDetailProduct))
    val productDetail = MutableLiveData<ResponseProduct>()
    private val isFirst = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)
    val progressVisibility = MutableLiveData(View.GONE)
    val minVisibilivty = MutableLiveData(View.GONE)
    val connectionErrorVisibility = MutableLiveData(View.GONE)

    val onClickedAdd = View.OnClickListener {
        bridge?.add()
    }
    val onClickedMin = View.OnClickListener {
        bridge?.min()
    }

    val onClickRefresh = View.OnClickListener {
        bridge?.refreshData()
    }
    val qty = MutableLiveData<Int>()
    val qtyString = MutableLiveData("0")

    init{
        getProduct()
    }

    fun getProduct() {
        if (!isFirst.value!!) {
            loadingVisibility.postValue(View.VISIBLE)
        }else{
            progressVisibility.postValue(View.VISIBLE)
        }
        connectionErrorVisibility.postValue(View.GONE)
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProduct()
                productDetail.postValue(response!!)
                isFirst.postValue(true)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                connectionErrorVisibility.postValue(View.VISIBLE)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
                progressVisibility.postValue(View.GONE)
            }
        }
    }


    fun updateStock(quantity: Int) {
        progressVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryProduct.updateStock(quantity)
                getProduct()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
                qty.postValue(productDetail.value?.data?.stockQuantity!!)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }finally {
                progressVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun onClickedUpButton()
        fun add()
        fun min()
        fun refreshData()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}