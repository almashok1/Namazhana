package kz.farabicorporation.namazhana.ui.details

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import kz.farabicorporation.namazhana.arch.models.OutcomeState
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.NavigationApps
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.binding.BindingBottomSheetFragment
import kz.farabicorporation.namazhana.common.binding.BindingFragment
import kz.farabicorporation.namazhana.common.extensions.ifNullOrEmpty
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.common.extensions.setUpRoundedCorners
import kz.farabicorporation.namazhana.data.FEEDBACK_MAIL
import kz.farabicorporation.namazhana.data.FEEDBACK_MAIL_TITLE
import kz.farabicorporation.namazhana.data.getMailSubject
import kz.farabicorporation.namazhana.data.models.Feedback
import kz.farabicorporation.namazhana.data.models.PlaceFull
import kz.farabicorporation.namazhana.data.models.PlaceType
import kz.farabicorporation.namazhana.data.models.getColor
import kz.farabicorporation.namazhana.data.models.getText
import kz.farabicorporation.namazhana.data.models.toWorkTime
import kz.farabicorporation.namazhana.databinding.FragmentPlaceDetailsBinding
import kz.farabicorporation.namazhana.databinding.FragmentPlaceImageBinding
import kz.farabicorporation.namazhana.ui.details.feedback.PlaceFeedbackAdapter
import kz.farabicorporation.namazhana.ui.getYesNoText
import kz.farabicorporation.namazhana.ui.loadWithPlaceType
import java.util.Date
import kotlin.reflect.KClass


