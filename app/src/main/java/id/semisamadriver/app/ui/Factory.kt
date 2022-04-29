package id.semisamadriver.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.ui.auth.ViewModelLogin
import id.semisamadriver.app.ui.navigation.home.ViewModelHome
import id.semisamadriver.app.ui.navigation.profile.ViewModelProfile
import id.semisamadriver.app.ui.person.edit.ViewModelEdit
import id.semisamadriver.app.ui.person.password.ViewModelPassword
import id.semisamadriver.app.ui.product.category.ViewModelCategory
import id.semisamadriver.app.ui.product.detail.ViewModelDetail
import id.semisamadriver.app.ui.product.productssg.ViewModelProduct
import id.semisamadriver.app.ui.product.search.ViewModelSearch
import id.semisamadriver.app.ui.route.addpoint.ViewModelAddPoint
import id.semisamadriver.app.ui.route.manage.ViewModelManageRoute
import id.semisamadriver.app.ui.route.allroute.ViewModelRoute

data class ViewModelFactoryHome(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelHome(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryCategory(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCategory(
            managerRepository
        ) as T
    }
}

data class ViewModelFactorySearch(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelSearch(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryProduct(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProduct(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryDetail(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelDetail(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryLogin(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelLogin(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryProfile(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelProfile(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryPassword(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelPassword(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryEdit(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelEdit(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryAddPoint(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelAddPoint(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryUpdateRoute(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelRoute(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryManageRoute(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelManageRoute(
            managerRepository
        ) as T
    }
}

data class ViewModelFactoryRoute(
    private val managerRepository: ManagerRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelRoute(
            managerRepository
        ) as T
    }
}