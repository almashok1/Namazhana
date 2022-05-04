package kz.farabicorporation.namazhana.ui.list

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialog
import kz.farabicorporation.namazhana.MainNavigationDirections
import kz.farabicorporation.namazhana.arch.models.OutcomeState
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.binding.BindingBottomSheetFragment
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.common.extensions.setFullScreen
import kz.farabicorporation.namazhana.databinding.FragmentPlaceListBinding
import kz.farabicorporation.namazhana.databinding.ItemPlaceDistanceChipBinding
import kz.farabicorporation.namazhana.ui.MainFragmentDirections
import kz.farabicorporation.namazhana.ui.filter.FilterFragment
import kz.farabicorporation.namazhana.ui.filter.FilterType
import kz.farabicorporation.namazhana.ui.filter.getText
import kz.farabicorporation.namazhana.ui.getDistance
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

class PlaceListFragment : BindingBottomSheetFragment<FragmentPlaceListBinding>(
    FragmentPlaceListBinding::inflate
) {
    private val args by navArgs<PlaceListFragmentArgs>()

    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(PlaceListViewModel::class) {
            { parametersOf(args.cityId) }
        }
    )

    override fun getTheme() = R.style.FullBottomSheetTheme

    private val vm get() = vm(PlaceListViewModel::class)!!

    private val distanceIds = mutableListOf<Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as BottomSheetDialog).run {
            setFullScreen(requireContext())
            behavior.isDraggable = false
        }
        vm.chipDistanceTypes.forEach { distanceType ->
            val id = View.generateViewId()
            distanceIds.add(id)
            inflateChip(binding.chipGroupType, distanceType.getText(requireContext()), id) {
                vm.selectDistanceType(distanceType)
            }
        }
        val adapter = PlaceListAdapter {
            findNavController().navigate(
                MainNavigationDirections.actionGlobalPlaceDetailsFragment(
                    it.id,
                    it.distance?.getDistance(requireContext())
                )
            )
        }
        binding.rv.adapter = adapter
        vm.places.observe(viewLifecycleOwner) {
            when (it) {
                is OutcomeState.Success -> {
                    binding.loading.isVisible = false
                    adapter.submitList(it.data)
                }
                is OutcomeState.Error -> {
                    binding.loading.isVisible = false
                }
                else -> {
                    binding.loading.isVisible = true
                }
            }
        }
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvFilter.onSafeClick {
            findNavController().navigate(MainFragmentDirections.actionGlobalFilterFragment(args.cityId))
        }
        binding.etSearch.doAfterTextChanged {
            vm.search(it?.toString())
        }
        FilterFragment.setResultListener(this) { filters ->
            vm.getPlaces(filters)
            filters.forEach {
                if (it is FilterType.DistanceType && it.distanceType != FilterType.DistanceType.Distance.OTHER)
                    binding.chipGroupType.check(distanceIds[it.index])
            }
        }
    }

    private fun inflateChip(root: ViewGroup, text: String, id: Int, onClick: (Boolean) -> Unit) {
        val chipBinding = ItemPlaceDistanceChipBinding.inflate(layoutInflater, root, true)
        chipBinding.root.text = text
        chipBinding.root.id = id
        chipBinding.root.onSafeClick {
            onClick(chipBinding.root.isChecked)
        }
    }
}