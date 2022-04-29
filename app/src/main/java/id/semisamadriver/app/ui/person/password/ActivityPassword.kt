package id.semisamadriver.app.ui.person.password

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityPasswordBinding
import id.semisamadriver.app.ui.ViewModelFactoryPassword
import id.semisamadriver.app.utilily.*
import org.kodein.di.generic.instance

class ActivityPassword : BaseActivity(), ViewModelPassword.Bridge {

    private lateinit var binding: ActivityPasswordBinding
    private lateinit var viewModel: ViewModelPassword
    private val factory: ViewModelFactoryPassword by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
    }

    private fun initBinding(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_password)
        viewModel = ViewModelProvider(this, factory).get(ViewModelPassword::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

    }

    private fun initObserver(){
        viewModel.oldPassword.observe(this, {
            viewModel.checkButton()
        })
        viewModel.password.observe(this, {
            viewModel.checkButton()
        })
        viewModel.passwordConfirmation.observe(this, {
            viewModel.checkButton()
        })
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