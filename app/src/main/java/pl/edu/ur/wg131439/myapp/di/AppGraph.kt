package pl.edu.ur.wg131439.myapp.di

import android.content.Context
import androidx.room.Room
import pl.edu.ur.wg131439.myapp.data.db.AppDatabase
import pl.edu.ur.wg131439.myapp.data.prefs.UserPrefs
import pl.edu.ur.wg131439.myapp.data.repo.ParkingRepository
import pl.edu.ur.wg131439.myapp.location.LocationClient
import pl.edu.ur.wg131439.myapp.notifications.NotificationHelper
import pl.edu.ur.wg131439.myapp.sensors.LightSensorManager
import pl.edu.ur.wg131439.myapp.sensors.TorchController

object AppGraph {
    private lateinit var appContext: Context

    lateinit var db: AppDatabase
        private set
    lateinit var repo: ParkingRepository
        private set
    lateinit var prefs: UserPrefs
        private set
    lateinit var locationClient: LocationClient
        private set
    lateinit var notificationHelper: NotificationHelper
        private set
    lateinit var lightSensor: LightSensorManager
        private set
    lateinit var torch: TorchController
        private set

    fun init(context: Context) {
        appContext = context.applicationContext

        db = Room.databaseBuilder(appContext, AppDatabase::class.java, "parkfinder.db")
            .fallbackToDestructiveMigration()
            .build()

        prefs = UserPrefs(appContext)
        repo = ParkingRepository(db.parkingDao(), prefs)
        locationClient = LocationClient(appContext)
        notificationHelper = NotificationHelper(appContext)
        lightSensor = LightSensorManager(appContext)
        torch = TorchController(appContext)
    }
}
