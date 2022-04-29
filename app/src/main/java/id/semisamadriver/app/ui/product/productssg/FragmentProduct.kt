package id.semisamadriver.app.ui.product.productssg

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.Product
import id.semisamadriver.app.base.BaseFragment
import id.semisamadriver.app.databinding.FragmentProductBinding
import id.semisamadriver.app.ui.ViewModelFactoryProduct
import id.semisamadriver.app.ui.product.detail.ActivityDetail
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.baseUrlImageProducts
import id.semisamadriver.app.utilily.Constant.currentActivity
import id.semisamadriver.app.utilily.Constant.home
import org.kodein.di.generic.instance

class FragmentProduct : BaseFragment(), ViewModelProduct.Bridge {

    private lateinit var binding: FragmentProductBinding
    private lateinit var viewModel: ViewModelProduct
    private val factory: ViewModelFactoryProduct by instance()
    private lateinit var adapterProduct: Adapter<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_product,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelProduct::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentActivity = home
        initObserver()
        showProducts()
    }

    private fun initObserver() {
        val owner = this
        viewModel.apply {
            product.observe(owner, {
                adapterProduct.addAll(it.data.results)
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
                image.loadImageFromLink(baseUrlImageProducts+item.image)

            },
            { _, item ->
                tempProductId = item.id
                context?.launchNewActivity(ActivityDetail::class.java)
            })
        binding.rvProducts.adapter = adapterProduct
        binding.rvProducts.addOnScrollListener(getScrollListener())
    }

    private fun getScrollListener(): EndlessRecyclerViewScrollListener {
        val layoutManager = binding.rvProducts.layoutManager as GridLayoutManager
        return object: EndlessRecyclerViewScrollListener(layoutManager){
            override fun onLoadMore(page: Int) {
                viewModel.page.postValue(page)
                viewModel.apply { getProduct(page) }
            }
        }
    }

    override fun refreshData() {
        viewModel.getProduct(viewModel.page.value!!)
    }


    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}