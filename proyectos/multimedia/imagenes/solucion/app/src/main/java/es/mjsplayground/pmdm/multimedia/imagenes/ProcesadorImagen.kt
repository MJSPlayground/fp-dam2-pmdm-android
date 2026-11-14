package es.mjsplayground.pmdm.multimedia.imagenes

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.IOException
import kotlin.math.min
import kotlin.math.roundToInt

enum class Formato(val extension: String, val mime: String) {
    PNG("png", "image/png"), JPEG("jpg", "image/jpeg")
}
data class ResultadoExportacion(
    val uri: Uri, val nombre: String, val mime: String,
    val ancho: Int, val alto: Int, val bytes: Long
)

fun ajustarDimensiones(ancho: Int, alto: Int, maximo: Int = 1024): Pair<Int, Int> {
    require(ancho > 0 && alto > 0 && maximo > 0)
    val factor = min(1.0, min(maximo.toDouble() / ancho, maximo.toDouble() / alto))
    return (ancho * factor).roundToInt().coerceAtLeast(1) to
        (alto * factor).roundToInt().coerceAtLeast(1)
}

/** Ejecutar fuera del hilo principal. Nunca interpreta una URI externa como ruta local. */
class ProcesadorImagen(context: Context) {
    private val app = context.applicationContext
    private val resolver = app.contentResolver
    private fun directorio() = File(app.filesDir, "imagenes").apply { mkdirs() }
    private fun uri(file: File): Uri = FileProvider.getUriForFile(app, app.packageName + ".fileprovider", file)

    fun prepararCaptura(): Uri = uri(File.createTempFile("captura-", ".jpg", directorio()))

    // --8<-- [start:imagen-carga]
    fun cargar(origen: Uri): Bitmap {
        require(origen.scheme == "content" || origen.scheme == "file")
        return if (Build.VERSION.SDK_INT >= 28) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, origen)) { decoder, info, _ ->
                val (ancho, alto) = ajustarDimensiones(info.size.width, info.size.height)
                decoder.setTargetSize(ancho, alto)
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else decodeCompat26(origen)
    }
    // --8<-- [end:imagen-carga]

    /** Pública para comprobar también el algoritmo de API26/27 desde un emulador moderno. */
    fun decodeCompat26(origen: Uri): Bitmap {
        val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        val primeraLectura = resolver.openInputStream(origen) ?: throw IOException("Sin acceso")
        primeraLectura.use { BitmapFactory.decodeStream(it, null, opciones) }
        if (opciones.outWidth <= 0 || opciones.outHeight <= 0) throw IOException("Formato no compatible")
        var muestra = 1
        while (opciones.outWidth / muestra > 1024 || opciones.outHeight / muestra > 1024) {
            muestra *= 2
        }
        val bitmap = resolver.openInputStream(origen)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = muestra })
        } ?: throw IOException("No se pudo decodificar")
        val exif = resolver.openInputStream(origen)?.use { ExifInterface(it) }
        val matriz = Matrix().apply {
            if (exif?.isFlipped == true) postScale(-1f, 1f)
            postRotate((exif?.rotationDegrees ?: 0).toFloat())
        }
        val orientada = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
        if (orientada !== bitmap) bitmap.recycle()
        return orientada
    }

    // --8<-- [start:imagen-transformacion]
    fun rotar90(bitmap: Bitmap): Bitmap = Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, Matrix().apply { postRotate(90f) }, true
    )
    // --8<-- [end:imagen-transformacion]

    // --8<-- [start:imagen-exportacion]
    fun exportar(bitmap: Bitmap, formato: Formato): ResultadoExportacion {
        val destino = File.createTempFile("resultado-", "." + formato.extension, directorio())
        // JPEG no conserva transparencia: se aplana sobre blanco de forma explícita.
        val salida = if (formato == Formato.JPEG && bitmap.hasAlpha()) {
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888).apply {
                Canvas(this).apply { drawColor(Color.WHITE); drawBitmap(bitmap, 0f, 0f, null) }
            }
        } else bitmap
        val tipo = if (formato == Formato.PNG) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        try {
            destino.outputStream().use { if (!salida.compress(tipo, 90, it)) throw IOException("Compresión fallida") }
        } finally {
            if (salida !== bitmap) salida.recycle()
        }
        val verificacion = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(destino.absolutePath, verificacion)
        if (verificacion.outMimeType != formato.mime ||
            verificacion.outWidth != bitmap.width || verificacion.outHeight != bitmap.height) {
            throw IOException("Resultado incompatible")
        }
        return ResultadoExportacion(uri(destino), destino.name, formato.mime,
            verificacion.outWidth, verificacion.outHeight, destino.length())
    }
    // --8<-- [end:imagen-exportacion]
}
