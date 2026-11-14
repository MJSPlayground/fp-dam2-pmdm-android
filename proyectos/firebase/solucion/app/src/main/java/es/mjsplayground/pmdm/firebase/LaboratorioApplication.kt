package es.mjsplayground.pmdm.firebase
import android.app.Application
class LaboratorioApplication : Application() { val repo by lazy { NubeRepository(this) } }
