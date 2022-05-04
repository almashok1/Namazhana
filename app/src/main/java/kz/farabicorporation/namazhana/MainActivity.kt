package kz.farabicorporation.namazhana

import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kz.farabicorporation.namazhana.common.AppMessages
import kz.farabicorporation.namazhana.common.BaseViewModel
import kz.farabicorporation.namazhana.common.EventObserver
import kz.farabicorporation.namazhana.common.binding.BindingActivity
import kz.farabicorporation.namazhana.common.extensions.getNavigationBarsHeight
import kz.farabicorporation.namazhana.common.showSnackbar
import kz.farabicorporation.namazhana.databinding.ActivityMainBinding
import kotlin.reflect.KClass


class MainActivity: BindingActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private lateinit var navController: NavController

    private val navBarHeight by lazy {
        getNavigationBarsHeight()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpNavigation()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
//        WindowCompat.setDecorFitsSystemWindows(window, false)
//        WindowCompat.getInsetsController(window, window.decorView)?.show(WindowInsetsCompat.Type.statusBars())
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { rootView, insets ->
//            val navigationBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
//            rootView.setPadding(0, 0, 0, navigationBarHeight)
//            insets
//        }
    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        AppMessages.snackbarMessages.observe(this, EventObserver { pair ->
            showSnackbar(pair, navBarHeight)
        })

    }

    override fun provideViewModel(): Map<KClass<*>, () -> BaseViewModel> = mapOf(
        vmCreator(MainViewModel::class)
    )

}