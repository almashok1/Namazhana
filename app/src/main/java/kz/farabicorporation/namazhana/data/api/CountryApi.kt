package kz.farabicorporation.namazhana.data.api

import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.data.models.City
import kz.farabicorporation.namazhana.data.models.Coordinate
import kz.farabicorporation.namazhana.data.models.Country
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CountryApi {

    @GET("country/{countryId}/cities")
    suspend fun getAllCitiesByCountryId(
        @Path("countryId") countryId: Int
    ) : Outcome<List<City>>

    @POST("city")
    suspend fun getAllCitiesByUserCoordinate(
        @Body coordinate: Coordinate
    ) : Outcome<List<City>>

    @GET("countries")
    suspend fun getAllCountries() : Outcome<List<Country>>

}