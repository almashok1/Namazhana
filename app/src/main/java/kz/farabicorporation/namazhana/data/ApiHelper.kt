package kz.farabicorporation.namazhana.data

import kz.farabicorporation.namazhana.data.api.CountryApi
import kz.farabicorporation.namazhana.data.api.FeedbackApi
import kz.farabicorporation.namazhana.data.api.PlaceApi

class ApiHelper(
    val placeApi: PlaceApi,
    val countryApi: CountryApi,
    val feedbackApi: FeedbackApi
)