package pl.edu.ur.wg131439.myapp.location

import android.content.Context
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ReverseGeocoder {
    suspend fun getAddress(context: Context, lat: Double, lon: Double): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale("pl", "PL"))
            if (Build.VERSION.SDK_INT >= 33) {
                suspendCoroutine { cont ->
                    geocoder.getFromLocation(lat, lon, 1) { list ->
                        cont.resume(list.firstOrNull()?.getAddressLine(0))
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val list = geocoder.getFromLocation(lat, lon, 1)
                list?.firstOrNull()?.getAddressLine(0)
            }
        } catch (_: Exception) {
            null
        }
    }
}
