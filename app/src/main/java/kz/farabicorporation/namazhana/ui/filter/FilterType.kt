package kz.farabicorporation.namazhana.ui.filter

import android.content.Context
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.data.models.PlaceType

sealed class FilterType : Parcelable {

    abstract val queryName: String

    abstract fun getValue(): Any?

    @Parcelize
    data class PlaceFilterType(
        val index: Int = 2,
        val type: PlaceType? = null
    ) : FilterType() {
        override val queryName: String get() = "type"
        override fun getValue() = type
    }

    @Parcelize
    data class DistanceType(
        val index: Int = 0,
        val distanceType: Distance = Distance.ALL,
        val nearDistanceKm: Int? = null
    ) : FilterType() {
        @Parcelize
        enum class Distance : Parcelable {
            ALL, NEAR, OTHER
        }

        override val queryName: String get() = "distance"
        override fun getValue() = nearDistanceKm?.times(1000)
    }

    @Parcelize
    data class WomenPlaceType(
        val hasWomenPlace: Boolean? = null
    ) : FilterType() {
        override val queryName: String get() = "womenSpace"

        override fun getValue() = hasWomenPlace.takeIf { it == true }
    }

    @Parcelize
    data class AblutionPlaceType(
        val hasAblutionPlace: Boolean? = null
    ) : FilterType() {
        override val queryName: String get() = "isAblutionPlace"
        override fun getValue() = hasAblutionPlace.takeIf { it == true }
    }

    @Parcelize
    data class IsJumaReadType(
        val isJumaRead: Boolean? = null
    ) : FilterType() {
        override val queryName: String get() = "isJumaRead"
        override fun getValue() = isJumaRead.takeIf { it == true }
    }
}

fun FilterType.DistanceType.getText(context: Context): String {
    return when(distanceType) {
        FilterType.DistanceType.Distance.ALL -> context.getString(R.string.filter_all)
        FilterType.DistanceType.Distance.NEAR -> context.getString(R.string.filter_near)
        FilterType.DistanceType.Distance.OTHER -> context.getString(R.string.filter_distancePlusKm, nearDistanceKm ?: 1)
    }
}

fun FilterType.PlaceFilterType.getText(context: Context): String {
    return context.getString(when(type) {
        PlaceType.MECHET -> R.string.common_mechet
        PlaceType.NAMAZHANA -> R.string.common_namazhana
        null -> R.string.filter_all
    })
}

val defaultFilterTypes get() = listOf(
    FilterType.PlaceFilterType(),
    FilterType.DistanceType(),
    FilterType.WomenPlaceType(),
    FilterType.AblutionPlaceType(),
    FilterType.IsJumaReadType()
)