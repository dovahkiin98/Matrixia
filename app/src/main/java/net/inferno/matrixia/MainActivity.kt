package net.inferno.matrixia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import net.inferno.matrixia.ui.theme.MatrixiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MatrixiaTheme {
                MainActivityUI()
            }
        }
    }
}