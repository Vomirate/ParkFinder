package pl.edu.ur.wg131439.myapp.data.repo

import kotlinx.coroutines.flow.Flow
import pl.edu.ur.wg131439.myapp.data.db.ParkingDao
import pl.edu.ur.wg131439.myapp.data.db.ParkingEntity
import pl.edu.ur.wg131439.myapp.data.prefs.UserPrefs

class ParkingRepository(
    private val dao: ParkingDao,
    private val prefs: UserPrefs
) {
    fun observeHistory(): Flow<List<ParkingEntity>> = dao.observeAll()

    suspend fun saveParking(entity: ParkingEntity): Long {
        val id = dao.insert(entity)
        dao.keepOnlyLatest(3)
        prefs.setCurrentParkingId(id)
        return id
    }

    suspend fun getLatest(): ParkingEntity? = dao.getLatest()

    suspend fun clearCurrentParking() {
        prefs.setCurrentParkingId(null)
    }

    fun observeCurrentParkingId() = prefs.currentParkingId
    fun observeReminderAt() = prefs.reminderAt

    suspend fun setReminderAt(epochMs: Long?) = prefs.setReminderAt(epochMs)
}
