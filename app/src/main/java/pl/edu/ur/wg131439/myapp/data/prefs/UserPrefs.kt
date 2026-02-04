package pl.edu.ur.wg131439.myapp.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPrefs(context: Context) {

    private val dataStore = PreferenceDataStoreFactory.create(
        produceFile = { context.preferencesDataStoreFile("parkfinder_prefs") }
    )

    private val KEY_CURRENT_PARKING_ID = longPreferencesKey("current_parking_id")
    private val KEY_REMINDER_AT = longPreferencesKey("reminder_at")
    private val KEY_TORCH_ENABLED = booleanPreferencesKey("torch_enabled")

    val currentParkingId: Flow<Long?> = dataStore.data.map { prefs ->
        val v = prefs[KEY_CURRENT_PARKING_ID] ?: 0L
        if (v == 0L) null else v
    }

    val reminderAt: Flow<Long?> = dataStore.data.map { prefs ->
        val v = prefs[KEY_REMINDER_AT] ?: 0L
        if (v == 0L) null else v
    }

    val torchEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_TORCH_ENABLED] ?: false
    }

    suspend fun setCurrentParkingId(id: Long?) {
        dataStore.edit { prefs ->
            prefs[KEY_CURRENT_PARKING_ID] = id ?: 0L
        }
    }

    suspend fun setReminderAt(epochMs: Long?) {
        dataStore.edit { prefs ->
            prefs[KEY_REMINDER_AT] = epochMs ?: 0L
        }
    }

    suspend fun setTorchEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_TORCH_ENABLED] = enabled
        }
    }
}
