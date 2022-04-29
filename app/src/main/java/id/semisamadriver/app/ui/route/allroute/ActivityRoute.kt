package id.semisamadriver.app.ui.route.allroute

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.Route
import id.semisamadriver.app.api.util.SocketServices
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityRouteBinding
import id.semisamadriver.app.ui.ViewModelFactoryRoute
import id.semisamadriver.app.utilily.*
import org.kodein.di.generic.instance

class ActivityRoute : BaseActivity(), ViewModelRoute.Bridge {
    private lateinit var binding: ActivityRouteBinding
    private lateinit var viewModel: ViewModelRoute
    private val factory: ViewModelFactoryRoute by instance()
    private lateinit var adapterRoute: Adapter<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        showRoute()
        initView()
    }

    private fun initView() {
        val address = SpannableString(
            " $tempAddress").also {
            it.recolorAndBold(this, R.color.black_70)
        }
        binding.tvDesc.append(address)
        binding.tvDesc.append(getString(R.string.labelDescManageRoute2))
    }


    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_route)
        viewModel = ViewModelProvider(this, factory).get(ViewModelRoute::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    @SuppressLint("NewApi")
    private fun initObserver(){
        val owner = this
        viewModel.apply {
            routes.observe(owner, {
                if (it.data.size == 0){
                    emptyVisibility.postValue(View.VISIBLE)
                }else{
                    emptyVisibility.postValue(View.GONE)
                }
                adapterRoute.data = it.data
                binding.cvMyRoute.visibility = View.VISIBLE
            })
            user.observe(owner, {
                if (it.isDrivingMode!!){
                    if(!isMyServiceRunning(SocketServices::class.java)){
                        startService()
                    }
                    binding.btnStartDriving.text = Application.getString(R.string.labelStopDriving)
                    binding.btnStartDriving.setBackgroundColor(getColor(R.color.colorStopDriving))
                    binding.btnStartDriving.setOnClickListener {
                        dialogUpdateDrivingMode(false)
                    }
                }else{
                    if(isMyServiceRunning(SocketServices::class.java)){
                        stopService()
                    }
                    binding.btnStartDriving.text = Application.getString(R.string.labelStartDriving)
                    binding.btnStartDriving.setBackgroundColor(getColor(R.color.colorPrimary))
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
            getColor(R.color.colorPrimary)
        }else{
            getColor(R.color.colorStopDriving)
        }

        customDialog(this, R.layout.dialog_update_driving_mode
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
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) return true
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun showRoute() {
        adapterRoute = Adapter(R.layout.item_route, mutableListOf(),
            { itemView, item ->
                val name = itemView.findViewById<TextView>(R.id.tvName)
                name.text = item.name
            },
            { _, _ ->

            })
        binding.rvRoutes.adapter = adapterRoute
    }

    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun startService() {
        val intentService = Intent(this, SocketServices::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intentService)
        } else {
            startService(intentService)
        }
    }

    override fun stopService() {
        val intentService = Intent(this, SocketServices::class.java)
        stopService(intentService)
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getRoutes()
    }
}