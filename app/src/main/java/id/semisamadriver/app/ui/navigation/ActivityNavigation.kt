package id.semisamadriver.app.ui.navigation

import android.os.Bundle
import android.os.Handler
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import id.semisamadriver.app.R
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.utilily.toast
import kotlinx.android.synthetic.main.activity_navigation.*

class ActivityNavigation : BaseActivity() {

    private lateinit var navController: NavController
    private var twice = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        initBottomNavigation()
        fetchLocation()
    }

    private fun initBottomNavigation() {
        navController = Navigation.findNavController(this, R.id.fMain)
        bnvMain.setupWithNavController(navController)
        bnvMain.setOnNavigationItemReselectedListener {}

    }

    override fun onBackPressed() {
        when (fMain.childFragmentManager.backStackEntryCount) {
            0 -> {
                if (twice) {
                    super.onBackPressed()
                    return
                }
                twice = true
                toast(resources.getString(R.string.labelPressOneMore))
                Handler().postDelayed({ twice = false }, 2000)
            }
            else -> super.onBackPressed()
        }
    }
}