package pl.edu.ur.wg131439.myapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking")
data class ParkingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val address: String?
)
