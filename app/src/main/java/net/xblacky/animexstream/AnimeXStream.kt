package net.xblacky.animexstream

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.xblacky.animexstream.utils.realm.InitalizeRealm
import timber.log.Timber

@HiltAndroidApp
class AnimeXStream : Application() {

    override fun onCreate() {
        super.onCreate()
        InitalizeRealm.initializeRealm(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }


}