package es.mjsplayground.pmdm.redes

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import java.io.InputStream
import java.util.UUID
import java.util.concurrent.Executors

/** Laboratorio acotado: permisos comprobados por Activity y revocaciones capturadas aquí.
 * Nunca empieza una escucha, exploración o conexión durante la construcción. */
@SuppressLint("MissingPermission")
class Enlaces(contexto: Context, private val estado: (String) -> Unit,
    private val encontrado: (BluetoothDevice) -> Unit) {
    private val app = contexto.applicationContext
    private val gestor = app.getSystemService(BluetoothManager::class.java)
    private val adaptador get() = gestor?.adapter
    private val principal = Handler(Looper.getMainLooper())
    private val tareas = Executors.newSingleThreadExecutor()
    @Volatile private var generacion = 0
    private var socket: BluetoothSocket? = null
    private var servidorClasico: BluetoothServerSocket? = null
    private var escaneo: ScanCallback? = null
    private var publicidad: AdvertiseCallback? = null
    private var servidorGatt: BluetoothGattServer? = null
    private var valorServidor = "HOLA"
    private var gatt: BluetoothGatt? = null
    private var caracteristica: BluetoothGattCharacteristic? = null
    @Volatile private var operacionGatt = false
    private var numeroOperacion = 0

    companion object {
        val SERVICIO: UUID = UUID.fromString("7c531d56-88c5-43de-807b-15a7dca42670")
        val DATO: UUID = UUID.fromString("7c531d56-88c5-43de-807b-15a7dca42671")
    }

    private fun comunicar(recurso: Int, token: Int = generacion, vararg datos: Any) {
        principal.post { if (token == generacion) estado(app.getString(recurso, *datos)) }
    }

    fun vinculados(): List<BluetoothDevice> = try {
        adaptador?.bondedDevices?.toList().orEmpty()
    } catch (_: SecurityException) { comunicar(R.string.sin_permiso); emptyList() }

    fun etiqueta(dispositivo: BluetoothDevice, indice: Int): String = try {
        dispositivo.name?.takeIf { it.isNotBlank() } ?: app.getString(R.string.elegir, indice)
    } catch (_: SecurityException) { app.getString(R.string.elegir, indice) }

    fun disponible(): Boolean = try { adaptador?.isEnabled == true }
        catch (_: SecurityException) { false }

    fun escucharClasico() {
        cerrar()
        val token = generacion
        comunicar(R.string.esperando_clasico, token)
        tareas.execute {
            if (token != generacion) return@execute
            try {
                val servidor = adaptador?.listenUsingRfcommWithServiceRecord("PMDM-Laboratorio", SERVICIO)
                    ?: error("Sin adaptador")
                synchronized(this@Enlaces) {
                    if (token != generacion) { servidor.close(); return@execute }
                    servidorClasico = servidor
                }
                val recibido = servidor.accept(15000)
                synchronized(this@Enlaces) {
                    if (token != generacion) { recibido.close(); return@execute }
                    socket = recibido
                }
                servidor.close(); servidorClasico = null
                principal.postDelayed({ if (token == generacion) try { recibido.close() } catch (_: Exception) {} }, 10000)
                recibido.use {
                    val entrada = leerTrama(it.inputStream)
                    val respuesta = Protocolo.responder(entrada)
                    it.outputStream.write((respuesta + "\n").toByteArray(Charsets.UTF_8))
                    comunicar(R.string.respuesta_clasica, token, respuesta)
                }
            } catch (_: Exception) { comunicar(R.string.error_enlace, token) }
            finally { if (token == generacion) { try { servidorClasico?.close() } catch (_: Exception) {}; servidorClasico = null; socket = null } }
        }
    }

    fun enviarClasico(dispositivo: BluetoothDevice, mensaje: String) {
        cerrar()
        val token = generacion
        comunicar(R.string.conectando, token)
        tareas.execute {
            if (token != generacion) return@execute
            try {
                val actual = dispositivo.createRfcommSocketToServiceRecord(SERVICIO)
                synchronized(this@Enlaces) {
                    if (token != generacion) { actual.close(); return@execute }
                    socket = actual
                }
                principal.postDelayed({ if (token == generacion) try { actual.close() } catch (_: Exception) {} }, 15000)
                actual.use {
                    it.connect()
                    it.outputStream.write((mensaje + "\n").toByteArray(Charsets.UTF_8))
                    val respuesta = leerTrama(it.inputStream).toString(Charsets.UTF_8)
                    comunicar(R.string.respuesta_clasica, token, respuesta)
                }
            } catch (_: Exception) { comunicar(R.string.error_enlace, token) }
            finally { if (token == generacion) socket = null }
        }
    }

    private fun leerTrama(entrada: InputStream): ByteArray {
        val datos = ArrayList<Byte>()
        while (true) {
            val siguiente = entrada.read()
            if (siguiente < 0 || siguiente == 10) break
            require(datos.size < 20) { "Trama excesiva" }
            datos.add(siguiente.toByte())
        }
        return datos.toByteArray()
    }

    fun explorar() {
        cerrar()
        val token = generacion
        try {
            val scanner = adaptador?.bluetoothLeScanner ?: error("Sin explorador")
            val callback = object : ScanCallback() {
                override fun onScanResult(callbackType: Int, result: ScanResult) {
                    principal.post { if (token == generacion) encontrado(result.device) }
                }
                override fun onScanFailed(errorCode: Int) { comunicar(R.string.error_enlace, token) }
            }
            escaneo = callback
            scanner.startScan(listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(SERVICIO)).build()),
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build(), callback)
            comunicar(R.string.explorando, token)
            principal.postDelayed({
                if (token == generacion) {
                    try { scanner.stopScan(callback) } catch (_: SecurityException) {}
                    escaneo = null
                    comunicar(R.string.exploracion_fin, token)
                }
            }, 10000)
        } catch (_: Exception) { comunicar(R.string.error_enlace, token) }
    }

    fun conectarBle(dispositivo: BluetoothDevice) {
        cerrar()
        val token = generacion
        comunicar(R.string.conectando, token)
        try {
            val callback = object : BluetoothGattCallback() {
                override fun onConnectionStateChange(actual: BluetoothGatt, status: Int, newState: Int) {
                    if (token != generacion) { actual.close(); return }
                    if (status != BluetoothGatt.GATT_SUCCESS || newState == BluetoothProfile.STATE_DISCONNECTED) {
                        actual.close(); gatt = null; caracteristica = null; operacionGatt = false
                        comunicar(R.string.desconectado, token)
                    } else if (newState == BluetoothProfile.STATE_CONNECTED) {
                        try { if (!actual.discoverServices()) comunicar(R.string.error_enlace, token) }
                        catch (_: SecurityException) { comunicar(R.string.sin_permiso, token) }
                    }
                }
                override fun onServicesDiscovered(actual: BluetoothGatt, status: Int) {
                    if (token != generacion) return
                    caracteristica = if (status == BluetoothGatt.GATT_SUCCESS) actual.getService(SERVICIO)?.getCharacteristic(DATO) else null
                    comunicar(if (caracteristica == null) R.string.error_enlace else R.string.ble_listo, token)
                }
                override fun onCharacteristicRead(actual: BluetoothGatt, dato: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
                    terminarLectura(value, status, token)
                }
                @Deprecated("Callback necesario en API 26-32")
                override fun onCharacteristicRead(actual: BluetoothGatt, dato: BluetoothGattCharacteristic, status: Int) {
                    if (Build.VERSION.SDK_INT < 33) terminarLectura(dato.value ?: byteArrayOf(), status, token)
                }
                override fun onCharacteristicWrite(actual: BluetoothGatt, dato: BluetoothGattCharacteristic, status: Int) {
                    if (token == generacion) { operacionGatt = false; comunicar(if (status == BluetoothGatt.GATT_SUCCESS) R.string.escritura_lista else R.string.error_enlace, token) }
                }
            }
            gatt = dispositivo.connectGatt(app, false, callback, BluetoothDevice.TRANSPORT_LE)
            principal.postDelayed({ if (token == generacion && caracteristica == null) { cerrar(); comunicar(R.string.error_enlace) } }, 15000)
        } catch (_: SecurityException) { comunicar(R.string.sin_permiso, token) }
    }

    private fun terminarLectura(datos: ByteArray, status: Int, token: Int) {
        if (token != generacion) return
        operacionGatt = false
        if (status == BluetoothGatt.GATT_SUCCESS && datos.size <= 20)
            comunicar(R.string.lectura_lista, token, datos.toString(Charsets.UTF_8))
        else comunicar(R.string.error_enlace, token)
    }

    fun leerBle() {
        try {
            val actual = gatt ?: return comunicar(R.string.sin_gatt)
            val dato = caracteristica ?: return comunicar(R.string.sin_gatt)
            if (operacionGatt) return comunicar(R.string.operacion_pendiente)
            operacionGatt = true
            if (!actual.readCharacteristic(dato)) { operacionGatt = false; comunicar(R.string.error_enlace) }
            else limitarOperacion()
        } catch (_: SecurityException) { operacionGatt = false; comunicar(R.string.sin_permiso) }
    }

    fun escribirBle(mensaje: String) {
        try {
            val actual = gatt ?: return comunicar(R.string.sin_gatt)
            val dato = caracteristica ?: return comunicar(R.string.sin_gatt)
            if (operacionGatt) return comunicar(R.string.operacion_pendiente)
            operacionGatt = true
            // TODO redes-escritura: inicia la escritura con respuesta según API y conserva el resultado de aceptación.
            val aceptada = false
            if (!aceptada) { operacionGatt = false; comunicar(R.string.error_enlace) }
            else limitarOperacion()
        } catch (_: SecurityException) { operacionGatt = false; comunicar(R.string.sin_permiso) }
    }

    private fun limitarOperacion() {
        val token = generacion
        val numero = ++numeroOperacion
        principal.postDelayed({
            if (token == generacion && numero == numeroOperacion && operacionGatt) {
                cerrar(); comunicar(R.string.error_enlace)
            }
        }, 8000)
    }

    fun anunciar() {
        cerrar()
        val token = generacion
        try {
            val adaptadorActual = adaptador ?: error("Sin adaptador")
            if (!adaptadorActual.isMultipleAdvertisementSupported) { comunicar(R.string.sin_periferico); return }
            val anunciante = adaptadorActual.bluetoothLeAdvertiser ?: error("Sin anunciante")
            val respuestaPublicidad = object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings) { comunicar(R.string.anunciando, token) }
                override fun onStartFailure(errorCode: Int) { comunicar(R.string.error_enlace, token) }
            }
            publicidad = respuestaPublicidad
            valorServidor = "HOLA"
            val callbacks = object : BluetoothGattServerCallback() {
                override fun onServiceAdded(status: Int, service: BluetoothGattService) {
                    if (token != generacion) return
                    if (status != BluetoothGatt.GATT_SUCCESS) { comunicar(R.string.error_enlace, token); return }
                    try {
                        anunciante.startAdvertising(AdvertiseSettings.Builder().setConnectable(true)
                            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER).setTimeout(60000).build(),
                            AdvertiseData.Builder().addServiceUuid(ParcelUuid(SERVICIO)).setIncludeDeviceName(false).build(), respuestaPublicidad)
                    } catch (_: SecurityException) { comunicar(R.string.sin_permiso, token) }
                }
                override fun onCharacteristicReadRequest(device: BluetoothDevice, requestId: Int, offset: Int, characteristic: BluetoothGattCharacteristic) {
                    if (token != generacion) return
                    try {
                        val bytes = valorServidor.toByteArray(Charsets.UTF_8)
                        val valido = characteristic.uuid == DATO && offset in 0..bytes.size
                        servidorGatt?.sendResponse(device, requestId, if (valido) BluetoothGatt.GATT_SUCCESS else BluetoothGatt.GATT_INVALID_OFFSET,
                            offset, if (valido) bytes.copyOfRange(offset, bytes.size) else null)
                    } catch (_: SecurityException) { comunicar(R.string.sin_permiso, token) }
                }
                override fun onCharacteristicWriteRequest(device: BluetoothDevice, requestId: Int, characteristic: BluetoothGattCharacteristic,
                    preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray) {
                    if (token != generacion) return
                    try {
                        val valido = characteristic.uuid == DATO && !preparedWrite && offset == 0 && value.size <= 20
                        if (valido) valorServidor = Protocolo.responder(value)
                        if (responseNeeded) servidorGatt?.sendResponse(device, requestId,
                            if (valido) BluetoothGatt.GATT_SUCCESS else BluetoothGatt.GATT_REQUEST_NOT_SUPPORTED, 0, null)
                        if (valido) comunicar(R.string.servidor_actualizado, token)
                    } catch (_: SecurityException) { comunicar(R.string.sin_permiso, token) }
                }
            }
            servidorGatt = gestor?.openGattServer(app, callbacks) ?: error("Sin servidor")
            val servicio = BluetoothGattService(SERVICIO, BluetoothGattService.SERVICE_TYPE_PRIMARY)
            servicio.addCharacteristic(BluetoothGattCharacteristic(DATO,
                BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE))
            check(servidorGatt?.addService(servicio) == true)
            principal.postDelayed({ if (token == generacion) { cerrar(); comunicar(R.string.detenido) } }, 60000)
        } catch (_: Exception) { comunicar(R.string.error_enlace, token) }
    }

    @Synchronized fun cerrar() {
        generacion++
        principal.removeCallbacksAndMessages(null)
        try { socket?.close() } catch (_: Exception) {}
        try { servidorClasico?.close() } catch (_: Exception) {}
        try { escaneo?.let { adaptador?.bluetoothLeScanner?.stopScan(it) } } catch (_: SecurityException) {}
        try { publicidad?.let { adaptador?.bluetoothLeAdvertiser?.stopAdvertising(it) } } catch (_: SecurityException) {}
        try { gatt?.disconnect(); gatt?.close() } catch (_: SecurityException) {}
        try { servidorGatt?.close() } catch (_: SecurityException) {}
        socket = null; servidorClasico = null; escaneo = null; publicidad = null
        gatt = null; caracteristica = null; servidorGatt = null; operacionGatt = false
    }

    fun destruir() { cerrar(); tareas.shutdownNow() }
}
