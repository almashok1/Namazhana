package kz.farabicorporation.namazhana.common

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri

class NavigationApps(base: Context) : ContextWrapper(base) {
    private var mIntent: Intent? = null

    enum class Apps {
        TWOGIS, YANDEX, GOOGLE
    }

    /**
     * Set geo coordinates point of destination point
     * @param flag select one of enumerated apps
     * @param points array of coordinates like (lat, lon, lat, lon)
     */
    fun setDestination(flag: Apps?, vararg points: LatLng): NavigationApps {
        mIntent = Intent()
        when (flag) {
            Apps.GOOGLE -> {
                if (points.size == 1) {
                    mIntent = GOOGLE.setData(
                        Uri.parse(
                            String.format(
                                GOOGLE_NAVIGATION,
                                points[0].latitude,
                                points[0].longitude
                            )
                        )
                    )
                } else {
                    val google = StringBuilder()
                    for ((i, point) in points.withIndex()) {
                        when (i) {
                            0 -> google.append("&daddr=").append(point.toString())
                            else -> google.append("+to:").append(point.toString())
                        }
                    }
                    mIntent = GOOGLE.setData(
                        Uri.parse(
                            String.format(
                                GOOGLE_MAP_NAVIGATION,
                                google.toString()
                            )
                        )
                    )
                }
            }
            Apps.YANDEX -> {
                mIntent = YANDEX
                mIntent!!.putExtra("lat_to", points[0].latitude)
                mIntent!!.putExtra("lon_to", points[0].longitude)
            }
            Apps.TWOGIS -> {
                if (points.size == 1) {
                    mIntent = TWOGIS.setData(
                        Uri.parse(
                            String.format(
                                TWOGIS_NAVIGATION,
                                points[0].longitude,
                                points[0].latitude
                            )
                        )
                    )
                } else {
                    val tgis = StringBuilder()
                    var j = 0
                    for (point in points) {
                        if (j == 0) tgis.append(
                            String.format(
                                "from/%s,%s",
                                point.longitude,
                                point.latitude
                            )
                        ) else {
                            if (j > 1) break //only for 2 points
                            tgis.append(String.format("/to/%s,%s", point.longitude, point.latitude))
                        }
                        j++
                    }
                    mIntent =
                        TWOGIS.setData(
                            Uri.parse(
                                String.format(
                                    TWOGIS_MAP_NAVIGATION,
                                    tgis.toString()
                                )
                            )
                        )
                }
            }
        }
        return this
    }

    fun getIntentFromFlag(flag: Apps) = when (flag) {
        Apps.GOOGLE -> GOOGLE
        Apps.YANDEX -> YANDEX
        Apps.TWOGIS -> TWOGIS
    }

    /**
     * Check selected navigation application is installed or not
     * @param flag select one of enumerated apps
     */
    @Throws(ActivityNotFoundException::class)
    fun checkPackage(flag: Apps?) {
        val intent = flag?.let(::getIntentFromFlag)
        if (!isPackageInstalled(intent)) {
            openMarket(intent)
        }
    }

    /**
     * Show the selected navigation application and build a route
     * @param install if navigation application not installed open GooglePlay on instalation page
     */
    @Throws(ActivityNotFoundException::class)
    fun guideMe(install: Boolean) {
        if (isPackageInstalled(mIntent)) {
            startActivity(
                mIntent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        } else {
            if (install) {
                openMarket(mIntent)
            } else {
                throw ActivityNotFoundException()
            }
        }
    }

    private fun isPackageInstalled(intent: Intent?): Boolean {
        if (intent == null) return false
        val pm = packageManager
        try {
            val packageName = intent.getPackage() ?: return false
            val info: PackageInfo = pm.getPackageInfo(
                packageName, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }

    private fun openMarket(intent: Intent?) {
        startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse(String.format(MARKET_LINK, intent!!.getPackage())))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    class LatLng(var1: Double, var3: Double) {
        var latitude: Double
        var longitude = 0.0
        override fun hashCode(): Int {
            var var2 = java.lang.Double.doubleToLongBits(latitude)
            val var1 = 31 + (var2 xor var2 ushr 32).toInt()
            var2 = java.lang.Double.doubleToLongBits(longitude)
            return var1 * 31 + (var2 xor var2 ushr 32).toInt()
        }

        override fun equals(var1: Any?): Boolean {
            return when {
                this === var1 -> {
                    true
                }
                var1 !is LatLng -> {
                    false
                }
                else -> {
                    val var2 = var1
                    java.lang.Double.doubleToLongBits(latitude) == java.lang.Double.doubleToLongBits(
                        var2.latitude
                    ) && java.lang.Double.doubleToLongBits(
                        longitude
                    ) == java.lang.Double.doubleToLongBits(var2.longitude)
                }
            }
        }

        override fun toString(): String {
            val var1 = latitude
            val var3 = longitude
            return String.format("%s, %s", var1, var3)
        }

        init {
            longitude = if (-180.0 <= var3 && var3 < 180.0) {
                var3
            } else {
                ((var3 - 180.0) % 360.0 + 360.0) % 360.0 - 180.0
            }
            latitude = Math.max(-90.0, Math.min(90.0, var1))
        }
    }

    fun checkAll(latLng: LatLng): NavigationApps {
        Apps.values().forEach {
            val intent = getIntentFromFlag(it)
            if (isPackageInstalled(intent)) return setDestination(it, latLng)
        }
        return setDestination(Apps.TWOGIS, latLng)
    }

    companion object {
        private val GOOGLE = Intent(Intent.ACTION_VIEW)
            .setPackage("com.google.android.apps.maps")
        private val YANDEX = Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP")
            .setPackage("ru.yandex.yandexnavi")
        private val TWOGIS = Intent(Intent.ACTION_VIEW)
            .setPackage("ru.dublgis.dgismobile")

        //App link to Google Play Market
        private const val MARKET_LINK = "market://details?id=%s"

        //Google api for delivery to navigation app next point coordinates
        private const val GOOGLE_NAVIGATION = "google.navigation:ll=%s,%s"
        private const val GOOGLE_MAP_NAVIGATION =
            "http://maps.google.com/maps?%s" //saddr=%1$s&daddr=%2$s+to:%3$s

        //Waze api for delivery to weze next point coordinates
        private const val WAZE_NAVIGATION = "waze://?ll=%s,%s&navigate=yes"
        private const val CITYGUIDE_NAVIGATION = "cgcmd delroute setroute %s"

        //2GIS api for delivery to 2gis navigator next point coordinates in custom format <type>/from/<lon>,<lat>/to/<lon>,<lat>
        // <type> - vehicle type (car (car route), ctx (public transport), pedestrian (hiking route), taxi) default is "car"
        // <lon>,<lat> - coordinates of the beginning and end of the route.
        private const val TWOGIS_NAVIGATION = "dgis://2gis.ru/routeSearch/rsType/car/to/%s,%s"
        private const val TWOGIS_MAP_NAVIGATION = "dgis://2gis.ru/routeSearch/rsType/car/%s"
    }
}