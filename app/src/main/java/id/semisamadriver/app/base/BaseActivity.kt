package id.semisamadriver.app.base

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import id.semisamadriver.app.R
import id.semisamadriver.app.api.repository.RepositoryLocation
import id.semisamadriver.app.utilily.Cache
import id.semisamadriver.app.utilily.showSimpleDialogReturn
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import java.io.ByteArrayOutputStream

abstract class BaseActivity: AppCompatActivity(), KodeinAware,
    EasyPermissions.PermissionCallbacks {
    override val kodein by kodein()
    val cache = Cache()

    private val repositoryLocation: RepositoryLocation by instance()

    companion object {
        const val REQUEST_PERMISSIONS_LOCATION = 345
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.cache.start(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    protected fun fetchLocation(){
        repositoryLocation.fetchLocation()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_PERMISSIONS_LOCATION) {
            showSimpleDialogReturn(
                getString(R.string.labelLocationPermissionCaption),
                getString(R.string.labelLocationPermission)
            ).apply {
                setPositiveButton(context.getString(R.string.labelTurnOn)) { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", "id.semisamadriver.app", null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                setNegativeButton(context.getString(R.string.labelCancel)) { _, _ ->
                    finish()
                }
                show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_PERMISSIONS_LOCATION) {
            haveLocationPermissions()
        }
    }

    protected fun haveLocationPermissions(): Boolean {
        val perms = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        return if (EasyPermissions.hasPermissions(this, *perms)) {
            true
        } else {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(this, REQUEST_PERMISSIONS_LOCATION, *perms)
                    .setRationale(R.string.labelLocationPermission)
                    .setPositiveButtonText(R.string.labelTurnOn)
                    .setNegativeButtonText(R.string.labelCancel)
                    .setTheme(R.style.MaterialAlertDialogTheme)
                    .build()
            )
            false
        }
    }

    fun encodeUriToBase64(uri: Uri): String? {
        val imageStream = contentResolver.openInputStream(uri)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        val b: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

    fun encodePathToBase64(path: String): String{
        val bitmap = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.NO_WRAP)
    }

}