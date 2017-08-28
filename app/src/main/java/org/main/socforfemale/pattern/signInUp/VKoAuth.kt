package org.main.socforfemale.pattern.signInUp

import android.app.Activity
import android.content.Intent
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import org.json.JSONArray
import org.json.JSONObject
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.Http
import org.main.socforfemale.resources.utils.log

/**
 * Created by Sarvar on 28.08.2017.
 */
class VKoAuth private constructor(builder: Builder) :SocialNetwork{



    private val context: Activity
    private val authorizeConnector: AuthorizeConnector

    var vkAccessToken: String = ""
    var vkEmail: String = ""
    var vkRequest: VKRequest? = null

    class Builder(val context: Activity, val authorizeConnector: AuthorizeConnector){

        fun build():VKoAuth = VKoAuth(this)
    }






    init {
        context = builder.context
        authorizeConnector = builder.authorizeConnector
    }



    override fun register() {


    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
            VKSdk.onActivityResult(requestCode,resultCode,data,vkCallback)

    override fun login() {
        VKSdk.login(context)

    }

    override fun cancel() {
        if (vkRequest != null) vkRequest!!.cancel()
    }
    private fun VKSuccess(success: VKAccessToken?) {

        if (success!!.accessToken != null) vkAccessToken = success.accessToken
        if (success.email != null) vkEmail = success.email

        log.d("Vk access token -> $vkAccessToken")
        log.d("Vk email -> $vkEmail")


        vkRequest = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,bdate,city,country,sex,email,photo_200_orig"))

        vkRequest!!.secure = false

        vkRequest!!.executeWithListener(vkRequestListener)

    }

    val vkRequestListener = object : VKRequest.VKRequestListener() {
        override fun attemptFailed(request: VKRequest?, attemptNumber: Int, totalAttempts: Int) {

        }

        override fun onComplete(response: VKResponse?) {


            log.d(response!!.json.toString())
            val list = JSONArray(response.json.optString("response"))

            val vkUser = Base.get.prefs.getUser()

            val vkObj = JSONObject(list.get(0).toString())
            vkUser.userId = vkObj.optString("id")
            vkUser.first_name = vkObj.optString("first_name")
            vkUser.last_name = vkObj.optString("last_name")
            vkUser.gender = if (vkObj.optString("sex") == "1") "F" else "M"
            vkUser.signType = 1
            vkUser.token = vkAccessToken
            vkUser.phoneOrMail = vkEmail
            vkUser.profilPhoto = if (vkObj.has("photo_200_orig")) vkObj.optString("photo_200_orig") else ""
            Base.get.prefs.setUser(vkUser)

//            val sendObj = JSONObject()
//            sendObj.put("vk_id", vkObj.optString("id"))
//            sendObj.put("token", vkAccessToken)
//
//            presenter.requestAndResponse(sendObj, Http.CMDS.VK_ORQALI_LOGIN)

            authorizeConnector.onSuccess(vkObj.optString("id"),vkAccessToken)
        }

        override fun onProgress(progressType: VKRequest.VKProgressType?, bytesLoaded: Long, bytesTotal: Long) {
        }

        override fun onError(error: VKError?) {
            log.d("Vkontakte error ${error!!.errorMessage}")
            log.d("Vkontakte error ${error.errorCode}")
            log.d("Vkontakte error ${error.errorReason}")
            log.d("Vkontakte error ${error.httpError.toString()}")

        }
    }

    val vkCallback = object : VKCallback<VKAccessToken> {
        override fun onError(error: VKError?) {
        }

        override fun onResult(success: VKAccessToken?) {


            VKSuccess(success)

        }

    }
}