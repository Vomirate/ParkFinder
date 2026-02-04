package pl.edu.ur.wg131439.myapp.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import pl.edu.ur.wg131439.myapp.R
import pl.edu.ur.wg131439.myapp.ui.MainViewModel
import pl.edu.ur.wg131439.myapp.util.rememberMapViewWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(vm: MainViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val latest by vm.latest.collectAsStateWithLifecycle()
    val route by vm.route.collectAsStateWithLifecycle()

    val mapView: MapView = rememberMapViewWithLifecycle()

    LaunchedEffect(latest) {
        if (latest != null) {
            vm.buildRoute(context) { /* route state updated */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.title_map)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth().weight(1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { mapView },
                    modifier = Modifier.fillMaxSize(),
                    update = { mv ->
                        mv.getMapAsync { googleMap ->
                            setupMap(googleMap, latest, route)
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val car = latest ?: return@Button
                    val uri = Uri.parse("geo:${car.latitude},${car.longitude}?q=${car.latitude},${car.longitude}(Auto)")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(context.getString(R.string.open_google_maps))
            }

            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Text("Wróć")
            }
        }
    }
}

private fun setupMap(map: GoogleMap, latest: pl.edu.ur.wg131439.myapp.data.db.ParkingEntity?, route: List<LatLng>) {
    map.uiSettings.isZoomControlsEnabled = true
    map.clear()

    if (latest == null) return

    val carPos = LatLng(latest.latitude, latest.longitude)
    map.addMarker(MarkerOptions().position(carPos).title("Auto"))

    if (route.isNotEmpty()) {
        map.addPolyline(PolylineOptions().addAll(route).width(12f))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.first(), 16f))
    } else {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(carPos, 16f))
    }
}
