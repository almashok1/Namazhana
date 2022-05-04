package kz.farabicorporation.namazhana.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.navigation.fragment.findNavController
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Circle
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import kz.farabicorporation.namazhana.BuildConfig
import kz.farabicorporation.namazhana.MainNavigationDirections
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.binding.BindingFragment
import kz.farabicorporation.namazhana.common.extensions.DoubleClickListener
import kz.farabicorporation.namazhana.common.extensions.beginChangeBoundsTransition
import kz.farabicorporation.namazhana.common.extensions.dpToPx
import kz.farabicorporation.namazhana.common.extensions.getActionBarHeight
import kz.farabicorporation.namazhana.common.extensions.getColorCompat
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.common.extensions.spring
import kz.farabicorporation.namazhana.data.models.Coordinate
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.data.models.PlaceType
import kz.farabicorporation.namazhana.databinding.FragmentMainBinding
import kz.farabicorporation.namazhana.ui.adapters.MainPlaceMiniAdapter
import kz.farabicorporation.namazhana.ui.adapters.MainSearchAdapter
import kz.farabicorporation.namazhana.ui.filter.FilterFragment
import kz.farabicorporation.namazhana.ui.filter.FilterType
import kotlin.reflect.KClass

class MainFragment : BindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate),
    UserLocationObjectListener {
    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(MainMapViewModel::class)
    )

    companion object {
        const val API_KEY = BuildConfig.YANDEX_MAP_API
        const val DEFAULT_ZOOM = 13f
        const val ZOOM_MAX = 18f
        private const val DEFAULT_LOCATION_CIRCLE_RADIUS = 3000f
        private const val DEFAULT_ICON_SCALE = 0.25f
    }

    private var userLocationLayer: UserLocationLayer? = null
    private val mapView get() = binding.mapView

    private val miniAdapter = MainPlaceMiniAdapter(::openPlaceDetails)
    private val searchAdapter = MainSearchAdapter(::openPlaceDetails)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMap()
    }

    private val requestPermissionInSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isLocationPermissionGranted(requireContext())) {
                createUserLocationLayer()
                vm.setLocationButtonRegular(binding.fabLocation)
            }
        }

    private val mapObjects get() = mapView.map.mapObjects

    private val tabListener : ChipNavigationBar.OnItemSelectedListener by lazy {
        object: ChipNavigationBar.OnItemSelectedListener {
            override fun onItemSelected(id: Int) {
                val tab = Tab.fromId(id)
                vm.onBottomTabSelected(tab)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createUserLocationLayer()
        binding.bottomNav.setItemSelected(Tab.ALL.getMenuId())
        binding.vgSearch.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = (16.dpToPx + requireActivity().getActionBarHeight()).toInt()
        }
        binding.bottomNav.setOnItemSelectedListener(tabListener)
        binding.rvMini.adapter = miniAdapter
        binding.rvSearch.adapter = searchAdapter

        val rvHeight = 170.dpToPx + 50.dpToPx

        vm.loading.observe(viewLifecycleOwner) {
            binding.bottomNav.isVisible = !it
            binding.vgShow.isVisible = !it
            binding.btnList.isVisible = !it
            binding.vgSearch.isVisible = !it
        }

        val rvAnim = binding.rvMini.spring(
            DynamicAnimation.TRANSLATION_Y,
            damping = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        ).addUpdateListener { _, value, _ ->
            val alpha = 1 - value / rvHeight
            binding.rvMini.alpha = alpha
            binding.rvMini.requestLayout()
            binding.fabLocation.translationY = value
            binding.btnList.translationY = value
        }

        vm.showBottom.observe(viewLifecycleOwner) {
            binding.tvShow.text =
                getString(if (it) R.string.common_hide else R.string.common_unhide)
            binding.ivShow.setImageResource(if (it) R.drawable.ic_polygon_down else R.drawable.ic_polygon_up)
            val pos = if (!it) rvHeight else 0f
            rvAnim.cancel()
            rvAnim.animateToFinalPosition(pos)
        }

        binding.etSearch.doAfterTextChanged {
            vm.search(it?.toString())
        }

        vm.searchResult.observe(viewLifecycleOwner) {
            searchAdapter.submitList(it) {
                binding.vgSearch.beginChangeBoundsTransition()
            }
        }

        binding.tvShow.onSafeClick {
            vm.switchShowHide()
        }

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().findViewById(android.R.id.content)) { _, insets ->
            val navigationBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            binding.bottomNav.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = 24.dpToPx.toInt() + navigationBarHeight
            }
            insets
        }

        vm.firstCity.observe(viewLifecycleOwner) {
            binding.mapView.map.move(
                CameraPosition(Point(it.latitude, it.longitude), DEFAULT_ZOOM, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 0.5F),
                null
            )
        }
        vm.points.observe(viewLifecycleOwner) {
            setTabIndexWithoutListener(it.first)
            mapObjects.clear()
            val points = it.second
            if (it.first != Tab.NAMAZHANA) addPlacemark(
                points.mosques,
                R.drawable.ic_map_pin_mechet
            )
            if (it.first != Tab.MECHET) addPlacemark(
                points.namazhanas,
                R.drawable.ic_map_pin_namazhana
            )
            miniAdapter.submitList(points.chooseType(it.first).take(10)) {
                binding.rvMini.scrollToPosition(0)
            }
        }

        vm.locationButtonState.observe(viewLifecycleOwner) {
            it.init(binding.fabLocation)
            it.onClick(
                requireActivity(),
                binding.fabLocation,
                userLocationLayer?.cameraPosition(),
                mapView,
                requestPermissionInSettingsLauncher
            )
        }
        setFabListener()

        binding.ivFilter.onSafeClick {
            findNavController().navigate(MainFragmentDirections.actionGlobalFilterFragment(vm.cityId.get()))
        }
        binding.btnList.onSafeClick {
            findNavController().navigate(MainFragmentDirections.actionGlobalPlaceListFragment(vm.cityId.get()))
        }
        FilterFragment.setResultListener(this) {
            val tab = getTab(it)
            loadData(it, tab)
        }
    }

    private fun getTab(list: List<FilterType>): Tab? {
        list.forEach {
            if (it is FilterType.PlaceFilterType) return when (it.type) {
                PlaceType.MECHET -> Tab.MECHET
                PlaceType.NAMAZHANA -> Tab.NAMAZHANA
                null -> Tab.ALL
            }
        }
        return null
    }

    private fun setTabIndexWithoutListener(tab: Tab) {
        binding.bottomNav.setOnItemSelectedListener {  }
        binding.bottomNav.setItemSelected(tab.getMenuId())
        binding.bottomNav.setOnItemSelectedListener(tabListener)
    }

    private fun setFabListener() {
        binding.fabLocation.setOnClickListener(object : DoubleClickListener() {
            override fun onDoubleClick() {
                vm.setLocationButtonContinuous(binding.fabLocation)
            }

            override fun onJustClick() {
                vm.setLocationButtonRegular(binding.fabLocation)
            }
        })
    }

    private fun addPlacemark(list: List<PlaceMini>, @DrawableRes icon: Int) {
        mapObjects.addPlacemarks(
            list.map { place -> Point(place.latitude, place.longitude) },
            ImageProvider.fromResource(requireContext(), icon, true),
            IconStyle().setScale(DEFAULT_ICON_SCALE)
        ).forEachIndexed { index, placemarkMapObject ->
            placemarkMapObject.userData = list[index]
            placemarkMapObject.addTapListener { mapObject, point ->
                val place = mapObject.userData as? PlaceMini
                place?.let(::openPlaceDetails)
                true
            }
        }
    }

    private val vm get() = vm(MainMapViewModel::class)!!

    private fun initMap() {
        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(requireContext())
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    private fun openPlaceDetails(placeMini: PlaceMini) {
        mapView.map.move(
            CameraPosition(Point(placeMini.latitude, placeMini.longitude), ZOOM_MAX, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.5F),
            null
        )
        findNavController().navigate(
            MainNavigationDirections.actionGlobalPlaceDetailsFragment(
                placeMini.id, placeMini.distance?.getDistance(requireContext())
            )
        )
    }

    private fun createUserLocationLayer() {
        if (userLocationLayer != null) return
        userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer!!.isVisible = true
        userLocationLayer!!.isHeadingEnabled = true

        userLocationLayer!!.setObjectListener(this)
    }

    private val userPinIcon
        get() = ImageProvider.fromResource(
            requireContext(), R.drawable.ic_map_pin_location
        ) to IconStyle().setScale(DEFAULT_ICON_SCALE)

    override fun onObjectAdded(userLocationView: UserLocationView) = with(userLocationView) {
        val (icon, style) = userPinIcon
        arrow.setIcon(icon, style)
        pin.setIcon(icon, style)
        pin.isVisible = true

        accuracyCircle.geometry = Circle(
            accuracyCircle.geometry.center,
            DEFAULT_LOCATION_CIRCLE_RADIUS
        )
        accuracyCircle.fillColor = requireContext().getColorCompat(R.color.main_primary_one_10)
        accuracyCircle.isVisible = true
        mapView.post { loadData() }
        Unit
    }

    override fun onObjectRemoved(userLocationView: UserLocationView) {
    }

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {
    }

    private fun loadData(
        filters: List<FilterType> = listOf(),
        predefinedItemTab: Tab? = null
    ) {
        val location = userLocationLayer!!.cameraPosition()?.target
        vm.loadAllData(
            true,
            Coordinate(
                location?.latitude ?: 0.0,
                location?.longitude ?: 0.0
            ),
            filters,
            withActiveTab = predefinedItemTab
        )
    }

}