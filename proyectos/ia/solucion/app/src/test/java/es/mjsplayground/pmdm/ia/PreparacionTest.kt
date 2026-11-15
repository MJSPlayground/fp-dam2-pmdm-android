package es.mjsplayground.pmdm.ia

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreparacionTest {
    @Test fun quitarImpideQueUnaLecturaTardiaRepongaElAdjunto() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = ConsultaViewModel()
            vm.preparar { withContext(NonCancellable) { delay(1000); "anterior" } }
            runCurrent()
            assertTrue(vm.estado.value.preparando)
            vm.quitarImagen()
            advanceUntilIdle()
            assertFalse(vm.estado.value.imagenPreparada)
            assertFalse(vm.estado.value.preparando)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun unaSeleccionNuevaSustituyeLaAnteriorYBloqueaConsultaMientrasPrepara() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val adjunto = AtomicReference<String?>()
            val enviado = CountDownLatch(1)
            val vm = ConsultaViewModel { _, imagen -> adjunto.set(imagen); enviado.countDown(); Respuesta("Prueba", true) }
            vm.preparar { withContext(NonCancellable) { delay(2000); "vieja" } }
            runCurrent()
            vm.consultar("Pregunta de prueba")
            assertEquals(1L, enviado.count)
            vm.preparar { delay(100); "nueva" }
            advanceUntilIdle()
            assertTrue(vm.estado.value.imagenPreparada)
            vm.consultar("Pregunta de prueba")
            runCurrent()
            assertTrue(enviado.await(5, TimeUnit.SECONDS))
            assertEquals("nueva", adjunto.get())
            advanceUntilIdle()
        } finally { Dispatchers.resetMain() }
    }
    @Test fun lecturaFallidaPermiteSeguirSinImagen() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val vm = ConsultaViewModel()
            vm.preparar { throw java.io.IOException("Entrada inaccesible") }
            advanceUntilIdle()
            assertFalse(vm.estado.value.preparando)
            assertFalse(vm.estado.value.imagenPreparada)
            assertEquals(R.string.error_imagen, vm.estado.value.mensaje)
        } finally { Dispatchers.resetMain() }
    }
}
