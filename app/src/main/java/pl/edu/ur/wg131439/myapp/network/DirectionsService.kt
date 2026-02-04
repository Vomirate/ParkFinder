package pl.edu.ur.wg131439.myapp.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import pl.edu.ur.wg131439.myapp.util.PolylineDecoder

class DirectionsService(private val apiKey: String) {

    private val client = OkHttpClient()

    suspend fun getRoutePolyline(originLat: Double, originLon: Double, destLat: Double, destLon: Double): List<Pair<Double, Double>>? {
        if (apiKey.isBlank()) return null

        val url = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=$originLat,$originLon&destination=$destLat,$destLon&mode=walking&key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val req = Request.Builder().url(url).get().build()
                client.newCall(req).execute().use { resp ->
                    if (!resp.isSuccessful) return@withContext null
                    val body = resp.body?.string() ?: return@withContext null
                    val json = JSONObject(body)
                    val routes = json.optJSONArray("routes") ?: return@withContext null
                    if (routes.length() == 0) return@withContext null
                    val overview = routes.getJSONObject(0).optJSONObject("overview_polyline") ?: return@withContext null
                    val points = overview.optString("points", "")
                    if (points.isBlank()) return@withContext null
                    PolylineDecoder.decode(points)
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}
