package id.semisamadriver.app.utilily

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import id.semisamadriver.app.BuildConfig
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.*
import id.semisamadriver.app.base.Application

object Constant {

    //MAP
    const val MAP_ZOOM_DEFAULT = 16f
    const val MAP_ZOOM_FAR = 10f

    fun getIndonesiaLatLng() = LatLng(-6.200000, 106.816666)

    //Request
    const val REQUEST_CAPTURE_PHOTO = 99
    const val REQUEST_GALLERY_PHOTO = 100

    const val baseUrlImageUser = "${BuildConfig.BASE_URL_IMAGE}${BuildConfig.PATH_IMAGE_USER}"
    const val baseUrlImageProducts = "${BuildConfig.BASE_URL_IMAGE}${BuildConfig.PATH_IMAGE_PRODUCTS}"
    const val baseUrlImageCategories = "${BuildConfig.BASE_URL_IMAGE}${BuildConfig.PATH_IMAGE_CATEGORIES}"

    //Temporary Activity
    const val profile: String = "profile"
    const val home: String = "home"
    var currentActivity: String = home

    //Path
    const val path = "id.semisama.app"
    const val authTemps = "id.semisama.app.auth"
    const val firebaseTemps = "id.semisama.app.firebase"
    const val boardingTemps = "id.semisama.app.boarding"
    const val regionTemps = "id.semisama.app.region"

    //get initial name
    fun getInitialName(): String{
        val fullName = tempAuth?.user?.fullName?.split(" ")?.toTypedArray()
        return if (fullName?.size == 1){
            val firstInitial = fullName[0][0].uppercaseChar()
            firstInitial.toString()
        }else{
            val firstInitial = fullName!![0][0].uppercaseChar()
            val secondInitial = fullName[1][0].uppercaseChar()
            "$firstInitial$secondInitial"
        }
    }

    // Get Auth
    fun getAuth(auth: String): DataAuth =
        Gson().fromJson(auth, DataAuth::class.java)

    fun getRegion(region: String): Region =
        Gson().fromJson(region, Region::class.java)

    fun getBoardingPass(): MutableList<Boarding>{
        val list = mutableListOf<Boarding>()
        list.add(
            Boarding(
                Application.getStringArray(R.array.labelBoarding1)[0],
                Application.getStringArray(R.array.labelBoarding1)[1],
                R.drawable.boarding_image_1
            )
        )

        list.add(
            Boarding(
                Application.getStringArray(R.array.labelBoarding2)[0],
                Application.getStringArray(R.array.labelBoarding2)[1],
                R.drawable.boarding_image_2
            )
        )

        list.add(
            Boarding(
                Application.getStringArray(R.array.labelBoarding3)[0],
                Application.getStringArray(R.array.labelBoarding3)[1],
                R.drawable.boarding_image_3
            )
        )

        return list
    }

    fun getProfileMenu(): MutableList<ProfileMenu>{
        val list = mutableListOf<ProfileMenu>()
        list.add(
            ProfileMenu(
                Application.getStringArray(R.array.labelProfileMenu)[1],
                false,
                R.drawable.ic_password
            )
        )

        return list
    }
}