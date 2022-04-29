package id.semisamadriver.app.api.manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.HandlerThread
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

class ManagerLocation(val context: Context) {

    companion object {
        const val TAG = "ManagerLocation"
    }

    fun getUpdatedLocation(block: (Location?) -> Unit) {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        val permission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            val client = LocationServices.getFusedLocationProviderClient(context)
            val locationRequest = LocationRequest.create()
            locationRequest.interval = 1000
            locationRequest.fastestInterval = 1000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val builder = LocationSettingsRequest.Builder()
            builder.addLocationRequest(locationRequest)
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    block.invoke(locationResult.lastLocation)
                    handlerThread.quit()
                    client.removeLocationUpdates(this)
                }
            }

            client.requestLocationUpdates(
                locationRequest,
                locationCallback,
                handlerThread.looper
            )
        } else {
            block.invoke(null)
        }
    }

}
