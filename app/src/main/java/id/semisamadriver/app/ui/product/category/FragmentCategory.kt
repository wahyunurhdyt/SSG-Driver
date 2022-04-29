package id.semisamadriver.app.ui.product.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.Category
import id.semisamadriver.app.api.data.Product
import id.semisamadriver.app.base.BaseFragment
import id.semisamadriver.app.databinding.FragmentCategoryBinding
import id.semisamadriver.app.ui.ViewModelFactoryCategory
import id.semisamadriver.app.ui.product.detail.ActivityDetail
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.baseUrlImageCategories
import id.semisamadriver.app.utilily.Constant.baseUrlImageProducts
import id.semisamadriver.app.utilily.Constant.currentActivity
import id.semisamadriver.app.utilily.Constant.home
import org.kodein.di.generic.instance

class FragmentCategory : BaseFragment(), ViewModelCategory.Bridge {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var viewModel: ViewModelCategory
    private val factory: ViewModelFactoryCategory by instance()
    private lateinit var adapterProduct: Adapter<Product>
    private lateinit var adapterCategory: Adapter<Category>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_category,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelCategory::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.layoutEmpty.ivEmpty.setImageResource(R.drawable.ic_empty_category)

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
                if (it.data.totalResults == 0) {
                    totalResultVisibility.postValue(View.GONE)
                    emptyVisibility.postValue(View.VISIBLE)
                    textEmpty.postValue("$tempCategoryName Sedang Kosong")
                } else {
                    totalResultVisibility.postValue(View.VISIBLE)
                    totalResults.postValue("Menampilkan ${it.data.totalResults} Product")
                    emptyVisibility.postValue(View.GONE)
                    if (it.data.page == 1){
                        adapterProduct.clearScrollListener(getScrollListener())
                    }
                    adapterProduct.addAll(it.data.results)
                }
            })
            categories.observe(owner, {
                showCategory(it)
            })
        }
    }

    private fun showCategory(data: MutableList<Category>) {
        adapterCategory = Adapter(R.layout.item_category, mutableListOf(),
            { itemView, item ->

                val container = itemView.findViewById<CardView>(R.id.container)
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val image = itemView.findViewById<ImageView>(R.id.ivCategory)
                name.text = item.name
                image.loadImageFromLink(baseUrlImageCategories + item.image)

                for (i in 0 until data.size){
                    if (data[i].id == tempCategoryId){
                        binding.rvCategory.scrollToPosition(i)
                    }
                }

                if (item.id == tempCategoryId){
                    name.setTextColor(resources.getColor(R.color.white))
                    container.setCardBackgroundColor(resources.getColor(R.color.colorPrimary))
                }else{
                    name.setTextColor(resources.getColor(R.color.black_70))
                    container.setCardBackgroundColor(resources.getColor(R.color.white))
                }

            },
            { _, item ->
                if (item.id != tempCategoryId) {
                    tempCategoryId = item.id
                    tempCategoryName = item.name
                    adapterCategory.refresh()
                    viewModel.getProduct(item.id, 1)
                }
            })
        binding.rvCategory.adapter = adapterCategory
        adapterCategory.data = data
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
                viewModel.apply { getProduct(tempCategoryId!!, page) }
            }
        }
    }


    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}