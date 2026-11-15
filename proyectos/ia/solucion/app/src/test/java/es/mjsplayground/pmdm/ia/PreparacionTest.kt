package es.mjsplayground.pmdm.ia

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
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
            val enviados = AtomicInteger()
            val vm = ConsultaViewModel { _, imagen -> adjunto.set(imagen); enviados.incrementAndGet(); Respuesta("Prueba", true) }
            vm.preparar { withContext(NonCancellable) { delay(2000); "vieja" } }
            runCurrent()
            vm.consultar("Pregunta de prueba")
            assertEquals(0, enviados.get())
            vm.preparar { delay(100); "nueva" }
            advanceUntilIdle()
            assertTrue(vm.estado.value.imagenPreparada)
            vm.consultar("Pregunta de prueba")
            // Esperar también el retorno IO -> Main antes de restablecer Main.
            // Un efecto dentro de generar() todavía no confirma ese retorno.
            vm.estado.first { !it.cargando && it.respuesta == "Prueba" }
            assertEquals(1, enviados.get())
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
