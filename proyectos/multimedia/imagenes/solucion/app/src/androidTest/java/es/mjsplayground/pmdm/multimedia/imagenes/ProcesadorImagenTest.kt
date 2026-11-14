package es.mjsplayground.pmdm.multimedia.imagenes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.io.File
import kotlin.math.abs
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProcesadorImagenTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val procesador = ProcesadorImagen(context)

    private fun muestra(orientacion: Int = 1): android.net.Uri {
        val carpeta = File(context.filesDir, "imagenes").apply { mkdirs() }
        val archivo = File.createTempFile("prueba-", ".jpg", carpeta)
        val bitmap = Bitmap.createBitmap(100, 60, Bitmap.Config.ARGB_8888)
        for (y in 0 until 60) for (x in 0 until 100) bitmap.setPixel(x, y,
            if (x < 50) { if (y < 30) Color.RED else Color.BLUE }
            else { if (y < 30) Color.GREEN else Color.WHITE })
        archivo.outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)) }
        bitmap.recycle()
        ExifInterface(archivo).apply {
            setAttribute(ExifInterface.TAG_ORIENTATION, orientacion.toString())
            saveAttributes()
        }
        return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", archivo)
    }

    @Test fun ambasRamasRespetanLasOchoOrientacionesExif() {
        for (orientacion in 1..8) {
            val uri = muestra(orientacion)
            val moderna = procesador.cargar(uri)
            val compatible = procesador.decodeCompat26(uri)
            assertEquals("ancho EXIF$orientacion", moderna.width, compatible.width)
            assertEquals("alto EXIF$orientacion", moderna.height, compatible.height)
            for ((fx, fy) in listOf(.25f to .25f, .75f to .25f, .25f to .75f, .75f to .75f)) {
                val a = moderna.getPixel((moderna.width * fx).toInt(), (moderna.height * fy).toInt())
                val b = compatible.getPixel((compatible.width * fx).toInt(), (compatible.height * fy).toInt())
                assertTrue("rojo EXIF$orientacion", abs(Color.red(a) - Color.red(b)) < 15)
                assertTrue("verde EXIF$orientacion", abs(Color.green(a) - Color.green(b)) < 15)
                assertTrue("azul EXIF$orientacion", abs(Color.blue(a) - Color.blue(b)) < 15)
            }
            moderna.recycle(); compatible.recycle()
        }
    }

    @Test fun rotaExportaYCompruebaFormato() {
        val bitmap = procesador.cargar(muestra())
        val rotada = procesador.rotar90(bitmap)
        assertEquals(bitmap.width, rotada.height)
        assertEquals(bitmap.height, rotada.width)
        for (formato in Formato.entries) {
            val resultado = procesador.exportar(rotada, formato)
            assertTrue(resultado.bytes > 0)
            assertEquals(formato.mime, resultado.mime)
            assertEquals(rotada.width, resultado.ancho)
            val cabecera = ByteArray(8)
            context.contentResolver.openInputStream(resultado.uri)!!.use { it.read(cabecera) }
            if (formato == Formato.PNG) assertArrayEquals(byteArrayOf(-119,80,78,71,13,10,26,10), cabecera)
            else { assertEquals(-1, cabecera[0].toInt()); assertEquals(-40, cabecera[1].toInt()) }
            assertTrue(procesador.cargar(resultado.uri).width > 0)
        }
        bitmap.recycle(); rotada.recycle()
    }

    @Test fun fileProviderRechazaFueraDelDirectorioPermitido() {
        assertThrows(IllegalArgumentException::class.java) {
            FileProvider.getUriForFile(context, context.packageName + ".fileprovider", File(context.filesDir, "fuera.txt"))
        }
    }

    @Test fun dimensionesLimitaAntesDeDecodificar() {
        assertEquals(1024 to 512, ajustarDimensiones(6000, 3000))
        assertEquals(100 to 50, ajustarDimensiones(100, 50))
        assertThrows(IllegalArgumentException::class.java) { ajustarDimensiones(0, 2) }
    }
}

