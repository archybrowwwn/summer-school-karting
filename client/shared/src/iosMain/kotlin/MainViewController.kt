import androidx.compose.ui.window.ComposeUIViewController
import com.apexkarting.ApexApp
import com.apexkarting.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    ApexApp()
}
