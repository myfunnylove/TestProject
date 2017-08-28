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
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.User
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.signInUp.*
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.Prefs
import org.main.socforfemale.resources.utils.log
import java.util.*
import javax.inject.Inject


class LoginActivity : BaseActivity(), Viewer {


    //var fbCallbackManager: CallbackManager? = null
    var username = ""
    var password = ""

    lateinit var facebookoAuth:FacebookoAuth
    lateinit var vkAuth:VKoAuth
    lateinit var signBridge:SignBridgeConnector


    @Inject
    lateinit var presenter:Presenter



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
            DaggerMVPComponent
                    .builder()
                    .mVPModule(MVPModule(this, Model(),this))
                    .presenterModule(PresenterModule())
                    .build()
                    .inject(this)


            Functions.checkPermissions(this) /*CHECK PERMISSION*/

            facebookoAuth = FacebookoAuth.Builder(CallbackManager.Factory.create(),this,
                    object : AuthorizeConnector{
                        override fun onSuccess(idUser: String, token: String) {

                            val sendObj = JSONObject()
                            sendObj.put("fb_id", idUser)
                            sendObj.put("token", token)

                            presenter.requestAndResponse(sendObj, Http.CMDS.FB_ORQALI_LOGIN)
                        }


                        override fun onFailure() {

                            onFailure(Http.CMDS.FB_ORQALI_LOGIN, resources.getString(R.string.error_no_type))

                        }

                    }
                    ).build()


            vkAuth = VKoAuth.Builder(this, object : AuthorizeConnector{

                override fun onSuccess(idUser: String, token: String) {
                        val sendObj = JSONObject()
                        sendObj.put("vk_id",idUser)
                        sendObj.put("token", token)

                        presenter.requestAndResponse(sendObj, Http.CMDS.VK_ORQALI_LOGIN)
                }

                override fun onFailure() {
                    onFailure(Http.CMDS.VK_ORQALI_LOGIN, resources.getString(R.string.error_no_type))

                }



            }).build()



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
                    presenter.requestAndResponse(obj, Http.CMDS.LOGIN_PAYTI)
                }
            }


            loginVk.setOnClickListener {
                signBridge = SignBridge(vkAuth)
                signBridge.initialize().tryAuthorize()
            }


            loginFb.setOnClickListener {
                signBridge = SignBridge(facebookoAuth)
                signBridge.initialize().tryAuthorize()

            }

            signUp.setOnClickListener {
                startActivity(Intent(this, SignActivity().javaClass))
                this.finish()
            }
        } else {
            startActivity(Intent(this, MainActivity().javaClass))
            this.finish()
        }

    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == VKONTAKTE) { /* INTEGRATE VIA VKONTAKTE */

            if (!signBridge.getResult(requestCode,resultCode,data)) {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else if (requestCode == FACEBOOK) { /* INTEGRATE VIA FACEBOOK */

            signBridge.getResult(requestCode,resultCode,data)
        }
    }

    /*
    *
    *  VKONTAKTE CALLBACK
    *
    * */



    private fun goNextActivity(from: Int) {
        val intent = Intent(this, LoginAndPassActivity::class.java)
        intent.putExtra("from", from)
        startActivityForResult(intent,Const.TO_FAIL)
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

            "96" -> {

            }

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
        signBridge.onDestroy()
    }

    private fun disableAllElements() {

        loginLay.isEnabled      = false
        loginLay.isErrorEnabled = false
        passLay.isEnabled       = false
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


