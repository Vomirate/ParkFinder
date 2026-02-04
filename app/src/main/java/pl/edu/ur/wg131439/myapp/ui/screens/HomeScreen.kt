package pl.edu.ur.wg131439.myapp.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import pl.edu.ur.wg131439.myapp.R
import pl.edu.ur.wg131439.myapp.ui.MainViewModel
import pl.edu.ur.wg131439.myapp.util.TimeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: MainViewModel,
    onShowMap: () -> Unit,
    onHistory: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val latest by vm.latest.collectAsStateWithLifecycle()
    val reminderAt by vm.reminderAt.collectAsStateWithLifecycle()
    val lux by vm.lux.collectAsStateWithLifecycle()

    var torchEnabled by remember { mutableStateOf(false) }

    var reminderMenuOpen by remember { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { /* result map ignored */ }

    fun requestBasics() {
        val perms = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
        if (Build.VERSION.SDK_INT >= 33) {
            perms += Manifest.permission.POST_NOTIFICATIONS
        }
        permissionsLauncher.launch(perms.toTypedArray())
    }

    LaunchedEffect(Unit) {
        // Ask once on first open (users can deny)
        requestBasics()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.title_home)) }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (latest == null) {
                        Text(stringResource(R.string.no_car_saved), style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Text(
                            text = latest?.address ?: stringResource(R.string.unknown_address),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${stringResource(R.string.saved_at)}: ${TimeFormat.formatDateTime(latest!!.timestamp)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (!vm.hasLocationPermission()) {
                                    requestBasics()
                                    scope.launch { snackbar.showSnackbar("Brak uprawnień do lokalizacji.") }
                                    return@Button
                                }
                                vm.parkHere(context) { ok ->
                                    scope.launch {
                                        snackbar.showSnackbar(
                                            if (ok) context.getString(R.string.car_saved) else "Nie udało się pobrać lokalizacji."
                                        )
                                    }
                                }
                            }
                        ) { Text(stringResource(R.string.btn_park_here)) }

                        Button(
                            onClick = {
                                if (latest == null) {
                                    scope.launch { snackbar.showSnackbar("Najpierw zapisz lokalizację auta.") }
                                } else onShowMap()
                            }
                        ) { Text(stringResource(R.string.btn_show_car)) }
                    }

                    Button(onClick = onHistory, modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.btn_history))
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.timer_label), style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val label = reminderAt?.let { "Aktywne do: ${TimeFormat.formatTime(it)}" } ?: "Brak"
                        Text(label)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(onClick = { reminderMenuOpen = true }) {
                                Text("Ustaw")
                            }
                            DropdownMenu(expanded = reminderMenuOpen, onDismissRequest = { reminderMenuOpen = false }) {
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.timer_30m)) },
                                    onClick = {
                                        reminderMenuOpen = false
                                        vm.scheduleReminder(context, 30)
                                        scope.launch { snackbar.showSnackbar(context.getString(R.string.reminder_set)) }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.timer_1h)) },
                                    onClick = {
                                        reminderMenuOpen = false
                                        vm.scheduleReminder(context, 60)
                                        scope.launch { snackbar.showSnackbar(context.getString(R.string.reminder_set)) }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(stringResource(R.string.timer_2h)) },
                                    onClick = {
                                        reminderMenuOpen = false
                                        vm.scheduleReminder(context, 120)
                                        scope.launch { snackbar.showSnackbar(context.getString(R.string.reminder_set)) }
                                    }
                                )
                            }
                            Spacer(Modifier.height(0.dp))
                        }
                    }

                    if (reminderAt != null) {
                        Button(
                            onClick = {
                                vm.cancelReminder(context)
                                scope.launch { snackbar.showSnackbar(context.getString(R.string.reminder_cancelled)) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Usuń przypomnienie") }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tryb nocny / latarka", style = MaterialTheme.typography.titleMedium)

                    val lowLight = (lux ?: 9999f) < 10f

                    if (lowLight) {
                        Text(stringResource(R.string.light_low_hint), style = MaterialTheme.typography.bodyMedium)
                    } else {
                        Text("Jasność: ${lux?.toInt() ?: 0} lx", style = MaterialTheme.typography.bodyMedium)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (torchEnabled) stringResource(R.string.torch_on) else stringResource(R.string.torch_off))
                        Switch(
                            checked = torchEnabled,
                            onCheckedChange = {
                                torchEnabled = it
                                vm.setTorch(it)
                            }
                        )
                    }
                }
            }
        }
    }
}
