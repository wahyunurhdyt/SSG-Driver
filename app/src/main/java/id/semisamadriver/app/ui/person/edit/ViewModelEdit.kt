package id.semisamadriver.app.ui.person.edit

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.User
import id.semisamadriver.app.api.manager.ManagerRepository
import id.semisamadriver.app.api.util.ApiException
import id.semisamadriver.app.api.util.ConnectionException
import id.semisamadriver.app.base.Application
import id.semisamadriver.app.utilily.Coroutines
import id.semisamadriver.app.utilily.isValidPhoneNumber
import id.semisamadriver.app.utilily.tempAuth

class ViewModelEdit(
    private val managerRepository: ManagerRepository
): ViewModel() {
    private var bridge: Bridge? = null

    fun setBridge(bridge: Bridge){
        this.bridge = bridge
    }

    val onClickedUpButton = View.OnClickListener { bridge?.onClickedUpButton() }
    val title = MutableLiveData(Application.getString(R.string.labelTitleEdit))
    val textInitial = MutableLiveData("")

    val user = MutableLiveData<User>()
    val name = MutableLiveData(tempAuth?.user?.fullName)
    val nameError = MutableLiveData<String?>()
    val phone = MutableLiveData(tempAuth?.user?.phoneNumber)
    val phoneError = MutableLiveData<String?>()
    val email = MutableLiveData(tempAuth?.user?.email)
    val emailError = MutableLiveData<String?>()
    private val limitLength = MutableLiveData(10)
    val maxLength = MutableLiveData(14)


    private var isLoading = false
    val isButtonEnabled = MutableLiveData(false)
    val loadingVisibility = MutableLiveData(View.GONE)

    fun checkButton() {
        val nameOk = isNameOk()
        val phoneOk = isPhoneOk()
        val emailOk = isEmailOk()
        val isDataChanged = name.value != tempAuth?.user?.fullName || phone.value != tempAuth?.user?.phoneNumber || email.value != tempAuth?.user?.email
        val enabled = nameOk && phoneOk && emailOk && isDataChanged
        if (!isLoading && isButtonEnabled.value != enabled) isButtonEnabled.postValue(enabled)
    }

    private fun isNameOk(): Boolean {
        return if (name.value.isNullOrEmpty()) {
            val error = Application.getString(R.string.labelNameError)
            if (nameError.value != error) nameError.postValue(error)
            false
        } else {
            if (nameError.value != null) nameError.postValue(null)
            true
        }
    }

    private fun isPhoneOk(): Boolean {
        return if (!phone.value.isNullOrEmpty()) {
            if (!isValidPhoneNumber(phone.value!!)) {
                val error = Application.getStringArray(R.array.labelPhoneError)[0]
                var seconChar= "1"
                if (phone.value!!.length >= 2){
                    seconChar = phone.value!![1].toString()
                }
                val newPhoneInternational = "${phone.value!![0]}$seconChar"
                if (newPhoneInternational == "62"){
                    limitLength.postValue(11)
                    maxLength.postValue(15)
                    if (phone.value!!.length >= 5){
                        if (phoneError.value != error) phoneError.postValue(error)
                    }
                }else{
                    limitLength.postValue(10)
                    maxLength.postValue(14)
                    if (phone.value!!.length >= 4){
                        if (phoneError.value != error) phoneError.postValue(error)
                    }
                }
                false
            } else {
                if ((phone.value?.length ?: 0) < limitLength.value!! ) {
                    val error = Application.getStringArray(R.array.labelPhoneError)[1]
                    if (phoneError.value != error) phoneError.postValue(error)
                    false
                } else {
                    if (phoneError.value != null) phoneError.postValue(null)
                    true
                }
            }
        } else {
            if (phoneError.value != null) phoneError.postValue(null)
            false
        }
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

    @Suppress("unused")
    fun View.onClickedSave(){
        val fullName: String? = if (name.value != tempAuth?.user?.fullName){
            name.value
        }else{ null }

        val email: String? = if (email.value != tempAuth?.user?.email){
            email.value
        }else{ null }

        val phone: String? = if (phone.value != tempAuth?.user?.phoneNumber){
            phone.value
        }else{ null }

        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        isButtonEnabled.postValue(false)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.updateUser(fullName, email, phone)
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
                bridge?.showSnackbar(Application.getString(R.string.labelSuccessChangeEditData))
            } catch (e: ApiException) {
                isButtonEnabled.postValue(true)
                when {
                    e.message?.contains("email") == true -> {
                        emailError.postValue(e.message)
                    }
                    e.message?.contains("phone") == true -> {
                        phoneError.postValue(e.message)
                    }
                    else -> bridge?.showSnackbar(e.message)
                }
            } catch (e: ConnectionException) {
                isButtonEnabled.postValue(true)
                bridge?.showSnackbarLong(e.message)
            } finally {
                isLoading = false
                loadingVisibility.postValue(View.GONE)
            }
        }
    }

    val onClickChangePicture = View.OnClickListener{
        bridge?.changePicture()
    }

    fun patchImage(image: String){
        isLoading = true
        loadingVisibility.postValue(View.VISIBLE)
        Coroutines.main {
            try {
                managerRepository.repositoryPerson.patchImage(image)
                user.postValue(managerRepository.repositoryPerson.getUser()?.data)
                bridge?.showSnackbar(Application.getString(R.string.labelSuccessChangePicture))
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
        fun onClickedUpButton()
        fun changePicture()
        fun showSnackbar(message: String?)
        fun showSnackbarLong(message: String?)
    }
}