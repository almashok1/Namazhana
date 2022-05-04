package kz.farabicorporation.namazhana.arch.models

import kz.farabicorporation.namazhana.arch.api.SerializedName

data class ServerErrorBody(
    @SerializedName( "status")
    val status: Int? = null,
    @SerializedName("error")
    val error: String? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("timestamp")
    val timestamp: String? = null
)
