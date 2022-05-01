package id.semisamadriver.app.ui.person.edit

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.dhaval2404.imagepicker.ImagePicker
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.DataAuth
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityEditBinding
import id.semisamadriver.app.ui.ViewModelFactoryEdit
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.authTemps
import id.semisamadriver.app.utilily.Constant.baseUrlImageUser
import id.semisamadriver.app.utilily.Constant.getInitialName
import org.kodein.di.generic.instance
import pub.devrel.easypermissions.EasyPermissions

class ActivityEdit : BaseActivity(), ViewModelEdit.Bridge,
    EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityEditBinding
    private lateinit var viewModel: ViewModelEdit
    private val factory: ViewModelFactoryEdit by instance()

    private val startForProfileImageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        val resultCode = result.resultCode
        val data = result.data

        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.data?.let {
                    setImageUri(it)
                } ?: run {
                    toast(getString(R.string.failedProcessingImage))
                }
            }
            ImagePicker.RESULT_ERROR -> {
                toast(ImagePicker.getError(data))
            }
        }
    }

    private fun setImageUri(imageUri: Uri?) {
        imageUri?.let { image ->
            val base64 = encodeUriToBase64(image)
            viewModel.uploadUserImage(base64)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        initView()

    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        viewModel = ViewModelProvider(this, factory)[ViewModelEdit::class.java]
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver() {
        val owner = this
        viewModel.apply {
            user.observe(owner) {
                val token = tempAuth?.tokens
                cache.set(authTemps, DataAuth(it, token))
                if (it.image == null) {
                    textInitial.postValue(getInitialName())
                } else {
                    textInitial.value = ""
                    binding.ivProfilePic.loadImageFromLink(baseUrlImageUser + tempAuth?.user?.image)
                }
            }
        }
        viewModel.name.observe(this) {
            viewModel.checkButton()
        }
        viewModel.email.observe(this) {
            viewModel.checkButton()
        }
        viewModel.phone.observe(this) {
            viewModel.checkButton()
        }
    }

    private fun initView(){
        if (tempAuth?.user?.image.isNullOrEmpty()){
            viewModel.textInitial.postValue(getInitialName())
        }else {
            viewModel. textInitial.value = ""
            binding.ivProfilePic.loadImageFromLink(baseUrlImageUser + tempAuth?.user?.image)
        }
    }

    override fun onClickedUpButton() {
        onBackPressed()
    }

    override fun changePicture() {
        ImagePickerDialog(this).apply {
            setListener(object: ImagePickerDialog.Listener {
                override fun selectedCaptureMode(selected: String) {
                    when (selected) {
                        ImagePickerDialog.SELECT_GALLERY -> {
                            ImagePicker.with(this@ActivityEdit)
                                .cropSquare()
                                .compress(1024)
                                .galleryOnly()
                                .createIntent { intent ->
                                    startForProfileImageResult.launch(intent)
                                }
                        }
                        ImagePickerDialog.SELECT_CAPTURE -> {
                            ImagePicker.with(this@ActivityEdit)
                                .cropSquare()
                                .compress(1024)
                                .cameraOnly()
                                .createIntent { intent ->
                                    startForProfileImageResult.launch(intent)
                                }
                        }
                    }
                }
            })
        }.show()
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }
}