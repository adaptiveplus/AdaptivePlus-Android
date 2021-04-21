package plus.adaptive.sdk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.provider.Settings
import android.telephony.TelephonyManager
import plus.adaptive.sdk.data.models.APUser


@SuppressLint("HardwareIds")
internal fun getDeviceId(context: Context) : String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}

internal fun getDeviceType(context: Context) : APUser.Device.Type {
    val uiMode = context.resources.configuration.uiMode
    val screenLayout = context.resources.configuration.screenLayout

    return when {
        (uiMode and Configuration.UI_MODE_TYPE_MASK) == Configuration.UI_MODE_TYPE_TELEVISION ->
            APUser.Device.Type.TV
        (screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE ->
            APUser.Device.Type.TABLET
        else -> APUser.Device.Type.PHONE
    }
}

internal fun getAppVersion(context: Context) : String? {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        null
    }
}

internal fun getMobileOperatorName(context: Context) : String? {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    return telephonyManager?.networkOperatorName
}

internal fun getMobileCountryCode(context: Context) : String? {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val networkOperator = telephonyManager?.networkOperator

    return when {
        networkOperator != null && networkOperator.length >= 3 -> networkOperator.substring(0, 3)
        else -> null
    }
}

internal fun getMobileNetworkCode(context: Context) : String? {
    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    val networkOperator = telephonyManager?.networkOperator

    return when {
        networkOperator != null && networkOperator.length >= 3 -> networkOperator.substring(3)
        else -> null
    }
}