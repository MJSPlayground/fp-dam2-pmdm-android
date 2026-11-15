package es.mjsplayground.pmdm.segundoplano.estudio

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

fun debeReintentar(intento: Int, fallosSimulados: Int): Boolean = intento < fallosSimulados.coerceIn(0, 3)

class RevisionWorker(contexto: Context, parametros: WorkerParameters) : CoroutineWorker(contexto, parametros) {
    override suspend fun doWork(): Result {
        if (debeReintentar(runAttemptCount, inputData.getInt("fallos", 0))) return Result.retry()
        // Simulación local: ningún dato se transmite a Internet.
        return if (Avisos.mostrar(applicationContext, resumen = true)) Result.success()
            else Result.failure(workDataOf("motivo" to "notificaciones_no_disponibles"))
    }
}

fun crearSolicitud(): OneTimeWorkRequest = OneTimeWorkRequestBuilder<RevisionWorker>()
    .setInitialDelay(10, TimeUnit.SECONDS)
    .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
    .setInputData(workDataOf("fallos" to 1))
    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
    .build()

fun programar(contexto: Context) {
    WorkManager.getInstance(contexto).enqueueUniqueWork("revision", ExistingWorkPolicy.REPLACE, crearSolicitud())
}

class RevisionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACCION) programar(context.applicationContext)
    }
    companion object { const val ACCION = "es.mjsplayground.pmdm.segundoplano.REVISAR" }
}
