package org.main.socforfemale.di

import dagger.Component
import org.main.socforfemale.base.Base
import org.main.socforfemale.di.modules.ApiModule
import org.main.socforfemale.di.modules.ContextModule
import org.main.socforfemale.di.modules.NetworkModule
import javax.inject.Singleton

/**
 * Created by Sarvar on 22.08.2017.
 */
@Singleton
@Component(modules = arrayOf(ContextModule::class,NetworkModule::class,ApiModule::class))
interface AppComponent {

    fun inject (base: Base)


}