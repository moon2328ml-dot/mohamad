package ir.bordermanager

import android.app.Application
import ir.bordermanager.data.AppRepository

class BorderManagerApp : Application() {
    lateinit var repository: AppRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = AppRepository(this)
    }
}
