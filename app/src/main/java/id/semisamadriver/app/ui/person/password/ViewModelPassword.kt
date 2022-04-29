package id.semisamadriver.app.ui.person.password

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.base.Application.Companion.getStringArray
import id.semisamadriver.app.base.BaseResponse.Companion.CODE_OLD_PASSWORD_NOT_MATCH
import id.semisamadriver.app.base.BaseResponse.Companion.CODE_PASSWORD_CANT_BE_SAME
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.isValidPassword
import id.semisamadriver.app.utilily.toast

class ViewModelPassword(
    private val managerRepository: ManagerRepository
): ViewModel() {
    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge){
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }

    val title = MutableLiveData(getStringArray(R.array.labelPassword)[1])

    val desc = MutableLiveData(getStringArray(R.array.labelDescPassword)[1])

    val oldPassword = MutableLiveData<String>()
    val oldPasswordError = MutableLiveData<String?>()
    val password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()
    val passwordConfirmation = MutableLiveData<String>()
    val passwordConfirmationError = MutableLiveData<String?>()

    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val oldPasswordOk = isOldPasswordOk()
        val passwordOk = isPasswordOk()
        val passwordConfirmationOk = isPasswordConfirmation()
        val enabled = oldPasswordOk && passwordOk && passwordConfirmationOk
        if (!isLoading && isButtonEnabled.value != enabled) isButtonEnabled.postValue(enabled)
    }

    private fun isOldPasswordOk(): Boolean {
        return if (!oldPassword.value.isNullOrEmpty()) {
            if ((oldPassword.value?.length ?: 0) < 8) {
                val error = getStringArray(R.array.labelPasswordError)[1]
                if (oldPasswordError.value != error) oldPasswordError.postValue(error)
                false
            } else {
                if (oldPasswordError.value != null) oldPasswordError.postValue(null)
                true
            }
        } else {
            if (oldPasswordError.value != null) oldPasswordError.postValue(null)
            false
        }
    }

    private fun isPasswordOk(): Boolean {
        return if (!password.value.isNullOrEmpty()) {
            if (!isValidPassword(password.value)) {
                val error = getStringArray(R.array.labelPasswordError)[0]
                if (passwordError.value != error) passwordError.postValue(error)
                false
            } else {
                if ((password.value?.length ?: 0) < 8) {
                    val error = getStringArray(R.array.labelPasswordError)[1]
                    if (passwordError.value != error) passwordError.postValue(error)
                    false
                } else {
                    if (passwordError.value != null) passwordError.postValue(null)
                    true
                }
            }
        } else {
            if (passwordError.value != null) passwordError.postValue(null)
            false
        }
    }

    private fun isPasswordConfirmation(): Boolean {
        return if (!passwordConfirmation.value.isNullOrEmpty()) {
            when {
                password.value != passwordConfirmation.value -> {
                    val error = getStringArray(R.array.labelPasswordError)[2]
                    if (passwordConfirmationError.value != error) passwordConfirmationError.postValue(error)
                    false
                }
                else -> {
                    if (passwordConfirmationError.value != null) passwordConfirmationError.postValue(null)
                    true
                }
            }
        } else {
            if (passwordConfirmationError.value != null) passwordConfirmationError.postValue(null)
            false
        }
    }

    @Suppress("unused")
    fun View.onClickedConfirm(){
        editPassword()
    }



    private fun editPassword(){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.changePassword(oldPassword.value!!, password.value!!)
//                user.postValue(null)
                Application.getContext().toast(Application.getString(R.string.labelSuccessEditPassword))
                bridge?.onClickedUpButton()
            } catch (e: ApiException) {
                when (e.code) {
                    CODE_OLD_PASSWORD_NOT_MATCH -> {
                        oldPasswordError.postValue(e.message)
                    }
                    CODE_PASSWORD_CANT_BE_SAME -> {
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

    interface Bridge {
        fun onClickedUpButton()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}