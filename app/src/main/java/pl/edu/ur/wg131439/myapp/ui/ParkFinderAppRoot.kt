package pl.edu.ur.wg131439.myapp.ui

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.edu.ur.wg131439.myapp.ui.screens.HistoryScreen
import pl.edu.ur.wg131439.myapp.ui.screens.HomeScreen
import pl.edu.ur.wg131439.myapp.ui.screens.MapScreen

@Composable
fun ParkFinderAppRoot() {
    val nav = rememberNavController()
    val app = LocalContext.current.applicationContext as Application
    val vm: MainViewModel = viewModel(factory = MainViewModel.Factory(app))

    NavHost(navController = nav, startDestination = "home") {
        composable("home") {
            HomeScreen(
                vm = vm,
                onShowMap = { nav.navigate("map") },
                onHistory = { nav.navigate("history") }
            )
        }
        composable("map") { MapScreen(vm = vm, onBack = { nav.popBackStack() }) }
        composable("history") { HistoryScreen(vm = vm, onBack = { nav.popBackStack() }) }
    }
}