class PlaceDetailsFragment : BindingBottomSheetFragment<FragmentPlaceDetailsBinding>(
    FragmentPlaceDetailsBinding::inflate
) {

    private val args by navArgs<PlaceDetailsFragmentArgs>()

    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(PlaceDetailsViewModel::class)
    )

    private val vm get() = vm(PlaceDetailsViewModel::class)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRoundedCorners()
        vm.loadDetails(args.placeId)

        vm.state.observe(viewLifecycleOwner) {
            when (it) {
                OutcomeState.Idle, OutcomeState.Loading -> {
                    binding.loading.isVisible = true
                }
                is OutcomeState.Success -> {
                    binding.loading.isVisible = it.data == null
                    it.data?.let(::bindDetails)
                }
                else -> {
                    dismiss()
                }
            }
        }

        vm.feedbacksState.observe(viewLifecycleOwner) {
            when (it) {
                OutcomeState.Idle, OutcomeState.Loading -> {
                    binding.loadingFeedbacks.isVisible = true
                }
                is OutcomeState.Success -> {
                    binding.loadingFeedbacks.isVisible = false
                    val data = it.data ?: return@observe
                    val adapter = PlaceFeedbackAdapter(data.second.type)
                    val comments = data.first
                    binding.tvFeedbacks.text = getString(R.string.common_feedbacks, comments.size)
                    binding.rvFeedback.adapter = adapter
                    binding.rvFeedback.isVisible = comments.isNotEmpty()
                    binding.vgComments.isVisible = comments.isNotEmpty()
                    adapter.submitList(comments)
                }
                else -> {}
            }
        }
    }

    private fun bindDetails(place: PlaceFull) = with(binding) {
        val color = place.type.getColor(requireContext())
        tvAddress.text = place.getFullAddress()
        tvType.text = place.type.getText(requireContext())
        tvType.setTextColor(color)
        tvDistance.text = args.distance
        tvDistance.isVisible = args.distance != null
        tvDistance.setTextColor(color)
        tvName.text = place.name
        vpImages.adapter = ViewPagerAdapter(this@PlaceDetailsFragment, place)
        tvTime.text = (place.startWorkTime to place.endWorkTime).toWorkTime(requireContext())
        TextViewCompat.setCompoundDrawableTintList(ivTime, ColorStateList.valueOf(color))
        TextViewCompat.setCompoundDrawableTintList(ivAblutionPlace, ColorStateList.valueOf(color))
        TextViewCompat.setCompoundDrawableTintList(ivIsJumaRead, ColorStateList.valueOf(color))
        TextViewCompat.setCompoundDrawableTintList(ivPeopleFit, ColorStateList.valueOf(color))
        TextViewCompat.setCompoundDrawableTintList(ivWomenPlace, ColorStateList.valueOf(color))
        tvAblutionPlace.text = (place.isAblutionPlace ?: false).getYesNoText(requireContext())
        tvIsJumaRead.text = (place.isJumaRead ?: false).getYesNoText(requireContext())
        tvPeopleFit.text = place.peopleFit ?: "-"
        tvWomenPlace.text = place.getWomenSpaceDescription()
        dotsIndicator.setViewPager2(vpImages)

        // location
        TextViewCompat.setCompoundDrawableTintList(tvBuildRoad, ColorStateList.valueOf(color))
        tvBuildRoad.setTextColor(color)
        vgMapTemplate.onSafeClick {
            try {
                NavigationApps(requireContext()).checkAll(
                    NavigationApps.LatLng(place.coordinate.latitude, place.coordinate.longitude)
                ).guideMe(true)
            } catch (e: Exception) {
            }
        }

        // feedback
        tvLeaveFeedback.setTextColor(color)
        tvLeaveFeedback.onSafeClick { composeEmail(place) }
    }

    private fun PlaceFull.getWomenSpaceDescription(): String {
        return if (womenSpace.description.isNullOrEmpty())
            (womenSpace.isExist ?: false).getYesNoText(requireContext())
        else womenSpace.description
    }

    private fun PlaceFull.getFullAddress(): String {
        return "${vm.getCity(cityId)?.name ?: ""}, $address".run {
            if (floor.isNullOrEmpty() || floor.trim() == "-") this else plus(", $floor")
        }
    }

    private fun getMockFeedbacks(): List<Feedback> {
        val longMessage =
            "Любимая мечеть, еще в раннем возрасте апашка водила каждую пятницу с утра, Альхамдулиллах! Любимая мечеть, еще в раннем возрасте апашка водила каждую пятницу с утра, Альхамдулиллах! Любимая мечеть, еще в раннем возрасте апашка водила каждую пятницу с утра, Альхамдулиллах!"
        val shortMessage =
            "Check Любимая мечеть, еще в раннем возрасте апашка водила каждую пятницу с утра"
        return listOf(
            Feedback(1, "Almas", "Tanayev", Date(), longMessage),
            Feedback(2, "Almas", "Almasov", Date(), shortMessage),
            Feedback(3, "Almas", "Almasov", Date(), longMessage),
        )
    }

    private class ViewPagerAdapter(
        fragment: Fragment,
        private val placeFull: PlaceFull
    ) : FragmentStateAdapter(fragment) {
        private val images = placeFull.images?.map { it.small.ifNullOrEmpty { it.original } } ?: listOf()

        override fun getItemCount() = images.size

        override fun createFragment(position: Int): Fragment {
            return ImageFragment.newInstance(images.getOrNull(position), placeFull.type)
        }

    }

    private fun composeEmail(place: PlaceFull) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(FEEDBACK_MAIL))
            putExtra(Intent.EXTRA_SUBJECT, getMailSubject(place.name))
        }
        startActivity(Intent.createChooser(emailIntent, FEEDBACK_MAIL_TITLE))
    }

    internal class ImageFragment : BindingFragment<FragmentPlaceImageBinding>(
        FragmentPlaceImageBinding::inflate
    ) {
        override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            binding.root.loadWithPlaceType(
                requireArguments().getString(ARGS_IMAGE, null),
                requireArguments().getParcelable(PLACE_TYPE) ?: PlaceType.MECHET
            )
        }

        companion object {

            private const val ARGS_IMAGE = "image"
            private const val PLACE_TYPE = "place_type"

            fun newInstance(image: String?, placeType: PlaceType) = ImageFragment().apply {
                arguments = bundleOf(
                    ARGS_IMAGE to image,
                    PLACE_TYPE to placeType
                )
            }
        }

    }
}