package kz.farabicorporation.namazhana.ui

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import jp.wasabeef.blurry.Blurry
import kz.farabicorporation.namazhana.R
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.binding.BindingFragment
import kz.farabicorporation.namazhana.common.extensions.navigateWithAnim
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.databinding.FragmentOnboardingBinding
import kz.farabicorporation.namazhana.domain.StartInteractor
import kotlin.reflect.KClass

class OnboardingFragment : BindingFragment<FragmentOnboardingBinding>(
    FragmentOnboardingBinding::inflate
) {
    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(OnboardingViewModel::class)
    )

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                onNextClick()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                onNextClick()
            }
            else -> requireActivity()
                .processLocationPermissionDecline(requestPermissionInSettingsLauncher)
        }
    }

    private val requestPermissionInSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (isLocationPermissionGranted(requireContext())) {
                onNextClick()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.post {
            Blurry.with(context)
                .radius(16)
                .sampling(6)
                .color(Color.argb(61, 85, 85, 85))
                .capture(binding.ivBackground)
                .into(binding.ivBlur)
        }
        binding.layoutOnboarding.btnStart.onSafeClick {
            if (isLocationPermissionGranted(requireContext())) {
                onNextClick()
            } else {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun onNextClick() {
        vm(OnboardingViewModel::class)?.onStartClicked()
    }
}

class OnboardingViewModel(
    private val startInteractor: StartInteractor
) : BaseViewModel() {

    fun onStartClicked() = withFragment {
        startInteractor.startUpdate()
        it.findNavController().navigateWithAnim(
            R.id.action_global_mainFragment,
            popUpTo = R.id.onboardingFragment,
            inclusive = true
        )
    }

}