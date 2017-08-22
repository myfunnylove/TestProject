package org.main.socforfemale.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.base.Http
import org.main.socforfemale.model.User
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.Prefs
import org.main.socforfemale.resources.utils.log
import java.util.*


class LoginActivity : BaseActivity(), Viewer {


    var vkRequest: VKRequest? = null
    var fbCallbackManager: CallbackManager? = null
    var username = ""
    var password = ""
    var presenter: Presenter? = null
    var vkAccessToken: String = ""
    var vkEmail: String = ""

    companion object {
        val FACEBOOK = 64206
        val VKONTAKTE = 10485
    }


    /*
    *
    *
    * PERMISSION CHECK
    *
    *
    * */


    override fun getLayout(): Int {
        return R.layout.activity_login
    }

    override fun initView() {
        Const.TAG = "LoginACtivity"

        if (Base.get.prefs.getUser().session == "") {
            presenter = Presenter(this)

            Functions.checkPermissions(this) /*CHECK PERMISSION*/


            fbCallbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback)



            logIn.setOnClickListener {

                username = login.text.toString()
                password = pass.text.toString()

                if (username.length < 6) {

                    loginLay.error = resources.getString(R.string.username_field_less_5)

                } else if (password.length < 5) {

                    passLay.error = resources.getString(R.string.password_field_less_5)

                } else {

                    val obj = JSONObject()
                    obj.put("username", username)
                    obj.put("password", password)
                    presenter!!.requestAndResponse(obj, Http.CMDS.LOGIN_PAYTI)
                }
            }


            loginVk.setOnClickListener { VKSdk.login(this) }


            loginFb.setOnClickListener { LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile")) }

            signUp.setOnClickListener {
                startActivity(Intent(this, SignActivity().javaClass))
                this.finish()
            }
        } else {
            startActivity(Intent(this, MainActivity().javaClass))
            this.finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (requestCode == VKONTAKTE) { /* INTEGRATE VIA VKONTAKTE */
            val VKCallback = object : VKCallback<VKAccessToken> {
                override fun onError(error: VKError?) {
                }

                override fun onResult(success: VKAccessToken?) {


                    VKSuccess(success)

                }

            }

            if (!VKSdk.onActivityResult(requestCode, resultCode, data, VKCallback)) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else if (requestCode == FACEBOOK) { /* INTEGRATE VIA FACEBOOK */

            fbCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }


    }


    /*
    *
    *  VKONTAKTE CALLBACK
    *
    * */
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
            vkUser.gender = if (vkObj.optString("sex") == "1") "female" else "male"
            vkUser.signType = 1
            vkUser.token = vkAccessToken
            vkUser.phoneOrMail = vkEmail
            vkUser.profilPhoto = if (vkObj.has("photo_200_orig")) vkObj.optString("photo_200_orig") else ""
            Base.get.prefs.setUser(vkUser)

            val sendObj = JSONObject()
            sendObj.put("vk_id", vkObj.optString("id"))
            sendObj.put("token", vkAccessToken)

            presenter!!.requestAndResponse(sendObj, Http.CMDS.VK_ORQALI_LOGIN)
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

    /*
   *
   *  FACEBOOK CALLBACK
   *
   * */
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

                        val sendObj = JSONObject()
                        sendObj.put("fb_id", user.optString("id"))
                        sendObj.put("token", AccessToken.getCurrentAccessToken().token)

                        presenter!!.requestAndResponse(sendObj, Http.CMDS.FB_ORQALI_LOGIN)
                    } else {
                        log.d("Facebook $p1.error.errorMessage")
                        log.d("Facebook $p1.error.errorRecoveryMessage")
                        log.d("Facebook $p1.error.errorUserMessage")
                        log.d("Facebook ${p1.error.errorCode}")
                        log.d("Facebook ${p1.error.errorType}")
                        onFailure(Http.CMDS.FB_ORQALI_LOGIN, resources.getString(R.string.error_no_type))
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

    private fun goNextActivity(from: Int) {
        val intent = Intent(this, LoginAndPassActivity::class.java)
        intent.putExtra("from", from)
        startActivity(intent)
        this.finish()
    }


    override fun initProgress() {
        progressLay.visibility = View.VISIBLE
        errorText.text = ""

        disableAllElements()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
        progressLay.visibility = View.GONE
        enableAllElements()
    }

    override fun onSuccess(from: String, result: String) {

//        if(from  == Http.CMDS.LOGIN_PAYTI){

        startActivity(Intent(this, MainActivity().javaClass))
        this.finish()
//        }else if (from == Http.CMDS.FB_ORQALI_LOGIN){
//
//        }
    }

    override fun onFailure(from: String, message: String, erroCode: String) {


        when (from) {

            Http.CMDS.FB_ORQALI_LOGIN -> goNextActivity(FACEBOOK)
            Http.CMDS.VK_ORQALI_LOGIN -> goNextActivity(VKONTAKTE)

            else -> {
                errorText.text = message

                Handler().postDelayed({
                    errorText.text = ""
                }, 3000)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        if (vkRequest != null) vkRequest!!.cancel()

        fbCallback.onCancel()


    }

    private fun disableAllElements() {

        loginLay.isEnabled = false
        loginLay.isErrorEnabled = false
        passLay.isEnabled = false
        passLay.isErrorEnabled = false
        loginVk.isEnabled = false
        loginFb.isEnabled = false
        signUp.isEnabled = false

    }

    private fun enableAllElements() {

        loginLay.isEnabled = true
        loginLay.isErrorEnabled = true
        passLay.isEnabled = true
        passLay.isErrorEnabled = true
        signUp.isEnabled = true
        loginVk.isEnabled = true
        loginFb.isEnabled = true
    }
}


