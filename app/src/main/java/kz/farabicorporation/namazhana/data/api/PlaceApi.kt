package kz.farabicorporation.namazhana.data.api

import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.data.models.Coordinate
import kz.farabicorporation.namazhana.data.models.PlaceFull
import kz.farabicorporation.namazhana.data.models.PlaceMini
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface PlaceApi {

    @GET("city/{cityId}/places")
    suspend fun getAllPlacesByCityId(
        @Path("cityId") cityId: Int
    ) : Outcome<List<PlaceMini>>

    @JvmSuppressWildcards
    @POST("city/{cityId}/places")
    suspend fun getAllPlacesByUserCoordinate(
        @Path("cityId") cityId: Int,
        @Body coordinate: Coordinate,
        @QueryMap queryMap: Map<String, Any>
    ) : Outcome<List<PlaceMini>>

    @GET("place/{placeId}")
    suspend fun getPlaceDetails(
        @Path("placeId") placeId: Int,
    ) : Outcome<PlaceFull>
}
