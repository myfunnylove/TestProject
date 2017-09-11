package org.main.socforfemale.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import org.main.socforfemale.model.User
import org.main.socforfemale.resources.utils.Prefs
import javax.inject.Singleton

/**
 * Created by Sarvar on 22.08.2017.
 */
@Module
class ContextModule(private val application: Application) {





    @Provides
    @Singleton
    fun context() : Context = application



    @Provides
    @Singleton
    fun getPrefs() : Prefs = Prefs.Builder()


}