package id.semisamadriver.app.api.repository

import android.content.Context
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.api.manager.ManagerLocation
import id.semisamadriver.app.api.util.SafeApiRequest
import id.semisamadriver.app.utilily.*

class RepositoryLocation(
    context: Context,
    private val managerLocation: ManagerLocation
):  SafeApiRequest(context){

    fun fetchLocation() {
        managerLocation.getUpdatedLocation{ location -> onLocationReceived(location)}
    }

    private fun onLocationReceived(location: android.location.Location?) = Coroutines.io {
        location?.let { newLocation ->
            saveLocation(
                Location(
                    newLocation.latitude,
                    newLocation.longitude
                )
            )
        }
    }

    private fun saveLocation(location: Location) {

    }
}