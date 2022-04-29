package id.semisamadriver.app.ui.auth

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.DataAuth
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.tempAddress
import id.semisamadriver.app.utilily.tempAuth

class ViewModelLogin(
    private val managerRepository: ManagerRepository
): ViewModel() {


    private var bridge: Bridge? = null

    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()

    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }


    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val emailOk = isEmailOk()
        val passwordOk = isPasswordOk()
        val enabled = emailOk && passwordOk
        if (!isLoading && isButtonEnabled.value != enabled) isButtonEnabled.postValue(enabled)
    }

    private fun isEmailOk(): Boolean {
        return if (!email.value.isNullOrEmpty()) {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.value ?: "").matches()) {
                if (emailError.value != null) emailError.postValue(null)
                true
            } else {
                val error = Application.getString(R.string.labelEmailError)
                if (emailError.value != error) emailError.postValue(error)
                false
            }
        } else {
            if (emailError.value != null) emailError.postValue(null)
            false
        }
    }

    private fun isPasswordOk(): Boolean {
        return if (!password.value.isNullOrEmpty()) {
            if ((password.value?.length ?: 0) < 8) {
                val error = Application.getStringArray(R.array.labelPasswordError)[1]
                if (passwordError.value != error) passwordError.postValue(error)
                false
            } else {
                if (passwordError.value != null) passwordError.postValue(null)
                true
            }
        } else {
            if (passwordError.value != null) passwordError.postValue(null)
            false
        }
    }

    @Suppress("unused")
    fun View.onClickedLogin() {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                val response =  managerRepository.repositoryAuth.postLogin(email.value, password.value)
                tempAuth = DataAuth(null, response?.data?.tokens)
                val user = managerRepository.repositoryPerson.getUser()
                bridge?.next(DataAuth(user?.data, response?.data?.tokens))
            } catch (e: ApiException) {
                when {
                    e.message?.contains("email") == true -> {
                        passwordError.postValue(e.message)
                    }
                    else -> bridge?.showSnackbar(e.message)
                }
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
                isButtonEnabled.postValue(true)
            }
        }
    }


//    fun requestFcmToken(isRetry: Boolean = false) {
//        if (isRetry) ManagerFirebase.deleteToken()
//        try {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener {
//                if(it.isSuccessful) { processToken(it.result.toString()) }
//            }
//        } catch (e: Exception) {
//            Log.e("requestFcmToken", "$e")
//            if (!isRetry) requestFcmToken(true)
//        }
//    }

    private fun processToken(token: String) {
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.subscribeFcm(token)
                //bridge?.next()
            } catch (e: ApiException) {
                bridge?.showSnackbar(e.message)
            } catch (e: ConnectionException) {
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
            }
        }
    }


    interface Bridge {
        fun next(data: DataAuth)
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}