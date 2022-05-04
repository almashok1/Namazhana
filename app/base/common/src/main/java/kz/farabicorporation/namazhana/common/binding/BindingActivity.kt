package kz.farabicorporation.namazhana.common.binding

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.EventObserver
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.ParametersDefinition
import kotlin.reflect.KClass

abstract class BindingActivity<B : ViewBinding> constructor(
    private val inflate: (inflater: LayoutInflater) -> B?
) : AppCompatActivity() {

    private var _binding: B? = null
    protected val binding get() = _binding!!

    private val _viewModels by lazy { provideViewModel().mapKeys { it.key.java.name } }
    abstract fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = inflate.invoke(layoutInflater)
        if (_binding is ViewDataBinding) (_binding as ViewDataBinding).lifecycleOwner = this
        _viewModels.values.forEach { vmFun ->
            vmFun().vmActionActivity.observe(this, EventObserver {
                it.invoke(this)
            })
        }
        setContentView(_binding?.root)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}