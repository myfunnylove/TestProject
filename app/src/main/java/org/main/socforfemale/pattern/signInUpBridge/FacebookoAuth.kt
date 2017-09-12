package org.main.socforfemale.pattern.signInUpBridge

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import org.json.JSONObject
import org.main.socforfemale.base.Base
import org.main.socforfemale.model.User
import org.main.socforfemale.resources.utils.log
import java.util.*

/**
 * Created by Sarvar on 28.08.2017.
 */
class FacebookoAuth private constructor(builder: Builder) :SocialNetwork{


    private val fbCallbackManager: CallbackManager
    private val context: Activity
    private val authorizeConnector: AuthorizeConnector


     class Builder(val fbCallbackManager: CallbackManager, val context: Activity,val authorizeConnector: AuthorizeConnector){

         fun build():FacebookoAuth = FacebookoAuth(this)


      }






    init {
        fbCallbackManager = builder.fbCallbackManager
        context = builder.context
        authorizeConnector = builder.authorizeConnector
    }



    override fun register() {
        LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback)

    }



    override fun result(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data)
        return true
    }

    override fun login() {
        LoginManager.getInstance().logInWithReadPermissions(context, Arrays.asList("email", "public_profile"))

    }

    override fun cancel() {
        fbCallback.onCancel()

    }


    val fbCallback = object : FacebookCallback<LoginResult> {

        override fun onSuccess(success: LoginResult?) {

            val graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), object : GraphRequest.GraphJSONObjectCallback {
                override fun onCompleted(user: JSONObject?, p1: GraphResponse?) {
                    log.d(user!!.toString())
                    log.d(p1!!.rawResponse!!)
                    log.d(p1.jsonObject!!.toString())
                    log.d(AccessToken.getCurrentAccessToken().token)
                    if (p1.error == null) {

                        log.d(user.toString())
                        val picture = JSONObject(user.optString("picture"))
                        val data = JSONObject(picture.optString("data"))
                        log.d(data.toString())
                        val fbUser = User(user.optString("id"),
                                "",
                                AccessToken.getCurrentAccessToken().token,
                                "",
                                "",
                                user.optString("gender"),
                                user.optString("email"),
                                "",
                                user.optString("first_name"),
                                user.optString("last_name"),
                                data.optString("url"),
                                0)

                        Base.get.prefs.setUser(fbUser)

//                        val sendObj = JSONObject()
//                        sendObj.put("fb_id", user.optString("id"))
//                        sendObj.put("token", AccessToken.getCurrentAccessToken().token)
//
//                        presenter.requestAndResponse(sendObj, Http.CMDS.FB_ORQALI_LOGIN)
                        authorizeConnector.onSuccess(user.optString("id"),AccessToken.getCurrentAccessToken().token)
                    } else {
                        log.d("Facebook $p1.error.errorMessage")
                        log.d("Facebook $p1.error.errorRecoveryMessage")
                        log.d("Facebook $p1.error.errorUserMessage")
                        log.d("Facebook ${p1.error.errorCode}")
                        log.d("Facebook ${p1.error.errorType}")
                        authorizeConnector.onFailure()
//                        onFailure(Http.CMDS.FB_ORQALI_LOGIN, resources.getString(R.string.error_no_type))
                    }
                }

            })

            val params = Bundle()
            params.putString("fields", "id,name,email,gender,first_name,last_name,picture")
            graphRequest.parameters = params
            graphRequest.executeAsync()
        }

        override fun onError(error: FacebookException?) {

            log.d(error!!.cause.toString())

        }

        override fun onCancel() {

        }

    }
}