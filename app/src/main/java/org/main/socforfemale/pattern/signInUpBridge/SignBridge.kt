package org.main.socforfemale.pattern.signInUpBridge

import android.content.Intent

/**
 * Created by Sarvar on 28.08.2017.
 */
class SignBridge(private val socialNetWork: SocialNetwork) :SignBridgeConnector{


    override fun getResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean  = socialNetWork.result(requestCode,resultCode,data)

    override fun initialize():SignBridgeConnector {
        socialNetWork.register()
        return this
    }

    override fun tryAuthorize():SignBridgeConnector {
        socialNetWork.login()
        return this

    }

    override fun onDestroy() {
        socialNetWork.cancel()
    }

}