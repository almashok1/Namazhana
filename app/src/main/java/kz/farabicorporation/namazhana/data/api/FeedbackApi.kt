package kz.farabicorporation.namazhana.data.api

import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.data.models.Feedback
import retrofit2.http.GET
import retrofit2.http.Path

interface FeedbackApi {

    @GET("place/{placeId}/feedbacks")
    suspend fun getAllFeedbacks(
        @Path("placeId") placeId: Int
    ) : Outcome<List<Feedback>>

}