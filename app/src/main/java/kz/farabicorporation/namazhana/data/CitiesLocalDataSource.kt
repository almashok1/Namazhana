package kz.farabicorporation.namazhana.data

import android.content.SharedPreferences
import kz.farabicorporation.namazhana.data.models.City
import kz.farabicorporation.namazhana.data.models.Coordinate

class CitiesLocalDataSource(
    private val sharedPreferences: SharedPreferences
) {

    companion object {
        private const val LAST_COORD_LAT = "LAST_COORD_LAT"
        private const val LAST_COORD_LONG = "LAST_COORD_LONG"
        private const val ALMATY_COORD_LAT = 43.128949
        private const val ALMATY_COORD_LONG = 76.889709
    }

    private val cities = hashMapOf<Int, City>()

    fun updateCities(list: List<City>) {
        cities.clear()
        list.forEach {
            cities[it.id] = it
        }
    }

    fun getCity(cityId: Int) = cities[cityId]

    fun updateLatestCoordinate(coordinate: Coordinate) {
        sharedPreferences.edit().apply {
            putString(LAST_COORD_LAT, coordinate.latitude.toString())
            putString(LAST_COORD_LONG, coordinate.longitude.toString())
            apply()
        }
    }

    fun getLatestCoordinate() : Coordinate {
        val lat = sharedPreferences.getString(LAST_COORD_LAT, null)?.toDoubleOrNull()
        val long = sharedPreferences.getString(LAST_COORD_LONG, null)?.toDoubleOrNull()
        return if (lat == null || long == null) getDefaultCoordinate() else Coordinate(lat, long)
    }

    fun getDefaultCoordinate(cityId: Int? = null): Coordinate {
        return cityId?.let { getCity(it)?.coordinate } ?: Coordinate(ALMATY_COORD_LAT, ALMATY_COORD_LONG)
    }

}