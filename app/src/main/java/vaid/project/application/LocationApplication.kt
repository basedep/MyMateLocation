package vaid.project.application

import android.app.Application
import vaid.project.database.remote.Appwrite

class LocationApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Appwrite.getInstance().initialize(applicationContext)
    }



}