package kz.farabicorporation.namazhana.ui

import android.content.Intent
import android.content.res.ColorStateList
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kz.farabicorporation.namazhana.arch.utils.doOnError
import kz.farabicorporation.namazhana.arch.utils.doOnSuccess
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.extensions.getColorCompat
import kz.farabicorporation.namazhana.common.extensions.setMain
import kz.farabicorporation.namazhana.data.models.Coordinate
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.data.models.PlaceType
import kz.farabicorporation.namazhana.domain.PlaceInteractor
import kz.farabicorporation.namazhana.ui.filter.FilterType
import java.util.concurrent.atomic.AtomicInteger

class MainMapViewModel(
    private val placeInteractor: PlaceInteractor
) : BaseViewModel() {

    var loadedFirst: Boolean? = null

    private val _firstCity = MutableLiveData<Coordinate>()
    val firstCity: LiveData<Coordinate> get() = _firstCity

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> get() = _loading

    private val _points = MutableStateFlow(Tab.ALL to Placemarks.empty())
    val points: LiveData<Pair<Tab, Placemarks>> get() = _points.asLiveData(viewModelScope.coroutineContext)

    private val _showBottom = MutableLiveData(false)
    val showBottom: LiveData<Boolean> get() = _showBottom

    private val _locationButtonState =
        MutableLiveData<LocationButtonState>(LocationButtonState.Regular)
    val locationButtonState: LiveData<LocationButtonState> get() = _locationButtonState

    private val _searchResult = MutableStateFlow<List<Pair<IntRange, PlaceMini>>>(listOf())
    val searchResult: LiveData<List<Pair<IntRange, PlaceMini>>>
        get() = _searchResult.debounce(500L).asLiveData(viewModelScope.coroutineContext)

    val cityId: AtomicInteger = AtomicInteger()

    init {
        launchIO {
            _firstCity.setMain(placeInteractor.getLatestCoordinate())
        }
    }

    fun switchShowHide() {
        _showBottom.value = !_showBottom.value!!
    }

    fun hideBottom() {
        _showBottom.postValue(false)
    }

    fun showBottom() {
        _showBottom.postValue(true)
    }

    fun loadAllData(
        withLoading: Boolean = true,
        userCoordinate: Coordinate? = null,
        filters: List<FilterType> = listOf(),
        withActiveTab: Tab? = null
    ) = launchIO {
        if (withLoading) _loading.setMain(true)
        placeInteractor.getPlacesWithCityId(userCoordinate, filters)
            .doOnSuccess {
                if (it?.isPresent == true) {
                    val city = it.get().first
                    cityId.set(city.id)
                    val places = it.get().second
                    _firstCity.setMain(userCoordinate ?: city.coordinate)
                    val placemarks = Placemarks(
                        mosques = places.filter { place -> place.type == PlaceType.MECHET },
                        namazhanas = places.filter { place -> place.type == PlaceType.NAMAZHANA },
                        all = places
                    )
                    _points.value = (withActiveTab ?: Tab.ALL) to placemarks
                    if (places.isNullOrEmpty()) hideBottom()
                    else showBottom()
                } else {
                    hideBottom()
                }
            }
            .doOnError {
                hideBottom()
            }
        _loading.setMain(false)
        loadedFirst = true
    }.bindSubscribe("loadFirstCity")

    fun onBottomTabSelected(tab: Tab) {
        _points.value = tab to _points.value.second
    }

    fun setLocationButtonRegular(button: FloatingActionButton) = launchIO {
        _locationButtonState.value?.dispose(button)
        _locationButtonState.setMain(LocationButtonState.Regular)
    }

    fun setLocationButtonContinuous(button: FloatingActionButton) = launchIO {
        _locationButtonState.value?.dispose(button)
        _locationButtonState.setMain(LocationButtonState.Continuous(viewModelScope))
    }


    private suspend fun LocationButtonState.dispose(
        button: FloatingActionButton
    ) {
        withContext(Dispatchers.Main) {
            cancel(button)
        }
    }

    fun search(text: String? = null) = launchDefault {
        if (text.isNullOrEmpty()) {
            _searchResult.value = listOf()
            return@launchDefault
        }
        _searchResult.value = _points.value.second.all.mapNotNull {
            val range = getStartAndEndOfSubstring(it.name.lowercase(), text.lowercase())
            if (range == IntRange.EMPTY) null
            else range to it
        }.take(5).sortedBy {
            it.first.last - it.first.first
        }
    }

    private fun getStartAndEndOfSubstring(str: String, sub: String): IntRange {
        val start = str.indexOf(sub)
        return when (start != -1) {
            true -> start until start + sub.length
            false -> IntRange.EMPTY
        }
    }

}

