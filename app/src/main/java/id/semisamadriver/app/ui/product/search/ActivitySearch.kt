package id.semisamadriver.app.ui.product.search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.Product
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivitySearchBinding
import id.semisamadriver.app.ui.ViewModelFactorySearch
import id.semisamadriver.app.ui.product.detail.ActivityDetail
import id.semisamadriver.app.utilily.*
import org.kodein.di.generic.instance

class ActivitySearch : BaseActivity(), ViewModelSearch.Bridge {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: ViewModelSearch
    private val factory: ViewModelFactorySearch by instance()
    private lateinit var adapterProduct: Adapter<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        showProducts()
    }


    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        viewModel = ViewModelProvider(this, factory).get(ViewModelSearch::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.layoutEmpty.ivEmpty.setImageResource(R.drawable.ic_empty_search)
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            search.observe(owner, Observer {
                if (it != null) {
                    getProduct(it, 1)
                }
            })
            product.observe(owner, {
                if (it.data.totalResults == 0) {
                    totalResults.postValue("Tidak dapat menemukan \"${search.value}\"")
                    emptyVisibility.postValue(View.VISIBLE)
                    textEmpty.postValue(getString(R.string.labelEmptySearch))
                    adapterProduct.clearScrollListener(getScrollListener())
                } else {
                    totalResults.postValue("${it.data.totalResults} Hasil Pencarian \"${search.value}\"")
                    emptyVisibility.postValue(View.GONE)
                    if (it.data.page == 1){
                        adapterProduct.clearScrollListener(getScrollListener())
                    }
                    adapterProduct.addAll(it.data.results)
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showProducts() {
        adapterProduct = Adapter(R.layout.item_product, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val price = itemView.findViewById<TextView>(R.id.tvPrice)
                val unit = itemView.findViewById<TextView>(R.id.tvUnit)
                val image = itemView.findViewById<ImageView>(R.id.ivProduct)
                name.text = item.name
                price.text = item.getPriceFormat()
                unit.text = item.getUnitDescription()
                image.loadImageFromLink(Constant.baseUrlImageProducts +item.image)
            },
            { _, item ->
                tempProductId = item.id
                launchNewActivity(ActivityDetail::class.java)
            })
        binding.rvProducts.adapter = adapterProduct
        binding.rvProducts.addOnScrollListener(getScrollListener())
    }

    private fun getScrollListener(): EndlessRecyclerViewScrollListener {
        val layoutManager = binding.rvProducts.layoutManager as GridLayoutManager
        return object: EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int) {
                viewModel.apply { getProduct(viewModel.search.value!!,page) }
            }
        }
    }

    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}