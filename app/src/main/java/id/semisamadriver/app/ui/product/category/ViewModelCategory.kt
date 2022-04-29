package id.semisamadriver.app.ui.product.category

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.api.data.Category
import id.semisamadriver.app.api.data.ResponseProducts
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.ui.product.search.ActivitySearch
import id.semisamadriver.app.utilily.*

class ViewModelCategory(
    private val managerRepository: ManagerRepository
): ViewModel() {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    val categories = MutableLiveData<MutableList<Category>>()
    val product = MutableLiveData<ResponseProducts>()
    val totalResults = MutableLiveData("")
    val textEmpty = MutableLiveData("")
    val loadingVisibility = MutableLiveData(View.GONE)
    val totalResultVisibility = MutableLiveData(View.GONE)
    val emptyVisibility = MutableLiveData(View.GONE)
    val progressVisibility = MutableLiveData(View.GONE)

    init{
        getCategories()
        getProduct(tempCategoryId!!, 1)
    }

    fun View.onClickedSearch(){
        context.launchNewActivity(ActivitySearch::class.java)
    }

    private fun getCategories() {
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getCategories()?.data
                categories.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            }
        }
    }

    fun getProduct(categoryId: String, page: Int) {
        if (page == 1){
            loadingVisibility.postValue(View.VISIBLE)
            emptyVisibility.postValue(View.GONE)
        }else{
            progressVisibility.postValue(View.VISIBLE)
        }
        Coroutines.main {
            try {
                val response = managerRepository.repositoryProduct.getProducts(
                    tempAuth?.user?.region?.id,
                    categoryId,
                    null,
                    null,
                    page,
                    6,
                    null,

                )
                product.postValue(response!!)
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                totalResultVisibility.postValue(View.GONE)
                emptyVisibility.postValue(View.VISIBLE)
                textEmpty.postValue("$tempCategoryName Sedang Kosong")
                bridge?.showSnackbarLong(e.message)
            }
            finally {
                loadingVisibility.postValue(View.GONE)
                progressVisibility.postValue(View.GONE)
            }
        }
    }

    interface Bridge {
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}