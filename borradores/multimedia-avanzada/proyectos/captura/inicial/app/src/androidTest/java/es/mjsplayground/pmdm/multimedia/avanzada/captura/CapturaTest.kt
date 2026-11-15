package es.mjsplayground.pmdm.multimedia.avanzada.captura
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
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
import java.io.FileInputStream
@org.junit.Ignore("Activa al completar la práctica guiada")
@RunWith(AndroidJUnit4::class)
class CapturaTest {
    @get:Rule val compose = createEmptyComposeRule()
    @Test fun capturaImagenYVideoSilencioso() {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val permiso = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ${contexto.packageName} android.permission.CAMERA")
        FileInputStream(permiso.fileDescriptor).use { it.readBytes() }; permiso.close()
        val carpeta = File(contexto.filesDir, "medios")
        val anteriores = carpeta.listFiles()?.map { it.name }?.toSet() ?: emptySet()
        ActivityScenario.launch(MainActivity::class.java).use {
            compose.onNodeWithText("Activar cámara").performClick()
            compose.waitUntil(20_000) { compose.onAllNodesWithText("Cámara lista").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Capturar foto").performScrollTo().performClick()
            compose.waitUntil(30_000) { compose.onAllNodesWithText("Archivo guardado").fetchSemanticsNodes().isNotEmpty() }
            val foto = carpeta.listFiles()!!.single { it.extension == "jpg" && it.name !in anteriores }
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(foto.absolutePath, limites)
            assertTrue(limites.outWidth > 0 && limites.outHeight > 0)
            assertEquals("image/jpeg", limites.outMimeType)
            compose.onNodeWithText("Cambiar a vídeo").performScrollTo().performClick()
            compose.waitUntil(20_000) { compose.onAllNodesWithText("Cámara lista").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithText("Grabar vídeo silencioso").performScrollTo().performClick()
            compose.waitUntil(20_000) { compose.onAllNodesWithText("Grabando vídeo sin audio").fetchSemanticsNodes().isNotEmpty() }
            Thread.sleep(1500)
            compose.onNodeWithText("Detener grabación").performScrollTo().performClick()
            compose.waitUntil(30_000) { compose.onAllNodesWithText("Archivo guardado").fetchSemanticsNodes().isNotEmpty() }
            val video = carpeta.listFiles()!!.single { it.extension == "mp4" && it.name !in anteriores }
            val lector = MediaMetadataRetriever()
            try {
                lector.setDataSource(video.absolutePath)
                assertTrue(lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toLong() > 0)
                assertTrue(lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt() > 0)
                assertNull(lector.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO))
            } finally { lector.release() }
        }
    }
}
