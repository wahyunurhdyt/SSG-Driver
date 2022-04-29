package id.semisamadriver.app.base


abstract class BaseResponse(
    var success: Boolean? = false,
    var code: Int? = null,
    var message: String? = null,
) {

    companion object {
        val CODE_OK = 200 until 400

        const val CODE_AUTH_TOKEN_INVALID = 401
        const val CODE_FAILED_RESET_PASSWORD = 722
        const val CODE_FAILED_TOO_MANY_REQUEST = 422
        const val CODE_FAILED_TOO_MANY_REQUEST_FORGOT_PASSWORD = 429
        const val CODE_FAILED_TOO_MANY_REQUEST_VERIFICATION_EMAIL = 726
        const val CODE_INVALID_REFRESH_TOKEN = 734
        const val CODE_PASSWORD_CANT_BE_SAME = 735
        const val CODE_OLD_PASSWORD_NOT_MATCH = 736
        const val CODE_LOCATION_NOT_SUPPORTED = 740
    }

    fun isSuccess(): Boolean {
        return success == true || code in CODE_OK
    }

}