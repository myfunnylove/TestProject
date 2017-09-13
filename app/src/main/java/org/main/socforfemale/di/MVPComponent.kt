package org.main.socforfemale.di

import dagger.Component
import org.main.socforfemale.di.modules.ErrorConnModule
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.di.scopes.MVPScope
import org.main.socforfemale.ui.activity.*
import org.main.socforfemale.ui.activity.publish.PublishUniversalActivity

/**
 * Created by Sarvar on 22.08.2017.
 */
@MVPScope
@Component(modules = arrayOf(MVPModule::class, PresenterModule::class,ErrorConnModule::class))
interface MVPComponent {

    fun inject(signActivity: SignActivity)
    fun inject(mainActivity: MainActivity)
    fun inject(commentActivity: CommentActivity)
    fun inject(followActivity: FollowActivity)
    fun inject(loginActivity: LoginActivity)
    fun inject(settingsActivity: SettingsActivity)
    fun inject(publishUniversalActivity: PublishUniversalActivity)
    fun inject(loginAndPassActivity: LoginAndPassActivity)
    fun inject(playlistActivity: PlaylistActivity)

}