class Placemarks(
    val mosques: List<PlaceMini>,
    val namazhanas: List<PlaceMini>,
    val all: List<PlaceMini>
) {
    fun chooseType(tab: Tab) = when (tab) {
        Tab.MECHET -> mosques
        Tab.ALL -> all
        Tab.NAMAZHANA -> namazhanas
    }

    companion object {
        fun empty() = Placemarks(listOf(), listOf(), listOf())
    }
}

enum class Tab(val index: Int) {
    MECHET(0), ALL(1), NAMAZHANA(2);

    companion object {
        fun from(index: Int): Tab {
            return when (index) {
                0 -> MECHET
                2 -> NAMAZHANA
                else -> ALL
            }
        }
        fun fromId(id: Int): Tab = when(id) {
            kz.farabicorporation.namazhana.R.id.mechet -> MECHET
            kz.farabicorporation.namazhana.R.id.namazhana -> NAMAZHANA
            else -> ALL
        }
    }

    fun getMenuId() = when(this) {
        MECHET -> kz.farabicorporation.namazhana.R.id.mechet
        ALL -> kz.farabicorporation.namazhana.R.id.all
        NAMAZHANA -> kz.farabicorporation.namazhana.R.id.namazhana
    }
}

sealed class LocationButtonState {

    abstract fun init(button: FloatingActionButton)
    abstract suspend fun cancel(button: FloatingActionButton)

    fun moveCamera(
        cameraPosition: CameraPosition?,
        mapView: MapView
    ) = cameraPosition?.let {
        mapView.map.move(
            CameraPosition(it.target, MainFragment.DEFAULT_ZOOM, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 1F),
            null
        )
    } ?: Unit

    abstract fun onClick(
        activity: FragmentActivity,
        button: FloatingActionButton,
        cameraPosition: CameraPosition?,
        mapView: MapView,
        launcher: ActivityResultLauncher<Intent>
    )

    object Regular : LocationButtonState() {
        override fun init(button: FloatingActionButton) {
            button.supportBackgroundTintList = ColorStateList.valueOf(
                button.context.getColorCompat(
                    R.color.white
                )
            )
            button.supportImageTintList = ColorStateList.valueOf(
                button.context.getColorCompat(
                    R.color.main_primary_one
                )
            )
        }

        override suspend fun cancel(button: FloatingActionButton) {}

        override fun onClick(
            activity: FragmentActivity,
            button: FloatingActionButton,
            cameraPosition: CameraPosition?,
            mapView: MapView,
            launcher: ActivityResultLauncher<Intent>
        ) {
            if (isLocationPermissionGranted(button.context)) moveCamera(cameraPosition, mapView)
            else activity.processLocationPermissionDecline(launcher)
        }
    }

    class Continuous(
        private val scope: CoroutineScope
    ) : LocationButtonState() {

        private var job: Job? = null

        override fun init(button: FloatingActionButton) {
            button.supportBackgroundTintList = ColorStateList.valueOf(
                button.context.getColorCompat(
                    R.color.main_primary_one
                )
            )
            button.supportImageTintList = ColorStateList.valueOf(
                button.context.getColorCompat(
                    R.color.white
                )
            )
        }

        override suspend fun cancel(button: FloatingActionButton) {
            job?.cancelAndJoin()
        }

        override fun onClick(
            activity: FragmentActivity,
            button: FloatingActionButton,
            cameraPosition: CameraPosition?,
            mapView: MapView,
            launcher: ActivityResultLauncher<Intent>
        ) {
            job?.cancel()
            if (isLocationPermissionGranted(button.context)) {
                job = scope.launch {
                    while (isActive) {
                        moveCamera(cameraPosition, mapView)
                        delay(1000)
                    }
                }
            } else {
                activity.processLocationPermissionDecline(launcher)
            }
        }

    }

}