package id.semisamadriver.app.ui.product.detail

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.Product
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityDetailBinding
import id.semisamadriver.app.ui.ViewModelFactoryDetail
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.baseUrlImageProducts
import org.kodein.di.generic.instance
import java.util.*

class ActivityDetail : BaseActivity(), ViewModelDetail.Bridge {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: ViewModelDetail
    private val factory: ViewModelFactoryDetail by instance()
    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        viewModel = ViewModelProvider(this, factory).get(ViewModelDetail::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            productDetail.observe(owner,  {
                binding.ivProduct.loadImageFromLink(baseUrlImageProducts + it.data.image)
                qty.postValue(it.data.stockQuantity)
            })
            qty.observe(owner,{
                if (it == 0){
                    minVisibilivty.postValue(View.GONE)
                }else{
                    minVisibilivty.postValue(View.VISIBLE)
                }
                if (it != productDetail.value?.data?.stockQuantity) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            // Update Stock
                            updateStock(it)
                        }
                    }, 1000L)
                }
                qtyString.postValue(it.toString())
            })
        }
    }


    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun add() {
        viewModel.qty.postValue(viewModel.qty.value?.toInt()!! + 1)
    }

    override fun min() {
        viewModel.qty.postValue(viewModel.qty.value?.toInt()!! - 1)
    }

    override fun refreshData() {
        viewModel.getProduct()
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.productDetail.value?.data?.id != null){
            tempProductId = viewModel.productDetail.value?.data?.id
        }
    }
}