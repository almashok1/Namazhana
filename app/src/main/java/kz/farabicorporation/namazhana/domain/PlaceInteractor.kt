package kz.farabicorporation.namazhana.domain

import com.google.common.base.Optional
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kz.farabicorporation.namazhana.arch.models.Outcome
import kz.farabicorporation.namazhana.arch.utils.asSuccess
import kz.farabicorporation.namazhana.arch.utils.doOnSuccess
import kz.farabicorporation.namazhana.arch.utils.map
import kz.farabicorporation.namazhana.arch.utils.transform
import kz.farabicorporation.namazhana.data.ApiHelper
import kz.farabicorporation.namazhana.data.CitiesLocalDataSource
import kz.farabicorporation.namazhana.data.models.City
import kz.farabicorporation.namazhana.data.models.Coordinate
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.ui.filter.FilterType
import java.util.LinkedList
import java.util.logging.Filter

class PlaceInteractor(
    private val apiHelper: ApiHelper,
    private val citiesLocalDataSource: CitiesLocalDataSource
) {

    private val mutex = Mutex()

    suspend fun getAllCitiesByCountryId(countryId: Int) =
        apiHelper.countryApi.getAllCitiesByCountryId(countryId)

    suspend fun getAllCountries() = apiHelper.countryApi.getAllCountries()

    suspend fun getAllFeedbacks(placeId: Int) = apiHelper.feedbackApi.getAllFeedbacks(placeId)

    suspend fun getAllPlacesByCityId(
        cityId: Int,
        coordinate: Coordinate? = null,
        filters: List<FilterType> = listOf()
    ): Outcome<List<PlaceMini>> {
        return if (coordinate == null) {
            citiesLocalDataSource.updateLatestCoordinate(
                citiesLocalDataSource.getDefaultCoordinate(cityId)
            )
            apiHelper.placeApi.getAllPlacesByCityId(cityId)
        } else {
            citiesLocalDataSource.updateLatestCoordinate(coordinate)
            apiHelper.placeApi.getAllPlacesByUserCoordinate(cityId, coordinate, filters.toQuery()).map {
                it?.sortedBy { place -> place.distance }
            }
        }
    }

    suspend fun getPlaceDetails(placeId: Int) = apiHelper.placeApi.getPlaceDetails(placeId)

    suspend fun getFirstCity(userCoordinate: Coordinate? = null): Outcome<Optional<City>> {
        return if (userCoordinate == null) getAllCountries().transform {
            val country = it?.firstOrNull() ?: return@transform Optional.absent<City>().asSuccess()
            getAllCitiesByCountryId(country.id).map { cities ->
                citiesLocalDataSource.updateCities(cities ?: listOf())
                Optional.fromNullable(cities?.firstOrNull())
            }
        } else apiHelper.countryApi.getAllCitiesByUserCoordinate(userCoordinate).map {
            citiesLocalDataSource.updateCities(it ?: listOf())
            Optional.fromNullable(it?.minByOrNull { city -> city.distance ?: Double.MAX_VALUE })
        }
    }

    suspend fun getPlacesWithCityId(
        userCoordinate: Coordinate? = null,
        filters: List<FilterType> = listOf()
    ): Outcome<Optional<Pair<City, List<PlaceMini>>>> {
        return getFirstCity(userCoordinate).transform {
            if (it?.isPresent == true) {
                getAllPlacesByCityId(it.get().id, userCoordinate, filters).map { list ->
                    Optional.of(it.get() to (list ?: listOf()))
                }
            } else {
                Optional.absent<Pair<City, List<PlaceMini>>>().asSuccess()
            }
        }
    }

    suspend fun getAllPlacesWithCities(
        scope: CoroutineScope,
        userCoordinate: Coordinate? = null
    ) = getAllCountries().map { countries ->
        val defCitiesList = LinkedList<Deferred<*>>()
        val citiesList = LinkedList<City>()
        countries?.forEach {
            val deferred = scope.async {
                getAllCitiesByCountryId(it.id).doOnSuccess {
                    mutex.withLock {
                        if (it != null) citiesList.addAll(it)
                    }
                }
            }
            defCitiesList.add(deferred)
        }
        defCitiesList.awaitAll()
        val defPlacesList = LinkedList<Deferred<*>>()
        val placesList = LinkedList<PlaceMini>()
        citiesList.forEach { city ->
            val deferred = scope.async {
                getAllPlacesByCityId(city.id, userCoordinate).doOnSuccess {
                    mutex.withLock {
                        if (it != null) placesList.addAll(it)
                    }
                }
            }
            defPlacesList.add(deferred)
        }
        defPlacesList.awaitAll()
        citiesList.sortedBy { it.id } to placesList.sortedBy { it.distance }
    }

    fun getCity(cityId: Int) = citiesLocalDataSource.getCity(cityId)

    fun getLatestCoordinate() = citiesLocalDataSource.getLatestCoordinate()

    fun updateLatestCoordinate(coordinate: Coordinate) = citiesLocalDataSource.updateLatestCoordinate(coordinate)

    private fun List<FilterType>.toQuery(): Map<String, Any> {
        return filter { it.getValue() != null }.associateBy(
            { it.queryName },
            { it.getValue()!! }
        )
    }
}
