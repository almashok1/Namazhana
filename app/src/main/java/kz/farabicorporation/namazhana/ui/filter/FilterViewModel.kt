package kz.farabicorporation.namazhana.ui.filter

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kz.farabicorporation.namazhana.arch.utils.doOnSuccess
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.extensions.getState
import kz.farabicorporation.namazhana.common.extensions.liveData
import kz.farabicorporation.namazhana.common.extensions.setToPrevSavedState
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.data.models.PlaceType
import kz.farabicorporation.namazhana.domain.PlaceInteractor

class FilterViewModel(
    private val cityId: Int,
    private val placeInteractor: PlaceInteractor
) : BaseViewModel() {

    private val _places = getState<List<PlaceMini>>(Dispatchers.IO, debounce = 600L)
    val places = _places.liveData

    private val _filters = MutableStateFlow(defaultFilterTypes)
    val filters = _filters.asLiveData(viewModelScope.coroutineContext)

    val chipPlaceTypes get() = listOf(
        FilterType.PlaceFilterType(0, PlaceType.NAMAZHANA),
        FilterType.PlaceFilterType(1, PlaceType.MECHET),
        FilterType.PlaceFilterType(2)
    )

    val chipDistanceTypes get() = listOf(
        FilterType.DistanceType(0, FilterType.DistanceType.Distance.ALL),
        FilterType.DistanceType(1, FilterType.DistanceType.Distance.NEAR, 2),
        FilterType.DistanceType(2, FilterType.DistanceType.Distance.OTHER, 3),
        FilterType.DistanceType(3, FilterType.DistanceType.Distance.OTHER, 5),
    )

    fun getLatestFilters() = _filters.value
    fun setLatestFilters(list: List<FilterType>, onSuccess: (() -> Unit)? = null) {
        _filters.value = list
        applyFilter(onSuccess)
    }


    private fun applyFilter(onSuccess: (() -> Unit)? = null) {
        _places.request {
            placeInteractor.getAllPlacesByCityId(
                cityId, placeInteractor.getLatestCoordinate(), _filters.value
            ).doOnSuccess {
                onSuccess?.invoke()
            }
        }
    }

    fun resetFilter() {
        setLatestFilters(defaultFilterTypes) {
            withFragment {
                it.setToPrevSavedState(FilterFragment.KEY_OBSERVE, getLatestFilters())
                it.findNavController().navigateUp()
            }
        }
    }

    inline fun<reified T: FilterType> updateFilter(type: T) {
        setLatestFilters(mapFilter<T> { type })
    }

    inline fun <reified T : FilterType> mapFilter(block: (T) -> FilterType): List<FilterType> {
        return getLatestFilters().mapFilter(block)
    }

    inline fun <reified T : FilterType> List<FilterType>.mapFilter(block: (T) -> FilterType): List<FilterType> {
        return map {
            if (it is T) block(it) else it
        }
    }
}