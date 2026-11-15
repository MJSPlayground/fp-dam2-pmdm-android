package es.mjsplayground.pmdm.segundoplano.estudio

import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test

class PlanificacionTest {
    @Test fun receiverDelegaTrabajoRealConReintentoYPermiteCancelar() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val contexto = instrumentation.targetContext
        val manager = WorkManager.getInstance(contexto)
        if (Build.VERSION.SDK_INT >= 33) instrumentation.uiAutomation.grantRuntimePermission(
            contexto.packageName, "android.permission.POST_NOTIFICATIONS")
        manager.cancelUniqueWork("revision").result.get(5, TimeUnit.SECONDS)
        val anteriores = manager.getWorkInfosForUniqueWork("revision").get(5, TimeUnit.SECONDS).map { it.id }.toSet()
        try {
            contexto.sendBroadcast(Intent(contexto, RevisionReceiver::class.java).setAction(RevisionReceiver.ACCION))
            val limite = System.currentTimeMillis() + 90_000
            var terminado: WorkInfo? = null
            while (System.currentTimeMillis() < limite && terminado == null) {
                val nuevo = manager.getWorkInfosForUniqueWork("revision").get(5, TimeUnit.SECONDS)
                    .firstOrNull { it.id !in anteriores }
                if (nuevo != null && nuevo.state.isFinished) terminado = nuevo
                else Thread.sleep(500)
            }
            assertNotNull("El receiver debe encolar y finalizar la solicitud real", terminado)
            assertEquals(WorkInfo.State.SUCCEEDED, terminado!!.state)
            assertTrue("Debe producirse el reintento simulado", terminado.runAttemptCount >= 1)
            val avisos = contexto.getSystemService(NotificationManager::class.java)
            val limiteAviso = System.currentTimeMillis() + 5_000
            while (avisos.activeNotifications.none { it.id == Avisos.ID } && System.currentTimeMillis() < limiteAviso) Thread.sleep(50)
            assertTrue(avisos.activeNotifications.any { it.id == Avisos.ID })
            programar(contexto)
            manager.cancelUniqueWork("revision").result.get(5, TimeUnit.SECONDS)
            assertTrue(manager.getWorkInfosForUniqueWork("revision").get(5, TimeUnit.SECONDS)
                .any { it.state == WorkInfo.State.CANCELLED })
        } finally {
            manager.cancelUniqueWork("revision").result.get(5, TimeUnit.SECONDS)
            contexto.getSystemService(NotificationManager::class.java).cancel(Avisos.ID)
        }
    }
}
