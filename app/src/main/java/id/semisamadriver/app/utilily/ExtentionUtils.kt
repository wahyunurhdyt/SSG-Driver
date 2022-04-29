package id.semisamadriver.app.utilily

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import id.semisamadriver.app.R
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.roundToInt

fun Context.launchNewActivity(cls: Class<out AppCompatActivity>){
    startActivity(Intent(this, cls))
}

fun Context.launchNewActivity(cls: Class<out AppCompatActivity>, flags: Int){
    Intent(this, cls).also {
        it.flags = flags
        startActivity(it)
    }
}


fun Context.getPx(dp: Int): Int {
    return (dp * this.resources.displayMetrics.density).roundToInt()
}

fun Context.toast(message: String?){
    Toast.makeText(this, message ?: "Text empty", Toast.LENGTH_SHORT).show()
}

fun Spannable.recolorAndBold(context: Context, color: Int){
    setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        0,
        length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun Spannable.recolor(context: Context, color: Int){
    setSpan(
        StyleSpan(Typeface.NORMAL),
        0,
        length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        0,
        length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
}

fun TextView.makeLinks(context: Context, vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                textPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
                textPaint.typeface = Typeface.DEFAULT_BOLD
                textPaint.isUnderlineText = false
            }
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance()
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun Context.isInternetAvailable(): Boolean {
    var result = false
    val connectivityManager = applicationContext.
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
    }
    return result
}

fun View.snackbar(message: String?){
    Snackbar.make(this, message ?: "Text empty", Snackbar.LENGTH_LONG).show()
}

fun View.snackbarLong(message: String?){
    Snackbar.make(this, message ?: "Text empty", Snackbar.LENGTH_INDEFINITE)
        .also { snackbar ->
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
        }.show()
}

fun ImageView.loadImageFromLink(link: String?) {
    val option = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .centerCrop()
        .placeholder(R.color.white_200)
        .error(R.color.black_70)

    try {
        Glide.with(context)
            .load(link)
            .apply(option)
            .into(this)
    } catch (e: IllegalArgumentException) {
        Log.e("Load File From URL", e.message ?: "")
    }
}


fun Any.decimalFormat(): String{
    val locale = Locale("id", "ID")
    val symbols = DecimalFormatSymbols(locale)
    symbols.currencySymbol = "Rp. "
    symbols.decimalSeparator = ','
    symbols.groupingSeparator = '.'
    val decimalFormat = DecimalFormat("##,###,###", symbols)
    decimalFormat.decimalFormatSymbols = symbols
    return when (this) {
        is Long -> decimalFormat.format(this)
        is Int -> decimalFormat.format(toLong())
        is Double -> decimalFormat.format(toLong())
        else -> "Wrong format"
    }
}

fun Any.formatToRupiah(): String {
    return "Rp. "+this.decimalFormat()
}

fun isValidPassword(password: String?): Boolean {
    val pattern: Pattern
    val patern = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{1,}$"
    pattern = Pattern.compile(patern)
    val matcher: Matcher = pattern.matcher(password)
    return matcher.matches()
}

fun Context.showSimpleDialogReturn(
    message: String?,
    title: String? = null
): MaterialAlertDialogBuilder {
    val builder = MaterialAlertDialogBuilder(this)
    if (title != null) {
        builder.setTitle(title)
    }
    builder.setMessage(message ?: getString(R.string.labelAlertDialogError))
    builder.setCancelable(false)
    return builder
}

fun Context.getAddress(lat: Double?, lng: Double?): Address? {
    val geoCoder = Geocoder(this, Locale.getDefault())
    return try {
        val addresses = geoCoder.getFromLocation(
            lat ?: 0.0, lng ?: 0.0, 1
        )
        addresses[0]
    } catch (e: java.lang.Exception) {
        Log.e("getAddress", "${e.message}")
        Log.e("getAddress", "INPUT ($lat, $lng)")
        null
    }
}

fun Context.createFile(): File? {
    try {
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"
        val storageDir =
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    } catch (ex: IOException) {
        toast(getString(R.string.labelFailedCreatingImage))
    }
    return null
}



fun isValidPhoneNumber(phone: String): Boolean {

    var newPhoneInternational = ""
    if (phone.length >= 5) {
        newPhoneInternational = "${phone[0]}${phone[1]}${phone[2]}${phone[3]}${phone[4]}"
    }

    val isPhoneInternation = newPhoneInternational == "62811" ||
            newPhoneInternational == "62812" || newPhoneInternational == "62813" ||
            newPhoneInternational == "62815" || newPhoneInternational == "62816" ||
            newPhoneInternational == "62817" || newPhoneInternational == "62818" ||
            newPhoneInternational == "62819" || newPhoneInternational == "62821" ||
            newPhoneInternational == "62822" || newPhoneInternational == "62823" ||
            newPhoneInternational == "62831" || newPhoneInternational == "62832" ||
            newPhoneInternational == "62833" || newPhoneInternational == "62836" ||
            newPhoneInternational == "62838" || newPhoneInternational == "62852" ||
            newPhoneInternational == "62853" || newPhoneInternational == "62855" ||
            newPhoneInternational == "62856" || newPhoneInternational == "62857" ||
            newPhoneInternational == "62858" || newPhoneInternational == "62877" ||
            newPhoneInternational == "62878" || newPhoneInternational == "62879" ||
            newPhoneInternational == "62881" || newPhoneInternational == "62882" ||
            newPhoneInternational == "62883" || newPhoneInternational == "62884" ||
            newPhoneInternational == "62885" || newPhoneInternational == "62886" ||
            newPhoneInternational == "62887" || newPhoneInternational == "62888" ||
            newPhoneInternational == "62889" || newPhoneInternational == "62894" ||
            newPhoneInternational == "62895" || newPhoneInternational == "62896" ||
            newPhoneInternational == "62897" || newPhoneInternational == "62898" ||
            newPhoneInternational == "62899"


    var newPhone = ""
    if (phone.length >= 4) {
        newPhone = "${phone[0]}${phone[1]}${phone[2]}${phone[3]}"
    }
    val isPhone = newPhone == "0811" ||
            newPhone == "0812" || newPhone == "0813" ||
            newPhone == "0815" || newPhone == "0816" ||
            newPhone == "0817" || newPhone == "0818" ||
            newPhone == "0819" || newPhone == "0821" ||
            newPhone == "0822" || newPhone == "0823" ||
            newPhone == "0831" || newPhone == "0832" ||
            newPhone == "0833" || newPhone == "0836" ||
            newPhone == "0838" || newPhone == "0852" ||
            newPhone == "0853" || newPhone == "0855" ||
            newPhone == "0856" || newPhone == "0857" ||
            newPhone == "0858" || newPhone == "0877" ||
            newPhone == "0878" || newPhone == "0879" ||
            newPhone == "0881" || newPhone == "0882" ||
            newPhone == "0883" || newPhone == "0884" ||
            newPhone == "0885" || newPhone == "0886" ||
            newPhone == "0887" || newPhone == "0888" ||
            newPhone == "0889" || newPhone == "0894" ||
            newPhone == "0895" || newPhone == "0896" ||
            newPhone == "0897" || newPhone == "0898" ||
            newPhone == "0899"

    return isPhoneInternation || isPhone
}