package org.main.socforfemale.base

import android.app.Application
import android.content.Context
import android.content.Intent
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatDelegate
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.drawee.backends.pipeline.Fresco
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKAccessTokenTracker
import com.vk.sdk.VKSdk
import org.main.socforfemale.R
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.LoginActivity
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.core.ImagePipelineConfig
import org.main.socforfemale.connectors.API
import org.main.socforfemale.di.DaggerAppComponent
import org.main.socforfemale.di.modules.ApiModule
import org.main.socforfemale.di.modules.ContextModule
import org.main.socforfemale.di.modules.NetworkModule
import org.main.socforfemale.model.User
import org.main.socforfemale.resources.utils.Prefs
import javax.inject.Inject


/**
 * Created by Michaelan on 5/18/2017.
 */
class Base : Application (){


    @Inject
    lateinit var APIClient:API

    @Inject
    lateinit var context:Context

    @Inject
    lateinit var prefs:Prefs

    val vkAccessTokenTracker = object : VKAccessTokenTracker(){
        override fun onVKAccessTokenChanged(oldToken: VKAccessToken?, newToken: VKAccessToken?) {
            if (newToken == null) {
                log.d("new token is null")
                goLoginActivity()
            }
        }

    }





    private fun goLoginActivity() {
        val intent = Intent(this,LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)

        get = this
        DaggerAppComponent
                .builder()
                .apiModule(ApiModule())
                .contextModule(ContextModule(this))
                .networkModule(NetworkModule())
                .build()
                .inject(this)

        vkAccessTokenTracker.startTracking()
        VKSdk.initialize(context)

        FacebookSdk.sdkInitialize(context)

        AppEventsLogger.activateApp(context)


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)


        Logger.addLogAdapter(AndroidLogAdapter())
        val config = ImagePipelineConfig.newBuilder(context)
                .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(context, config)

        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                        .setDefaultFontPath("font/Quicksand-Regular.otf")
                        .setFontAttrId(R.attr.fontPath)
                        .build())

    }

    companion object {


        lateinit var get: Base
            private set
    }


}