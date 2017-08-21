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
import com.orhanobut.logger.Logger.addLogAdapter
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig
import com.facebook.imagepipeline.core.ImagePipelineConfig







/**
 * Created by Michaelan on 5/18/2017.
 */
class Base : Application (){

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

        startActivity(intent);
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        MultiDex.install(instance)
        val config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build()
        Fresco.initialize(this, config)
        vkAccessTokenTracker.startTracking()

        VKSdk.initialize(instance)

        FacebookSdk.sdkInitialize(instance)

        AppEventsLogger.activateApp(this)

        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                                 .setDefaultFontPath("font/Quicksand-Regular.otf")
                                 .setFontAttrId(R.attr.fontPath)
                                 .build()
                                     )


        Logger.addLogAdapter(AndroidLogAdapter())
    }

    companion object {
        lateinit var instance: Base
            private set
    }


}