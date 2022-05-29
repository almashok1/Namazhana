package kz.farabicorporation.namazhana.data.models

import android.content.Context
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kz.farabicorporation.namazhana.arch.api.SerializedName
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.extensions.getColorCompat
import java.util.Date

class PlaceFull(
    val id: Int,
    val name: String,
    val address: String,
    val type: PlaceType,
    val womenSpace: WomenSpace,
    val isAblutionPlace: Boolean?,
    val isJumaRead: Boolean?,
    val hint: String?,
    val peopleFit: String?,
    val floor: String?,
    val cityId: Int,
    val coordinate: Coordinate,
    val previewImage: String?,
    val images: List<PlaceImage>?,
    val comments: List<Feedback>?,
    val counter: Int,
    val startWorkTime: String,
    val endWorkTime: String,
    val createdAt: Date,
    val updatedAt: Date
)

class PlaceImage(
    val id: Long,
    val placeId: Int,
    val small: String?,
    val original: String
)

@Entity
data class PlaceMini(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: PlaceType,
    val distance: Float?,
    val latitude: Double,
    val longitude: Double,
    val previewImage: String?,
    val startWorkTime: String,
    val endWorkTime: String,
    val womenSpace: WomenSpace,
    val isJumaRead: Boolean?,
    val isAblutionPlace: Boolean?
)

data class WomenSpace(
    val isExist: Boolean?,
    val description: String?
)

@Parcelize
enum class PlaceType : Parcelable {
    @SerializedName("MECHET")
    MECHET,

    @SerializedName("NAMAZHANA")
    NAMAZHANA
}

fun PlaceType.getColor(context: Context) = context.getColorCompat(
    when (this) {
        PlaceType.MECHET -> R.color.main_primary_one
        PlaceType.NAMAZHANA -> R.color.main_primary_two
    }
)

fun PlaceType.getText(context: Context) = context.getString(
    when (this) {
        PlaceType.MECHET -> R.string.common_mechet
        PlaceType.NAMAZHANA -> R.string.common_namazhana
    }
)

fun Pair<String, String>.toWorkTime(context: Context) =
    context.getString(R.string.common_workTime, first, second)