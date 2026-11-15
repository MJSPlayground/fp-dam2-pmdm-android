package es.mjsplayground.pmdm.firebase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** Solo APK de tests. Java permite ejecutarlo en proceso aislado sin el runtime Kotlin del APK principal. */
public class ImagenDiferidaProvider extends ContentProvider {
    private CountDownLatch puerta = new CountDownLatch(1);
    private volatile byte[] datos = new byte[0];
    private volatile boolean abierta;
    private volatile boolean terminada;
    @Override public boolean onCreate() { return true; }
    @Override public String getType(Uri uri) { return "image/png"; }
    @Override public synchronized Bundle call(String method, String arg, Bundle extras) {
        if ("preparar".equals(method)) {
            puerta = new CountDownLatch(1); datos = extras.getByteArray("bytes"); abierta = false; terminada = false;
        } else if ("liberar".equals(method)) puerta.countDown();
        Bundle estado = new Bundle(); estado.putBoolean("abierta", abierta); estado.putBoolean("terminada", terminada);
        return estado;
    }
    @Override public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if (!"/imagen".equals(uri.getPath()) || !"r".equals(mode)) throw new FileNotFoundException();
        final ParcelFileDescriptor[] tuberia;
        try { tuberia = ParcelFileDescriptor.createPipe(); } catch (java.io.IOException e) { throw new FileNotFoundException(); }
        final CountDownLatch bloqueo = puerta;
        final byte[] imagen = datos.clone();
        abierta = true;
        new Thread(() -> {
            try (ParcelFileDescriptor.AutoCloseOutputStream salida = new ParcelFileDescriptor.AutoCloseOutputStream(tuberia[1])) {
                if (bloqueo.await(20, TimeUnit.SECONDS)) salida.write(imagen);
            } catch (Exception ignorada) {
                // El consumidor puede abandonar la lectura; el proveedor de test no conserva datos.
            } finally { terminada = true; }
        }).start();
        return tuberia[0];
    }
    @Override public Cursor query(Uri uri, String[] projection, String selection, String[] args, String order) { return null; }
    @Override public Uri insert(Uri uri, ContentValues values) { return null; }
    @Override public int delete(Uri uri, String selection, String[] args) { return 0; }
    @Override public int update(Uri uri, ContentValues values, String selection, String[] args) { return 0; }
}
