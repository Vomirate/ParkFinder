package pl.edu.ur.wg131439.myapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ParkingEntity): Long

    @Query("SELECT * FROM parking ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<ParkingEntity>>

    @Query("SELECT * FROM parking ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): ParkingEntity?

    @Query("DELETE FROM parking WHERE id NOT IN (SELECT id FROM parking ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun keepOnlyLatest(limit: Int)

    @Query("DELETE FROM parking")
    suspend fun clearAll()
}
