package kz.farabicorporation.namazhana.common.binding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.EventObserver
import kz.farabicorporation.namazhana.common.R
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

abstract class BindingBottomSheetFragment<B : ViewBinding> constructor(
    private val inflate: (inflater: LayoutInflater, container: ViewGroup?, b: Boolean) -> B?
) : BottomSheetDialogFragment() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    private val _viewModels by lazy { provideViewModel().mapKeys { it.key.java.name } }
    abstract fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel>

    override fun getTheme(): Int {
        return R.style.BindingBottomSheetDialog
    }

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflate.invoke(inflater, container, false)
        if (_binding is ViewDataBinding) (_binding as ViewDataBinding).lifecycleOwner = viewLifecycleOwner
        return _binding?.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _viewModels.values.forEach { vmFun ->
            vmFun().vmActionFragment.observe(viewLifecycleOwner, EventObserver {
                it.invoke(this)
            })
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> vm(cl: KClass<T>? = null): T? {
        val provider = cl
            ?.let { _viewModels[it.java.name] }
            ?: return null

        return try {
            provider() as T
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    inline fun <T : Any> vm(cl: KClass<T>? = null, block: T.() -> Unit) {
        block(vm(cl) ?: return)
    }

    inline fun <reified T : BaseViewModel> vmCreator(
        cl: KClass<T>,
        crossinline parameters: () -> ParametersDefinition? = { null }
    ): Pair<KClass<*>, () -> BaseViewModel> {
        return cl to { getViewModel<T>(parameters = parameters()) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}