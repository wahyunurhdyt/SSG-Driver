package id.semisamadriver.app.ui.navigation.profile

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.adapter.Adapter
import id.semisamadriver.app.api.data.ProfileMenu
import id.semisamadriver.app.api.util.SocketServices
import id.semisamadriver.app.base.BaseFragment
import id.semisamadriver.app.databinding.FragmentProfileBinding
import id.semisamadriver.app.ui.ViewModelFactoryProfile
import id.semisamadriver.app.ui.auth.ActivityLogin
import id.semisamadriver.app.ui.person.password.ActivityPassword
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.authTemps
import id.semisamadriver.app.utilily.Constant.baseUrlImageUser
import id.semisamadriver.app.utilily.Constant.getInitialName
import id.semisamadriver.app.utilily.Constant.getProfileMenu
import org.kodein.di.generic.instance

class FragmentProfile : BaseFragment(), ViewModelProfile.Bridge {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: ViewModelProfile
    private val factory: ViewModelFactoryProfile by instance()


    private lateinit var adapter: Adapter<ProfileMenu>

    override fun onStart() {
        super.onStart()
        viewModel.user.postValue(tempAuth?.user!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile,
            container,
            false
        )
        viewModel = ViewModelProvider(this, factory).get(ViewModelProfile::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        viewModel.user.postValue(tempAuth?.user!!)
        showMenu()
        adapter.data = getProfileMenu()
    }

    private fun showMenu() {
        adapter = Adapter(R.layout.item_menu_profile, mutableListOf(),
            { itemView, item ->
                val title = itemView.findViewById<TextView>(R.id.tvTitleMenu)
                val icon = itemView.findViewById<ImageView>(R.id.iconMenu)
                val required = itemView.findViewById<TextView>(R.id.tvRequiredAction)
                title.text = item.title
                icon.setImageResource(item.icon)
                if (!item.isRequired){
                    required.text = ""
                }
            },
            { _, _ ->
                context?.launchNewActivity(ActivityPassword::class.java)
            })
        binding.rvMenu.adapter = adapter
    }



    private fun initObserver() {
        viewModel.apply {
            user.observe(viewLifecycleOwner, {
                city.postValue(tempAddress)
                if (it.image.isNullOrEmpty()){
                    textInitial.postValue(getInitialName())
                }else {
                    textInitial.value = ""
                    binding.ivProfilePic.loadImageFromLink(baseUrlImageUser + it.image)
                }

            })
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) return true
        }
        return false
    }

    override fun logout() {
        cache.delete(authTemps)
        if (isMyServiceRunning(SocketServices::class.java)) {
            val intentService = Intent(requireActivity(), SocketServices::class.java)
            requireActivity().stopService(intentService)
        }
        context?.toast(getString(R.string.labelSuccessLogout))
        context?.launchNewActivity(ActivityLogin::class.java,
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    }

    override fun showLogoutDialog() {
        customDialog(requireActivity(), R.layout.dialog_logout
        ) { itemView, dialog  ->
            val tvYes = itemView.findViewById<TextView>(R.id.tvYes)
            val tvNo = itemView.findViewById<TextView>(R.id.tvNo)

            tvYes.setOnClickListener {
                viewModel.logoutAccount()
                dialog.dismiss()
            }

            tvNo.setOnClickListener {
                dialog.dismiss()
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