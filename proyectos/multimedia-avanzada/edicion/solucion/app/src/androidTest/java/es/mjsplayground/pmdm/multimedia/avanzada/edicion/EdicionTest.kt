package es.mjsplayground.pmdm.multimedia.avanzada.edicion
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
@RunWith(AndroidJUnit4::class)
class EdicionTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val contexto = ApplicationProvider.getApplicationContext<Context>()
    @Test fun recorteCuadradoProduceUnPngReal() {
        val carpeta = File(contexto.filesDir, "medios")
        val previas = carpeta.listFiles()?.map { it.name }?.toSet() ?: emptySet()
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Cargar patrón propio").performClick()
            compose.waitUntil(20_000) { compose.onAllNodesWithText("Imagen lista").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Recorte cuadrado").performScrollTo().performClick()
            compose.onNodeWithText("Girar imagen").performScrollTo().performClick()
            compose.onNodeWithText("Guardar PNG").performScrollTo().performClick()
            compose.waitUntil(30_000) { compose.onAllNodesWithText("Recorte guardado").fetchSemanticsNodes().isNotEmpty() }
            val salida = carpeta.listFiles()!!.single { it.extension == "png" && it.name !in previas }
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(salida.absolutePath, limites)
            assertEquals("image/png", limites.outMimeType)
            assertTrue(limites.outWidth > 0 && limites.outWidth <= 1024)
            assertEquals(limites.outWidth, limites.outHeight)
        }
    }
    @Test fun transformerExportaAvcSinAudioYReduceAltura() {
        val instrumentacion = InstrumentationRegistry.getInstrumentation()
        lateinit var exportador: Exportador
        instrumentacion.runOnMainSync {
            exportador = Exportador(contexto)
            exportador.iniciar(Uri.parse("android.resource://${contexto.packageName}/${R.raw.muestra}"))
        }
        try {
            val fin = System.currentTimeMillis() + 90_000
            var activo = true
            while (activo && System.currentTimeMillis() < fin) {
                Thread.sleep(100)
                instrumentacion.runOnMainSync { activo = exportador.activo }
            }
            assertFalse("La exportación no finalizó", activo)
            lateinit var salida: File
            instrumentacion.runOnMainSync {
                assertEquals("Estado de error del exportador", R.string.export_fin, exportador.estado)
                salida = exportador.resultado!!
            }
            val lector = MediaMetadataRetriever()
            try {
                lector.setDataSource(salida.absolutePath)
                assertEquals("144", lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
                assertNull(lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO))
                assertTrue(lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() > 0)
            } finally { lector.release() }
        } finally { instrumentacion.runOnMainSync { exportador.cancelar() } }
    }
    @Test fun soundPoolCargaAceptaUnStreamYSeLibera() {
        val instrumentacion = InstrumentationRegistry.getInstrumentation()
        lateinit var sonidos: EfectosSonoros
        instrumentacion.runOnMainSync { sonidos = EfectosSonoros(contexto) }
        try {
            val fin = System.currentTimeMillis() + 10_000
            var listo = false
            while (!listo && System.currentTimeMillis() < fin) {
                Thread.sleep(50); instrumentacion.runOnMainSync { listo = sonidos.listo }
            }
            assertTrue(listo)
            instrumentacion.runOnMainSync { assertTrue(sonidos.reproducir()) }
        } finally { instrumentacion.runOnMainSync { sonidos.liberar(); assertFalse(sonidos.reproducir()) } }
    }
}
