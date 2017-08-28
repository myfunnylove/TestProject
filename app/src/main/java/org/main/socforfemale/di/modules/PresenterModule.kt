package org.main.socforfemale.di.modules

import dagger.Module
import dagger.Provides
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.di.scopes.MVPScope
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer

/**
 * Created by Sarvar on 22.08.2017.
 */
@Module
class PresenterModule {

    @Provides
    fun presenter(viewer: Viewer,model:Model,context:BaseActivity):Presenter = Presenter(viewer,model,context)
}