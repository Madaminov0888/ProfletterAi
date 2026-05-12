package com.example.profletterai

import android.app.Application
import com.example.profletterai.di.ServiceLocator

/**
 * Holds the single [ServiceLocator] for the process so ViewModels and
 * composables can fetch dependencies without dragging a DI framework in.
 */
class ProfletterApp : Application() {

    lateinit var locator: ServiceLocator
        private set

    override fun onCreate() {
        super.onCreate()
        locator = ServiceLocator(this)
        instance = this
    }

    companion object {
        @Volatile private var instance: ProfletterApp? = null
        val locator: ServiceLocator
            get() = requireNotNull(instance) {
                "ProfletterApp not initialised yet — declare android:name=\".ProfletterApp\""
            }.locator
    }
}
