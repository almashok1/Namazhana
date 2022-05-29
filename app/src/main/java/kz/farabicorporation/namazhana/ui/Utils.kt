package kz.farabicorporation.namazhana.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.extensions.loadUrl
import kz.farabicorporation.namazhana.common.extensions.showInfoDialog
import kz.farabicorporation.namazhana.data.models.PlaceType

fun Float.getDistance(context: Context) = when {
    this >= 1000 -> context.getString(
        R.string.common_distanceKM,
        String.format("%.2f", this / 1000.0f)
    )
    else -> context.getString(R.string.common_distanceM, this)
}

fun isLocationPermissionGranted(context: Context) =
    checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
            checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

fun checkPermission(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.processLocationPermissionDecline(launcher: ActivityResultLauncher<Intent>) {
    val isDoNotAskFlagSet =
        !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                || !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
    if (isDoNotAskFlagSet) {
        showInfoDialog(
            message = getString(R.string.common_permission_location),
            positiveButtonText = getString(R.string.common_settings),
            onPositiveClick = { dialog, i ->
                openAppPermissionSettings(launcher)
                dialog.dismiss()
            }
        )
    }
}

private fun Context.openAppPermissionSettings(launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    launcher.launch(intent)
}

fun Boolean.getYesNoText(context: Context) =
    context.getString(if (this) R.string.common_yes else R.string.common_no)

fun ImageView.loadWithPlaceType(url: String?, type: PlaceType) {
    loadUrl(
        url, fallback = when (type) {
            PlaceType.MECHET -> R.drawable.no_image_mechet
            PlaceType.NAMAZHANA -> R.drawable.no_image_namazhana
        }
    )
}

fun Context.checkLocationEnabled() {
    val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isGpsEnabled = false
    var isNetworkEnabled = false
    try {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    try {
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    if (!isGpsEnabled && !isNetworkEnabled) {
        showInfoDialog(
            "Включите геолокация для работы программы в настройках",
            "Настройки"
        ) { dialog, _ ->
            startActivity(Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS))
            dialog.dismiss()
        }
    }
}