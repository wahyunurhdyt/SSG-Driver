package id.semisamadriver.app.base

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.Fragment
import id.semisamadriver.app.R
import id.semisamadriver.app.api.repository.RepositoryLocation
import id.semisamadriver.app.utilily.Cache
import id.semisamadriver.app.utilily.showSimpleDialogReturn
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

@Suppress("DEPRECATION")
abstract class BaseFragment:
    Fragment(),
    KodeinAware,
    EasyPermissions.PermissionCallbacks
{
    companion object {
        const val REQUEST_PERMISSIONS_LOCATION = 345
    }

    override val kodein by kodein()
    val cache = Cache()

    private val repositoryLocation: RepositoryLocation by instance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.cache.start(requireContext())
    }

    protected fun fetchLocation(){
        repositoryLocation.fetchLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this
        )
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_PERMISSIONS_LOCATION) {
            requireContext().showSimpleDialogReturn(
                getString(R.string.labelLocationPermissionCaption),
                getString(R.string.labelLocationPermission)
            ).apply {
                setPositiveButton(context.getString(R.string.labelTurnOn)) { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", "id.semisama.app", null)
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                setNegativeButton(context.getString(R.string.labelCancel)) { _, _ ->
                    requireActivity().finish()
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

        return if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
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
}