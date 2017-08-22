package org.main.socforfemale.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import org.main.socforfemale.di.scopes.MVPScope
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Viewer
import javax.inject.Singleton

/**
 * Created by Sarvar on 22.08.2017.
 */
@Module
class MVPModule(private val viewer: Viewer,private val model: Model) {

    @Provides
    fun model() : Model = model

    @Provides
    fun view()  : Viewer = viewer


}