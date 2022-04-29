package id.semisamadriver.app.ui.route.manage

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.dragview.ItemTouchHelperAdapter
import id.semisamadriver.app.adapter.dragview.MyItemTouchHelperCallback
import id.semisamadriver.app.adapter.dragview.OnStartDragListener
import id.semisamadriver.app.adapter.dragview.RouteAdapter
import id.semisamadriver.app.api.data.RequestUpdateRoute
import id.semisamadriver.app.api.data.Route
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityManageRouteBinding
import id.semisamadriver.app.ui.ViewModelFactoryManageRoute
import id.semisamadriver.app.utilily.snackbar
import id.semisamadriver.app.utilily.snackbarLong
import id.semisamadriver.app.utilily.tempRoutes
import org.kodein.di.generic.instance
import javax.security.auth.callback.Callback

class ActivityManageRoute : BaseActivity(), ViewModelManageRoute.Bridge {

    var itemTouchHelper: ItemTouchHelper? = null

    override fun onResume() {
        super.onResume()
        if(::binding.isInitialized) binding.cvMyRoute.visibility = View.GONE
        viewModel.getRoutes()
    }

    private lateinit var binding: ActivityManageRouteBinding
    private lateinit var viewModel: ViewModelManageRoute
    private val factory: ViewModelFactoryManageRoute by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initRecycler(data: MutableList<Route>) {
        binding.rvRoutes.setHasFixedSize(true)
        val adapter = RouteAdapter(this, data, object: OnStartDragListener{
            override fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
            {
                itemTouchHelper?.startDrag(viewHolder, )

            }
        })
        binding.rvRoutes.adapter = adapter
        val calback = MyItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(calback)
        itemTouchHelper?.attachToRecyclerView(binding.rvRoutes)
        adapter.setBridge(object: RouteAdapter.Bridge{
            override fun onClickDelete(item: Route, data: MutableList<Route>) {
                val list = data
                list.remove(item)
                viewModel.updateRoutes(RequestUpdateRoute(list))
            }

            override fun onItemMove(data: MutableList<Route>) {
                viewModel.newRoute.postValue(data)
            }

        })
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_route)
        viewModel = ViewModelProvider(this, factory).get(ViewModelManageRoute::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            routes.observe(owner, {
                binding.cvMyRoute.visibility = View.VISIBLE
                if (it.data.size == 0){
                    viewModel.emptyVisibility.postValue(View.VISIBLE)
                }else{
                    viewModel.emptyVisibility.postValue(View.GONE)
                    routeJson.postValue(Gson().toJson(it.data))
                }

                initRecycler(it.data)
            })
            newRoute.observe(owner,{
                val newData = Gson().toJson(it)
                if (newData == routeJson.value){
                    isButtonEnabled.postValue(false)
                }else{
                    isButtonEnabled.postValue(true)
                }
            })
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