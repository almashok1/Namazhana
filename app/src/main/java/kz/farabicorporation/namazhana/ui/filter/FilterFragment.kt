package kz.farabicorporation.namazhana.ui.filter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.arch.models.OutcomeState
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.binding.BindingBottomSheetFragment
import kz.farabicorporation.namazhana.common.extensions.dpToPx
import kz.farabicorporation.namazhana.common.extensions.getBottomSheetBehavior
import kz.farabicorporation.namazhana.common.extensions.observePrevSavedState
import kz.farabicorporation.namazhana.common.extensions.observeSavedState
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.common.extensions.setFullScreen
import kz.farabicorporation.namazhana.common.extensions.setShowProgress
import kz.farabicorporation.namazhana.common.extensions.setToPrevSavedState
import kz.farabicorporation.namazhana.common.extensions.setUpRoundedCorners
import kz.farabicorporation.namazhana.databinding.FragmentFilterBinding
import kz.farabicorporation.namazhana.databinding.ItemFilterChipBinding
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

class FilterFragment : BindingBottomSheetFragment<FragmentFilterBinding>(
    FragmentFilterBinding::inflate
) {
    private val navArgs by navArgs<FilterFragmentArgs>()

    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(FilterViewModel::class) {
            { parametersOf(navArgs.cityId) }
        }
    )

    private val vm get() = vm(FilterViewModel::class)!!

    private val distanceTypeIds = mutableListOf<Int>()
    private val placeTypeIds = mutableListOf<Int>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRoundedCorners {
            (this as BottomSheetDialog).setFullScreen(requireContext(), 70.dpToPx.toInt())
        }
        (dialog as BottomSheetDialog).getBottomSheetBehavior().run {
            isDraggable = false
            isFitToContents = true
        }
        observePrevSavedState<List<FilterType>>(KEY_OBSERVE) {
            vm.setLatestFilters(it)
        }
        vm.chipDistanceTypes.forEach { filterType ->
            val id = View.generateViewId()
            distanceTypeIds.add(id)
            inflateChip(binding.chipGroupDistance, filterType.getText(requireContext()), id) {
                vm.updateFilter(filterType)
            }
        }

        vm.chipPlaceTypes.forEach { filterType ->
            val id = View.generateViewId()
            placeTypeIds.add(id)
            inflateChip(binding.chipGroupType, filterType.getText(requireContext()), id) {
                vm.updateFilter(filterType)
            }
        }

        vm.filters.observe(viewLifecycleOwner) {
            it.forEach(::bindFilter)
            setClickers()
        }
        vm.places.observe(viewLifecycleOwner) {
            when(it) {
                is OutcomeState.Success -> {
                    setShowProgress(false)
                    binding.btnApply.text = "${getString(R.string.filter_applyFilter)} (${it.data?.size ?: 0})"
                }
                is OutcomeState.Idle, is OutcomeState.Error -> {
                    setShowProgress(false)
                    binding.btnApply.text = getString(R.string.filter_applyFilter)
                }
                is OutcomeState.Loading -> {
                    setShowProgress(true)
                }
            }
        }
        binding.btnApply.onSafeClick {
            setToPrevSavedState(KEY_OBSERVE, vm.getLatestFilters())
            findNavController().navigateUp()
        }
        binding.tvClear.onSafeClick { vm.resetFilter() }
        binding.ivClose.onSafeClick { findNavController().navigateUp() }
    }

    private fun setShowProgress(isLoading: Boolean) {
        binding.btnApply.setShowProgress(isLoading, getString(R.string.filter_applyFilter))
    }

    private fun inflateChip(root: ViewGroup, text: String, id: Int, onClick: (Boolean) -> Unit) {
        val chipBinding = ItemFilterChipBinding.inflate(layoutInflater, root, true)
        chipBinding.root.text = text
        chipBinding.root.id = id
        chipBinding.root.onSafeClick {
            onClick(chipBinding.root.isChecked)
        }
    }

    private fun setClickers() = with(binding) {
        vgWomenPlace.onSafeClick { switchWomenPlace.performClick() }
        switchWomenPlace.onClickSwitchFilter { FilterType.WomenPlaceType(it) }
        vgJumaRead.onSafeClick { switchJumaRead.performClick() }
        switchJumaRead.onClickSwitchFilter { FilterType.IsJumaReadType(it) }
        vgAblutionPlace.onSafeClick { switchAblutionPlace.performClick() }
        switchAblutionPlace.onClickSwitchFilter { FilterType.AblutionPlaceType(it) }
    }

    private inline fun<reified T: FilterType> SwitchCompat.onClickSwitchFilter(crossinline generateFilter: (Boolean) -> T) {
        setOnCheckedChangeListener { _, isChecked ->
            vm.updateFilter(generateFilter(isChecked))
        }
    }

    private fun bindFilter(filter: FilterType) {
        when(filter) {
            is FilterType.AblutionPlaceType -> {
                binding.switchAblutionPlace.isChecked = filter.hasAblutionPlace ?: false
            }
            is FilterType.DistanceType -> {
                binding.chipGroupDistance.check(distanceTypeIds[filter.index])
            }
            is FilterType.IsJumaReadType -> {
                binding.switchJumaRead.isChecked = filter.isJumaRead ?: false
            }
            is FilterType.PlaceFilterType -> {
                binding.chipGroupType.check(placeTypeIds[filter.index])
            }
            is FilterType.WomenPlaceType -> {
                binding.switchWomenPlace.isChecked = filter.hasWomenPlace ?: false
            }
        }
    }

    companion object {

        const val KEY_OBSERVE = "observe_filter"

        fun setResultListener(fragment: Fragment, onResult: (List<FilterType>) -> Unit) {
            fragment.observeSavedState(KEY_OBSERVE, onResult)
        }

    }
}