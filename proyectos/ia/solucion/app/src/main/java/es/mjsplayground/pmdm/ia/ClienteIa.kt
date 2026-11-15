package es.mjsplayground.pmdm.ia

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.util.Base64
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

fun consultaValida(texto: String): Boolean = texto.trim().length in 3..2000

data class Respuesta(val texto: String, val simulada: Boolean)

/** Solo se invoca fuera del hilo de UI. Destino fijo de laboratorio, sin credenciales cliente. */
class ClienteIa {
    fun generar(texto: String, imagen: String?): Respuesta {
        require(consultaValida(texto))
        val cuerpo = JSONObject().put("pregunta",texto.trim())
        if(imagen != null) cuerpo.put("imagen",imagen)
        val conexion=URL("http://10.0.2.2:8095/generar").openConnection() as HttpURLConnection
        try {
            conexion.requestMethod="POST";conexion.doOutput=true
            conexion.connectTimeout=5000;conexion.readTimeout=25000
            conexion.setRequestProperty("Content-Type","application/json")
            conexion.outputStream.use {it.write(cuerpo.toString().toByteArray(Charsets.UTF_8))}
            val codigo=conexion.responseCode
            if(codigo!=200) throw IOException("HTTP $codigo")
            val respuesta=conexion.inputStream.bufferedReader().use { lector ->
                val texto=StringBuilder()
                val bloque=CharArray(2048)
                while(true) {
                    val cantidad=lector.read(bloque)
                    if(cantidad<0)break
                    if(texto.length+cantidad>20000)throw IOException("Respuesta excesiva")
                    texto.append(bloque,0,cantidad)
                }
                texto.toString()
            }
            val json=JSONObject(respuesta)
            return Respuesta(json.getString("respuesta"),json.getBoolean("simulada"))
        } finally {conexion.disconnect()}
    }
}

/** Recompresión acotada elimina metadatos de la foto; no envía URI ni fichero original. */
fun prepararImagen(context: Context, uri: Uri): String {
    val resolver=context.contentResolver
    val limites=BitmapFactory.Options().apply{inJustDecodeBounds=true}
    resolver.openInputStream(uri)?.use{BitmapFactory.decodeStream(it,null,limites)}
    if(limites.outWidth<=0||limites.outHeight<=0)throw IOException("Formato")
    var muestra=1
    while(limites.outWidth/muestra>1024||limites.outHeight/muestra>1024)muestra*=2
    val original=resolver.openInputStream(uri)?.use{
        BitmapFactory.decodeStream(it,null,BitmapFactory.Options().apply{inSampleSize=muestra})
    }?:throw IOException("Lectura")
    val exif=resolver.openInputStream(uri)?.use{ExifInterface(it)}
    val matriz=Matrix().apply{
        if(exif?.isFlipped==true)postScale(-1f,1f)
        postRotate((exif?.rotationDegrees?:0).toFloat())
    }
    val orientada=Bitmap.createBitmap(original,0,0,original.width,original.height,matriz,true)
    val opaca=Bitmap.createBitmap(orientada.width,orientada.height,Bitmap.Config.ARGB_8888)
    try {
        Canvas(opaca).apply{drawColor(Color.WHITE);drawBitmap(orientada,0f,0f,null)}
        val bytes=ByteArrayOutputStream().use {out->
            if(!opaca.compress(Bitmap.CompressFormat.JPEG,85,out))throw IOException("Compresión")
            out.toByteArray()
        }
        if(bytes.size>512*1024)throw IOException("Imagen excesiva")
        return Base64.encodeToString(bytes,Base64.NO_WRAP)
    } finally {
        opaca.recycle()
        if(orientada!==original)orientada.recycle()
        original.recycle()
    }
}
