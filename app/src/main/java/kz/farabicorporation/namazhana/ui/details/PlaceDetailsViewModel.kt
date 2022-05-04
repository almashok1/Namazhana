package kz.farabicorporation.namazhana.ui.details

import kotlinx.coroutines.Dispatchers
import kz.farabicorporation.namazhana.arch.utils.doOnSuccess
import kz.farabicorporation.namazhana.arch.utils.map
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.extensions.getState
import kz.farabicorporation.namazhana.common.extensions.liveData
import kz.farabicorporation.namazhana.data.models.Feedback
import kz.farabicorporation.namazhana.data.models.PlaceFull
import kz.farabicorporation.namazhana.domain.PlaceInteractor

class PlaceDetailsViewModel(
    private val placeInteractor: PlaceInteractor
) : BaseViewModel() {

    private val _state = getState<PlaceFull>(Dispatchers.IO)
    val state get() = _state.liveData

    private val _feedbacksState = getState<Pair<List<Feedback>, PlaceFull>>(Dispatchers.IO)
    val feedbacksState get() = _feedbacksState.liveData

    fun loadDetails(placeId: Int) {
        _state.request {
            val places = placeInteractor.getPlaceDetails(placeId)
                .doOnSuccess {
                    if (it != null) loadFeedbacks(it)
                }
            places
        }
    }

    private suspend fun loadFeedbacks(placeFull: PlaceFull) {
        _feedbacksState.request {
            placeInteractor.getAllFeedbacks(placeFull.id).map {
                (it ?: listOf()) to placeFull
            }
        }
    }

    fun getCity(cityId: Int) = placeInteractor.getCity(cityId)

}