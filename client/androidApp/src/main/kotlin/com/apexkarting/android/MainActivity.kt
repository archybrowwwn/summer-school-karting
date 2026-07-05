package com.apexkarting.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apexkarting.ApexApp
import com.apexkarting.core.storage.PlatformSessionStorage
import com.apexkarting.di.initKoin
import com.apexkarting.map.PlatformMapLauncher
import timber.log.Timber

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }
        PlatformSessionStorage.initialize(applicationContext)
        PlatformMapLauncher.initialize(applicationContext)
        initKoin()
        setContent {
            ApexApp()
        }
    }
}
