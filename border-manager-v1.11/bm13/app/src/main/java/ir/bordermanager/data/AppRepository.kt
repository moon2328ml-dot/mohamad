package ir.bordermanager.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppRepository(context: Context) {
    val db = DatabaseHelper(context.applicationContext)
    val cloud = CloudSyncManager(context.applicationContext, db)
    private val _revision = MutableStateFlow(0L)
    val revision: StateFlow<Long> = _revision.asStateFlow()

    fun changed() { _revision.value += 1 }

    inline fun <T> write(block: DatabaseHelper.() -> T): T {
        val result = db.block()
        changed()
        cloud.scheduleBackup()
        return result
    }
}
