package pl.edu.ur.wg131439.myapp

import android.app.Application
import pl.edu.ur.wg131439.myapp.di.AppGraph

class ParkFinderApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
