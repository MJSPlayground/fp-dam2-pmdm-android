package es.mjsplayground.pmdm.desarrollo.cuaderno

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.mjsplayground.pmdm.desarrollo.cuaderno.ui.CuadernoApp
import es.mjsplayground.pmdm.desarrollo.cuaderno.ui.CuadernoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as CuadernoApplication).contenedor.repository
        setContent { CuadernoTheme { CuadernoApp(repository) } }
    }
}
