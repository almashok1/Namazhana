package kz.farabicorporation.namazhana.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kz.farabicorporation.namazhana.R
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.binding.BindingFragment
import kz.farabicorporation.namazhana.common.extensions.navigateWithAnim
import kz.farabicorporation.namazhana.databinding.FragmentStartBinding
import kz.farabicorporation.namazhana.domain.StartInteractor
import kotlin.reflect.KClass

class StartFragment : BindingFragment<FragmentStartBinding>(FragmentStartBinding::inflate) {
    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(StartViewModel::class)
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm(StartViewModel::class) {
            findNavController().navigateWithAnim(
                if (isFirstStart()) R.id.action_global_onboardingFragment
                else R.id.action_global_mainFragment,
                popUpTo = R.id.startFragment,
                inclusive = true
            )
        }
    }

}

class StartViewModel(
    private val startInteractor: StartInteractor
) : BaseViewModel() {

    fun isFirstStart() = startInteractor.getIsFirstStart()

    fun updateStart() = startInteractor.startUpdate()

}