package org.main.socforfemale.pattern.signInUp

import android.content.Intent

/**
 * Created by Sarvar on 28.08.2017.
 */
interface SignBridgeConnector {

    fun initialize():SignBridgeConnector
    fun tryAuthorize():SignBridgeConnector
    fun getResult(requestCode: Int, resultCode: Int, data: Intent?):Boolean
    fun onDestroy()
}