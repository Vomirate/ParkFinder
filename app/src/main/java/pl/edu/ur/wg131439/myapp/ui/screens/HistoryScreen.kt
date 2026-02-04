package pl.edu.ur.wg131439.myapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.edu.ur.wg131439.myapp.R
import pl.edu.ur.wg131439.myapp.ui.MainViewModel
import pl.edu.ur.wg131439.myapp.util.TimeFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(vm: MainViewModel, onBack: () -> Unit) {
    val history by vm.history.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Historia parkowań") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            if (history.isEmpty()) {
                Text("Brak historii.", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(item.address ?: "Nieznany adres", style = MaterialTheme.typography.titleMedium)
                            Text(TimeFormat.formatDateTime(item.timestamp), style = MaterialTheme.typography.bodyMedium)
                            Text("(${item.latitude}, ${item.longitude})", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
