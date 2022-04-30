package id.semisamadriver.app.ui.navigation.home

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.util.SocketServices
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.base.BaseFragment
import id.semisamadriver.app.databinding.FragmentHomeBinding
import id.semisamadriver.app.ui.ViewModelFactoryHome
import id.semisamadriver.app.ui.product.category.FragmentCategory
import id.semisamadriver.app.ui.product.detail.ActivityDetail
import id.semisamadriver.app.ui.product.productssg.FragmentProduct
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.baseUrlImageCategories
import id.semisamadriver.app.utilily.Constant.baseUrlImageProducts
import id.semisamadriver.app.utilily.Constant.currentActivity
import id.semisamadriver.app.utilily.Constant.home
import io.socket.client.IO
import io.socket.client.Socket
import org.kodein.di.generic.instance
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList

class FragmentHome : BaseFragment(), OnMapReadyCallback, ViewModelHome.Bridge {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: ViewModelHome
    private val factory: ViewModelFactoryHome by instance()
    private lateinit var adapterProduct: Adapter<Product>
    private lateinit var adapterRecommend: Adapter<Product>
    private lateinit var adapterCategory: Adapter<Category>
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var map: GoogleMap? = null

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        buildLocationRequest()
        buildLocationCallBack()
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(Application.getContext())

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.myLooper()!!
        )

    }

    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        val lat = location.latitude
                        val long = location.longitude
                        viewModel.driverLocation.postValue(LatLng(lat, long))
                    } else {
                        println("Can't Get Your Location")
                    }
                }
            }
        }
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.interval = 5000
        locationRequest!!.fastestInterval = 5000
        locationRequest!!.smallestDisplacement = 5f
    }

    override fun onResume() {
        super.onResume()
        if(::binding.isInitialized) {
            binding.mvMain.onResume()
            viewModel.getRegionLocation()
            viewModel.getMe()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(::binding.isInitialized) binding.mvMain.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelHome::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.mvMain.onCreate(savedInstanceState)
        binding.mvMain.getMapAsync(this)
        Handler().postDelayed({
            getLocation() }, 5000)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentActivity = home
        initObserver()
        initRefresh()
        if (!tempFragmenHasLoadedData){
            loadData()
            tempFragmenHasLoadedData = true
        }
    }

    private fun loadData() {
        viewModel.getCategories()
        viewModel.getProductSelected()
        viewModel.getProductRecommend()
        viewModel.getRegionLocation()
        viewModel.getMe()
    }

    private fun initRefresh(){
        binding.srlLayout.setOnRefreshListener {
            loadData()
            binding.srlLayout.isRefreshing = false
        }
    }

    @SuppressLint("NewApi", "MissingPermission")
    private fun initObserver() {
        val owner = this
        viewModel.apply {
            product.observe(owner, {
                showProducts(it.data.results)
            })
            productRecommend.observe(owner, {
                showProductRecommend(it.data.results)
            })
            categories.observe(owner, {
                showCategory(it)
            })
            driverLocation.observe(owner, {
                if (viewModel.routes.value != null){
                    loadRoute(viewModel.routes.value!!.data, it)
                }
            })
            routes.observe(owner, {
                loadRoute(it.data, viewModel.driverLocation.value!!)
            })
            user.observe(owner, {
                if (it.isDrivingMode!!){
                    if(!isMyServiceRunning(SocketServices::class.java)){
                        startService()
                    }
                    if (map != null){
                        map!!.isMyLocationEnabled = true
                    }
                    binding.btnStartDriving.text = Application.getString(R.string.labelStopDriving)
                    binding.btnStartDriving.setBackgroundColor(requireActivity().getColor(R.color.colorStopDriving))
                    binding.btnStartDriving.setOnClickListener {
                        dialogUpdateDrivingMode(false)
                    }
                }else{
                    if(isMyServiceRunning(SocketServices::class.java)){
                        stopService()
                    }
                    if (map != null){
                        map!!.isMyLocationEnabled = false
                    }
                    binding.btnStartDriving.text = Application.getString(R.string.labelStartDriving)
                    binding.btnStartDriving.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary))
                    binding.btnStartDriving.setOnClickListener {
                        dialogUpdateDrivingMode(true)
                    }
                }
            })
        }
    }

    @SuppressLint("NewApi")
    private fun dialogUpdateDrivingMode(drivingMode: Boolean){
        val textButton: String
        val title: String
        val desc1: String
        val desc2: String
        if (drivingMode){
            textButton = Application.getString(R.string.labelButtonStart)
            title = Application.getString(R.string.labelTitleStart)
            desc1 = Application.getString(R.string.labelDesc1Start)
            desc2 = Application.getString(R.string.labelDesc2Start)
        }else{
            textButton = Application.getString(R.string.labelButtonStop)
            title = Application.getString(R.string.labelTitleStop)
            desc1 = Application.getString(R.string.labelDesc1Stop)
            desc2 = Application.getString(R.string.labelDesc2Stop)
        }

        val bgColor = if (drivingMode) {
            requireActivity().getColor(R.color.colorPrimary)
        }else{
            requireActivity().getColor(R.color.colorStopDriving)
        }

        customDialog(requireActivity(), R.layout.dialog_update_driving_mode
        ) { itemView, dialog  ->
            val titleText = itemView.findViewById<TextView>(R.id.tvTitle)
            val desc1Text = itemView.findViewById<TextView>(R.id.tvDesc1)
            val desc2Text = itemView.findViewById<TextView>(R.id.tvDesc2)
            val btnConfirm = itemView.findViewById<Button>(R.id.btnConfirmUpdate)
            titleText.text = title
            desc1Text.text = desc1
            desc2Text.text = desc2
            btnConfirm.text = textButton
            btnConfirm.setBackgroundColor(bgColor)
            btnConfirm.setOnClickListener {
                viewModel.updateDrivingMode(drivingMode)
                dialog.dismiss()
            }

        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) return true
        }
        return false
    }

    private fun showCategory(data: MutableList<Category>) {
        adapterCategory = Adapter(R.layout.item_category, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                val image = itemView.findViewById<ImageView>(R.id.ivCategory)
                name.text = item.name
                image.loadImageFromLink(baseUrlImageCategories + item.image)

            },
            { _, item ->
                tempCategoryId = item.id
                tempCategoryName = item.name
                val fragment = FragmentCategory()
                val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.fMain, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            })
        binding.rvCategory.adapter = adapterCategory
        adapterCategory.data = data
    }

    @SuppressLint("SetTextI18n")
    private fun showProducts(data: MutableList<Product>) {
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
        adapterProduct.data = data
    }

    @SuppressLint("SetTextI18n")
    private fun showProductRecommend(data: MutableList<Product>) {
        adapterRecommend = Adapter(R.layout.item_product, mutableListOf(),
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
        binding.rvRecommends.adapter = adapterRecommend
        adapterRecommend.data = data
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    private fun loadRoute(data: MutableList<Route>, driverLocation: LatLng){
        map?.clear()

        //Driver Marker
        val driverMarker = ((ContextCompat.getDrawable(
            Application.getContext(),
            R.drawable.driver
        )) as BitmapDrawable).bitmap
        val driverIcon = Bitmap.createScaledBitmap(driverMarker, 96, 96, false)
        map?.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(driverIcon))
                .position(driverLocation)
                .title("Lokasi Anda")
        )

        val builder = LatLngBounds.Builder()
        builder.include(driverLocation)

        val size = data.size
        val bitmap = ((ContextCompat.getDrawable(
            Application.getContext(),
            R.drawable.marker
        )) as BitmapDrawable).bitmap




        for (i in 0 until size) {
            val lat = data[i].location?.coordinates!![1]
            val lng = data[i].location?.coordinates!![0]
            val position = LatLng(lat, lng)
            val name = data[i].name

            val markerIcon = Bitmap.createScaledBitmap(bitmap, 72, 72, false)

            if (size > 1 && i < (size-1)){
                addPolyline(position, LatLng(data[i+1].location?.coordinates!![1], data[i+1].location?.coordinates!![0]))
            }

            builder.include(position)
            map?.addMarker(
                MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(markerIcon))
                    .position(position)
                    .snippet((i+1).toString())
                    .title(name)
            )
        }
        when (size) {
            0 -> {
                val location = LatLng(tempLocation?.longitude!!, tempLocation?.latitude!!)
                map?.addMarker(
                    MarkerOptions()
                        .position(location)
                )
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }
            1 -> {
                val lat = data[0].location?.coordinates!![1]
                val lng = data[0].location?.coordinates!![0]
                val location = LatLng(lat, lng)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
            }
            else -> {
                val bounds = builder.build()
                val padding = Application.getContext().getPx(32) // offset from edges of the map in pixels
                val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                map?.moveCamera(cu)
            }
        }

    }

    private fun addPolyline(start: LatLng, end: LatLng){
        map?.addPolyline(PolylineOptions().add(start, end).width(5f).color(Color.BLACK).geodesic(true))
    }

    override fun viewAllProduct(isSelected: Boolean?) {
        tempSelectedProduct = isSelected
        val fragment = FragmentProduct()
        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.fMain, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun refreshData() {
        loadData()
    }

    override fun startService() {
        val intentService = Intent(requireActivity(), SocketServices::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(intentService)
        } else {
            requireActivity().startService(intentService)
        }
    }

    override fun stopService() {
        val intentService = Intent(requireActivity(), SocketServices::class.java)
        requireActivity().stopService(intentService)
    }


    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}
