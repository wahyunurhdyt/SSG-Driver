package id.semisamadriver.app.api.data

import com.google.gson.annotations.SerializedName

//Auth
data class DataAuth(
    var user: User?,
    var tokens: Tokens?
)

data class DataResponseAuth(
    var tokens: Tokens?
)

data class VerificationEmail(
    var requestId: String?,
    var remainingRetry: Int?
)

//Location
class Location(
    var latitude: Double? = null,
    var longitude: Double? = null
)

data class Route(
    var location: UserLocation?,
    var name: String?
)


data class UserLocation(
    var type: String?,
    var coordinates: ArrayList<Double>?
)

data class User(
    var image: String?,
    var role: String?,
    var isVerifiedEmail: Boolean?,
    var isVerifiedPhoneNumber: Boolean?,
    var isSetPassword: Boolean?,
    var provider: String?,
    var fullName: String?,
    var email: String?,
    var phoneNumber: String?,
    var region: Region?,
    var isDrivingMode: Boolean?,
    var id: String?
)


data class Tokens(
    var access: Token?,
    var refresh: Token?,
    var type: String?
)

data class Token(
    var token: String?,
    var expires: Long?
)

//Region
class Region(
    var name: String? = null,
    var id: String? = null,
    var isSupported: Boolean? = false
)

//Product
class DataProduct(
    var results: MutableList<Product>,
    var page: Int,
    var limit: Int,
    var totalPages: Int,
    var totalResults: Int
)
