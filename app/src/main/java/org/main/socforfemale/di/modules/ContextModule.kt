package org.main.socforfemale.di.modules

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by Sarvar on 22.08.2017.
 */
@Module
class ContextModule(private val application: Application) {





    @Provides
    @Singleton
    fun context() : Context = application



}