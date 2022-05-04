package kz.farabicorporation.namazhana.arch.models

import kz.farabicorporation.namazhana.arch.api.SerializedName

data class RefreshTokenResponse(
    @SerializedName("access_token")
    val accessToken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("status")
    val status: String?
)
