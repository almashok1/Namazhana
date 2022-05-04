package kz.farabicorporation.namazhana.ui.list

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kz.farabicorporation.namazhana.arch.utils.doOnError
import kz.farabicorporation.namazhana.arch.utils.doOnSuccess
import kz.farabicorporation.namazhana.arch.utils.map
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.extensions.getState
import kz.farabicorporation.namazhana.common.extensions.liveData
import kz.farabicorporation.namazhana.common.extensions.setToCurrentSavedState
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.domain.PlaceInteractor
import kz.farabicorporation.namazhana.ui.filter.FilterFragment
import kz.farabicorporation.namazhana.ui.filter.FilterType
import kz.farabicorporation.namazhana.ui.filter.defaultFilterTypes

class PlaceListViewModel(
    private val cityId: Int,
    private val placeInteractor: PlaceInteractor
) : BaseViewModel() {

    private val mutex = Mutex()

    private val _places = getState<List<PlaceMini>>(Dispatchers.IO, debounce = 600L)
    val places = _places.liveData

    private var localFilters = defaultFilterTypes
    private var lastSearch: String? = null

    val chipDistanceTypes get() = listOf(
        FilterType.DistanceType(0, FilterType.DistanceType.Distance.ALL),
        FilterType.DistanceType(1, FilterType.DistanceType.Distance.NEAR, 2)
    )

    fun getPlaces(
        filters: List<FilterType>,
        onError: (() -> Unit)? = null
    ) {
        _places.request {
            placeInteractor.getAllPlacesByCityId(
                cityId, placeInteractor.getLatestCoordinate(), filters
            ).map {
                it?.applySearch(lastSearch)
            }.doOnSuccess {
                mutex.withLock {
                    localFilters = filters
                }
            }.doOnError {
                onError?.invoke()
            }
        }
    }

    private fun List<PlaceMini>.applySearch(text: String?): List<PlaceMini> {
        if (text.isNullOrEmpty()) return this
        return filter {
            it.name.contains(text, ignoreCase = true)
        }
    }

    fun selectDistanceType(type: FilterType.DistanceType) = withFragment {
        it.setToCurrentSavedState(FilterFragment.KEY_OBSERVE, localFilters.map { filterType ->
            if (filterType is FilterType.DistanceType) type else filterType
        })
    }

    fun search(text: String?) {
        lastSearch = text
        getPlaces(localFilters)
    }

    init {
        getPlaces(localFilters)
    }

}