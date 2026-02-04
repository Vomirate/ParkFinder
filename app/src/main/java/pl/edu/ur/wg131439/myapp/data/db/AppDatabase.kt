package pl.edu.ur.wg131439.myapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ParkingEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun parkingDao(): ParkingDao
}
