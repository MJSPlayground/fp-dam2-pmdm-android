package es.mjsplayground.pmdm.segundoplano.estudio

import android.app.NotificationManager
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class RevisionWorkerTest {
    @Test fun reintentoYNotificacionFinal() = runBlocking {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val contexto = instrumentation.targetContext
        if (Build.VERSION.SDK_INT >= 33) {
            instrumentation.uiAutomation.grantRuntimePermission(contexto.packageName, "android.permission.POST_NOTIFICATIONS")
        }
        val primero = TestListenableWorkerBuilder<RevisionWorker>(contexto)
            .setInputData(workDataOf("fallos" to 1)).setRunAttemptCount(0).build()
        assertEquals(ListenableWorker.Result.retry(), primero.doWork())
        val segundo = TestListenableWorkerBuilder<RevisionWorker>(contexto)
            .setInputData(workDataOf("fallos" to 1)).setRunAttemptCount(1).build()
        assertEquals(ListenableWorker.Result.success(), segundo.doWork())
        val manager = contexto.getSystemService(NotificationManager::class.java)
        assertTrue(manager.activeNotifications.any { it.id == Avisos.ID })
        manager.cancel(Avisos.ID)
    }
}
