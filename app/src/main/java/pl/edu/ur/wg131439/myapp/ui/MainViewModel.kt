package pl.edu.ur.wg131439.myapp.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.edu.ur.wg131439.myapp.BuildConfig
import pl.edu.ur.wg131439.myapp.data.db.ParkingEntity
import pl.edu.ur.wg131439.myapp.di.AppGraph
import pl.edu.ur.wg131439.myapp.location.ReverseGeocoder
import pl.edu.ur.wg131439.myapp.network.DirectionsService
import pl.edu.ur.wg131439.myapp.notifications.ParkingReminderWorker
import pl.edu.ur.wg131439.myapp.notifications.ReminderActionsReceiver
import java.util.concurrent.TimeUnit

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppGraph.repo
    private val locationClient = AppGraph.locationClient
    private val lightSensor = AppGraph.lightSensor
    private val directions = DirectionsService(BuildConfig.MAPS_API_KEY)

    val history: StateFlow<List<ParkingEntity>> =
        repo.observeHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latest: StateFlow<ParkingEntity?> =
        repo.observeHistory()
            .map { it.firstOrNull() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val reminderAt: StateFlow<Long?> =
        repo.observeReminderAt().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val lux: StateFlow<Float?> =
        lightSensor.lux.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _route = MutableStateFlow<List<LatLng>>(emptyList())
    val route: StateFlow<List<LatLng>> = _route

    init {
        lightSensor.start()
    }

    override fun onCleared() {
        super.onCleared()
        lightSensor.stop()
    }

    fun hasLocationPermission(): Boolean = locationClient.hasLocationPermission()

    fun parkHere(context: Context, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val loc = locationClient.getCurrentLocation()
            if (loc == null) {
                onDone(false)
                return@launch
            }
            val address = ReverseGeocoder.getAddress(context, loc.latitude, loc.longitude)
            val entity = ParkingEntity(
                timestamp = System.currentTimeMillis(),
                latitude = loc.latitude,
                longitude = loc.longitude,
                address = address
            )
            repo.saveParking(entity)
            onDone(true)
        }
    }

    fun scheduleReminder(context: Context, delayMinutes: Long) {
        val req = OneTimeWorkRequestBuilder<ParkingReminderWorker>()
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            ReminderActionsReceiver.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            req
        )

        viewModelScope.launch {
            repo.setReminderAt(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(delayMinutes))
        }
    }

    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ReminderActionsReceiver.WORK_NAME)
        AppGraph.notificationHelper.cancelReminderNotification()
        viewModelScope.launch { repo.setReminderAt(null) }
    }

    fun setTorch(enabled: Boolean) {
        AppGraph.torch.setTorch(enabled)
    }

    fun buildRoute(context: Context, onReady: (List<LatLng>) -> Unit) {
        viewModelScope.launch {
            val car = repo.getLatest()
            if (car == null) {
                _route.value = emptyList()
                onReady(emptyList())
                return@launch
            }
            val userLoc = locationClient.getCurrentLocation()
            if (userLoc == null) {
                _route.value = emptyList()
                onReady(emptyList())
                return@launch
            }

            val routePairs = directions.getRoutePolyline(
                userLoc.latitude, userLoc.longitude,
                car.latitude, car.longitude
            )

            val points = if (!routePairs.isNullOrEmpty()) {
                routePairs.map { LatLng(it.first, it.second) }
            } else {
                // fallback: straight line
                listOf(
                    LatLng(userLoc.latitude, userLoc.longitude),
                    LatLng(car.latitude, car.longitude)
                )
            }
            _route.value = points
            onReady(points)
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(app) as T
        }
    }
}
