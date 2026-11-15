package es.mjsplayground.pmdm.sensores

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import es.mjsplayground.pmdm.sensores.ui.PrimeraAplicacionTheme

class MainActivity : FragmentActivity(), SensorEventListener {
    private val sensores by lazy { getSystemService(SensorManager::class.java) }
    private var seleccionado = Sensor.TYPE_ACCELEROMETER
    private var lectura by mutableStateOf("")
    private var sensorDisponible by mutableStateOf(false)
    private var tipoVisible by mutableStateOf(R.string.acelerometro)
    private var aviso by mutableStateOf(R.string.preparado)
    private var autenticado by mutableStateOf(false)
    private var luz = false
    private var idLinterna: String? = null
    private lateinit var prompt: BiometricPrompt
    private val permisoCamara = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) alternarLinterna() else aviso = R.string.permiso_denegado
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // El callback se vuelve a asociar al propietario al recrear la Activity.
        prompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                autenticado = true; aviso = R.string.auth_correcta
            }
            override fun onAuthenticationFailed() { aviso = R.string.auth_no_coincide }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                autenticado = false; aviso = R.string.auth_cancelada
            }
        })
        setContent { PrimeraAplicacionTheme {
            val view = LocalView.current
            Scaffold(Modifier.fillMaxSize()) { margen ->
                Column(Modifier.padding(margen).padding(16.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(stringResource(R.string.app_name))
                    Text(stringResource(R.string.privacidad))
                    val opciones = listOf(
                        Sensor.TYPE_ACCELEROMETER to R.string.acelerometro,
                        Sensor.TYPE_GYROSCOPE to R.string.giroscopio,
                        Sensor.TYPE_LIGHT to R.string.luz,
                        Sensor.TYPE_PRESSURE to R.string.presion,
                        Sensor.TYPE_AMBIENT_TEMPERATURE to R.string.temperatura
                    )
                    opciones.forEach { (tipo, nombre) ->
                        Button(onClick = { tipoVisible = nombre; seleccionar(tipo) }) { Text(stringResource(nombre)) }
                    }
                    Text(stringResource(tipoVisible))
                    Text(if (sensorDisponible) lectura.ifBlank { getString(R.string.esperando) } else stringResource(R.string.sin_sensor))
                    Button(onClick = { view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS) }) { Text(stringResource(R.string.haptica)) }
                    Button(onClick = ::vibrar) { Text(stringResource(R.string.vibrar)) }
                    Button(onClick = {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) alternarLinterna()
                        else permisoCamara.launch(Manifest.permission.CAMERA)
                    }) { Text(stringResource(R.string.linterna)) }
                    Button(onClick = ::autenticar) { Text(stringResource(R.string.autenticar)) }
                    if (autenticado) Text(stringResource(R.string.contenido))
                    Text(stringResource(aviso))
                }
            }
        } }
    }
    private fun seleccionar(tipo: Int) {
        sensores.unregisterListener(this)
        seleccionado = tipo; lectura = ""
        val sensor = sensores.getDefaultSensor(tipo)
        sensorDisponible = sensor != null && sensores.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onResume() { super.onResume(); seleccionar(seleccionado) }
    override fun onPause() {
        sensores.unregisterListener(this)
        // Solo una demostración visual de reautorización; sin datos privados ni claves cifradas.
        autenticado = false
        apagarLinterna()
        super.onPause()
    }
    override fun onSensorChanged(event: SensorEvent) {
        // Copiar antes de conservar: Android reutiliza los arrays de SensorEvent.
        val v = event.values.copyOf()
        lectura = when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.vector_aceleracion, v[0], v[1], v[2], magnitud(v[0],v[1],v[2]))
            Sensor.TYPE_GYROSCOPE -> getString(R.string.vector_giro, radianesAGrados(v[0]), radianesAGrados(v[1]), radianesAGrados(v[2]))
            // TODO sensor-luz: seguir el caso de presión utilizando su recurso y unidad.
            Sensor.TYPE_LIGHT -> getString(R.string.esperando)
            Sensor.TYPE_PRESSURE -> getString(R.string.medida_presion, v[0])
            Sensor.TYPE_AMBIENT_TEMPERATURE -> getString(R.string.medida_temperatura, v[0])
            else -> getString(R.string.sin_sensor)
        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
    private fun vibrar() {
        val vibrador = getSystemService(Vibrator::class.java)
        if (!vibrador.hasVibrator()) { aviso = R.string.sin_vibrador; return }
        vibrador.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE))
        aviso = R.string.vibracion_solicitada
    }
    private fun alternarLinterna() {
        try {
            val gestor = getSystemService(CameraManager::class.java)
            val id = gestor.cameraIdList.firstOrNull {
                gestor.getCameraCharacteristics(it).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (id == null) { aviso = R.string.sin_linterna; return }
            gestor.setTorchMode(id, !luz); idLinterna = id; luz = !luz
            aviso = if (luz) R.string.linterna_encendida else R.string.linterna_apagada
        } catch (_: Exception) { luz = false; aviso = R.string.linterna_error }
    }
    private fun apagarLinterna() {
        idLinterna?.let { id -> runCatching { getSystemService(CameraManager::class.java).setTorchMode(id, false) } }
        luz = false
    }
    private fun autenticar() {
        val permitido = BiometricManager.Authenticators.BIOMETRIC_STRONG
        if (BiometricManager.from(this).canAuthenticate(permitido) != BiometricManager.BIOMETRIC_SUCCESS) {
            aviso = R.string.auth_no_disponible; return
        }
        prompt.authenticate(BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.titulo_biometria))
            .setSubtitle(getString(R.string.subtitulo_biometria))
            .setAllowedAuthenticators(permitido)
            .setNegativeButtonText(getString(R.string.cancelar)).build())
    }
}
