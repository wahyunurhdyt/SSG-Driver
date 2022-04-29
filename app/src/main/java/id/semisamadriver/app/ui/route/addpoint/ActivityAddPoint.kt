package id.semisamadriver.app.ui.route.addpoint

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.RequestUpdateRoute
import id.semisamadriver.app.api.data.Route
import id.semisamadriver.app.api.data.UserLocation
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityAddPointBinding
import id.semisamadriver.app.ui.ViewModelFactoryAddPoint
import id.semisamadriver.app.utilily.*
import org.kodein.di.generic.instance

class ActivityAddPoint : BaseActivity(), ViewModelAddPoint.Bridge, OnMapReadyCallback {

    private lateinit var binding: ActivityAddPointBinding
    private lateinit var viewModel: ViewModelAddPoint
    private val factory: ViewModelFactoryAddPoint by instance()

    private var map: GoogleMap? = null

    override fun onResume() {
        super.onResume()
        if(::binding.isInitialized) binding.mvMain.onResume()
    }

    override fun onPause() {
        super.onPause()
        if(::binding.isInitialized) binding.mvMain.onPause()
    }

    override fun onStart() {
        super.onStart()
        if(::binding.isInitialized) binding.mvMain.onStart()
    }

    override fun onStop() {
        super.onStop()
        if(::binding.isInitialized) binding.mvMain.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::binding.isInitialized) binding.mvMain.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if(::binding.isInitialized) binding.mvMain.onLowMemory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding(savedInstanceState)
        initObserver()
    }

    private fun initBinding(savedInstanceState: Bundle?){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_point)
        viewModel = ViewModelProvider(this, factory).get(ViewModelAddPoint::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.mvMain.onCreate(savedInstanceState)
        binding.mvMain.getMapAsync(this)
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            center.observe(owner, {
                getDistrictName(it.latitude, it.longitude)
            })
            address.observe(owner, {
                val lat = center.value?.latitude!!.toString().substring(0, 10)
                val lng = center.value?.longitude!!.toString().substring(0, 10)
                val coordinates: ArrayList<Double> = ArrayList()
                coordinates.add(lng.toDouble())
                coordinates.add(lat.toDouble())
                val routes: MutableList<Route> = tempRoutes!!
                routes.add(Route(UserLocation("Point", coordinates), it))
                val req = RequestUpdateRoute(routes)
                viewModel.updateRoute(req)
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

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        val location = LatLng(tempLocation?.latitude!!, tempLocation?.longitude!!)
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
        binding.btnAddPoint.setOnClickListener {
            val center = map?.cameraPosition?.target
            viewModel.center.postValue(center)
        }
    }
}