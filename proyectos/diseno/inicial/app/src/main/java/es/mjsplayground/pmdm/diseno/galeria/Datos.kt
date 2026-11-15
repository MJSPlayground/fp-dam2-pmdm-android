package es.mjsplayground.pmdm.diseno.galeria

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

data class Lamina(val id: Int, val titulo: Int, val grupo: Int, val imagen: Any)

val laminas = listOf(
    Lamina(1, R.string.lamina_bosque, R.string.naturaleza, R.drawable.bosque),
    Lamina(2, R.string.lamina_montana, R.string.naturaleza, R.drawable.montana),
    Lamina(3, R.string.lamina_ciudad, R.string.ciudad, R.drawable.ciudad),
    Lamina(4, R.string.lamina_app, R.string.ciudad,
        "https://raw.githubusercontent.com/MJSPlayground/fp-dam2-pmdm-android/main/docs/assets/capturas/primera-app.png")
)

fun columnas(ancho: WindowWidthSizeClass): Int =
    // TODO grid: devuelve 1, 2 o 3 segun la clase de ancho recibida.
    1

fun seleccionar(datos: List<Lamina>, categoria: Int?, soloFavoritos: Boolean, favoritos: Set<Int>): List<Lamina> =
    datos.filter { (categoria == null || it.grupo == categoria) && (!soloFavoritos || it.id in favoritos) }

fun alternar(favoritos: Set<Int>, id: Int): Set<Int> =
    // TODO favoritos: alterna pertenencia al conjunto sin modificarlo.
    favoritos
