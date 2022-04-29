package id.semisamadriver.app.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.DataAuth
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityLoginBinding
import id.semisamadriver.app.ui.ViewModelFactoryLogin
import id.semisamadriver.app.ui.navigation.ActivityNavigation
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.authTemps
import id.semisamadriver.app.utilily.Constant.getAuth
import org.kodein.di.generic.instance

class ActivityLogin : BaseActivity(), ViewModelLogin.Bridge {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: ViewModelLogin
    private val factory: ViewModelFactoryLogin by instance()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        if (haveLocationPermissions()) {
            fetchLocation()
        }
        val auth = cache.get(authTemps)
        if (!auth.isNullOrEmpty()){
            val data = getAuth(auth)
            tempAuth = data
            next(data)
        }
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, factory).get(ViewModelLogin::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        viewModel.email.observe(this, {
            viewModel.checkButton()
        })
        viewModel.password.observe(this,  {
            viewModel.checkButton()
        })
    }

    override fun next(data: DataAuth) {
        cache.set(authTemps, data)
        tempAddress = data.user?.region?.name!!
        launchNewActivity(ActivityNavigation::class.java,
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

}