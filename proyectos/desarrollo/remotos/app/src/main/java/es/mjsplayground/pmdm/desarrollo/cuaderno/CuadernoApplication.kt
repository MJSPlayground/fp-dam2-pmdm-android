package es.mjsplayground.pmdm.desarrollo.cuaderno
import android.app.Application
import es.mjsplayground.pmdm.desarrollo.cuaderno.data.Contenedor

class CuadernoApplication : Application() {
    val contenedor by lazy { Contenedor() }
}
