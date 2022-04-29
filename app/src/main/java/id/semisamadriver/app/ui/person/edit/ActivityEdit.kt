package id.semisamadriver.app.ui.person.edit
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.DataAuth
import id.semisamadriver.app.base.BaseActivity
import id.semisamadriver.app.databinding.ActivityEditBinding
import id.semisamadriver.app.ui.ViewModelFactoryEdit
import id.semisamadriver.app.utilily.*
import id.semisamadriver.app.utilily.Constant.REQUEST_CAPTURE_PHOTO
import id.semisamadriver.app.utilily.Constant.REQUEST_GALLERY_PHOTO
import id.semisamadriver.app.utilily.Constant.authTemps
import id.semisamadriver.app.utilily.Constant.baseUrlImageUser
import id.semisamadriver.app.utilily.Constant.getInitialName
import org.kodein.di.generic.instance
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException

class ActivityEdit : BaseActivity(), ViewModelEdit.Bridge,
    EasyPermissions.PermissionCallbacks {

    private lateinit var binding: ActivityEditBinding
    private lateinit var viewModel: ViewModelEdit
    private val factory: ViewModelFactoryEdit by instance()

    private var lastRequestCapture = 0
    private var lastRequestGalery = 0
    private var requestId = 0
    private var path = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initObserver()
        initView()

    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)
        viewModel = ViewModelProvider(this, factory).get(ViewModelEdit::class.java)
        viewModel.setBridge(this)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
    }

    private fun initObserver(){
        val owner = this
        viewModel.apply {
            user.observe(owner, {
                val token = tempAuth?.tokens
                cache.set(authTemps, DataAuth(it, token))
                if (it.image == null){
                    textInitial.postValue(getInitialName())
                }else {
                    textInitial.value = ""
                    binding.ivProfilePic.loadImageFromLink(baseUrlImageUser + tempAuth?.user?.image)
                }
            })
        }
        viewModel.name.observe(this, {
            viewModel.checkButton()
        })
        viewModel.email.observe(this, {
            viewModel.checkButton()
        })
        viewModel.phone.observe(this, {
            viewModel.checkButton()
        })
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
        showBottomSheetDialog(this, R.layout.sheet_pickimage){ dialog ->
            val openGallery = dialog.findViewById<TextView>(R.id.openGallery)
            val openCamera = dialog.findViewById<TextView>(R.id.openCamera)

            openGallery?.setOnClickListener {
                dialog.dismiss()
                lastRequestGalery = REQUEST_GALLERY_PHOTO
                openGalleryCamera(REQUEST_GALLERY_PHOTO)
            }

            openCamera?.setOnClickListener {
                dialog.dismiss()
                lastRequestCapture = REQUEST_CAPTURE_PHOTO
                path = openIntentCamera(REQUEST_CAPTURE_PHOTO)
            }

        }
    }

    override fun showSnackbar(message: String?) {
        binding.container.snackbar(message)
    }

    override fun showSnackbarLong(message: String?) {
        binding.container.snackbarLong(message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE_PHOTO && resultCode == Activity.RESULT_OK) {
            toast("${getString(R.string.labelPictureHasTaken)} : $path")
            try {
                viewModel.patchImage(encodePathToBase64(path))
            }catch (e: IOException){
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == Activity.RESULT_OK) {
            val dataImage = data!!.data
            try {
                viewModel.patchImage(encodeUriToBase64(dataImage!!)!!)
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) { }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestId == REQUEST_CAPTURE_PHOTO) path = openIntentCamera(lastRequestCapture)
        else openGalleryCamera(lastRequestGalery)
    }
}