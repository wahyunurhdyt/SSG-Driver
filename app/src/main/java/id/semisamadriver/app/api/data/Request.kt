package id.semisamadriver.app.api.data

data class RequestLogin(
    var email: String?,
    var password: String?
)

data class RequestRefreshToken(
    var refreshToken: String?
)

data class RequestPassword(
    var password: String?
)

data class RequestChangePassword(
    var oldPassword: String?,
    var newPassword: String?
)

data class RequestPatch(
    var fullName: String?,
    var email: String?,
    var phoneNumber: String?,
    var image: String?
)

data class RequestUpdateDrivingMode(
    var isDrivingMode: Boolean
)

data class RequestSubscribeFcm(
    var token: String?,
    var device: String?
)

data class RequestUpdateRoute(
    var routes: MutableList<Route>?
)

data class RequestUpdateLocation(
    var location: UserLocation
)

data class RequestUpdateStock(
    var stockQuantity: Int?
)