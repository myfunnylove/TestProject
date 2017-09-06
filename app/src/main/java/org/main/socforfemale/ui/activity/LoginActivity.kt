package org.main.socforfemale.ui.activity

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.facebook.CallbackManager
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.rest.Http
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.signInUp.*
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import javax.inject.Inject


class LoginActivity : BaseActivity(), Viewer {


    //var fbCallbackManager: CallbackManager? = null
    var username = ""
    var password = ""

    lateinit var facebookoAuth:FacebookoAuth
    lateinit var vkAuth:VKoAuth
    lateinit var simpleAuth:SimpleoAuth
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


            Functions.checkPermissions(this)


            //FACEBOOK OAUTH2 BUILDER INITIALIZE
            facebookoAuth = FacebookoAuth.Builder(CallbackManager.Factory.create(),this,fb)
                                         .build()

            //VKONTAKTE OAUTH2 BUILDER INITIALIZE
            vkAuth = VKoAuth.Builder(this, vk)
                            .build()

            //SIMPLE AUTH BUILDER INITIALIZE
            simpleAuth = SimpleoAuth.Builder(this,sm,login,pass)
                                    .build()

            logIn.setOnClickListener {

                signBridge = SignBridge(simpleAuth)
                signBridge.initialize().tryAuthorize()

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
//        Toast.makeText(this,":OK",Toast.LENGTH_SHORT).show()
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
        try{
            signBridge.onDestroy()
        }catch (e:Exception){}
    }

    private fun disableAllElements() {


        loginVk.isEnabled       = false
        loginFb.isEnabled       = false
        signUp.isEnabled        = false

    }

    private fun enableAllElements() {


        signUp.isEnabled = true
        loginVk.isEnabled = true
        loginFb.isEnabled = true
    }


    /*
    *
    *
    *   LISTENERS
    *
    *
    * */

    val fb = object : AuthorizeConnector{
        override fun onSuccess(idUser: String, token: String) {

            val sendObj = JSONObject()
            sendObj.put("fb_id", idUser)
            sendObj.put("token", token)

            presenter.requestAndResponse(sendObj, Http.CMDS.FB_ORQALI_LOGIN)
        }


        override fun onFailure(message: String) {

            if(message.isEmpty()) onFailure(Http.CMDS.VK_ORQALI_LOGIN, resources.getString(R.string.error_no_type))
            else onFailure(Http.CMDS.FB_ORQALI_LOGIN, message)

        }

    }

    val vk = object : AuthorizeConnector{

        override fun onSuccess(idUser: String, token: String) {
            val sendObj = JSONObject()
            sendObj.put("vk_id",idUser)
            sendObj.put("token", token)

            presenter.requestAndResponse(sendObj, Http.CMDS.VK_ORQALI_LOGIN)
        }

        override fun onFailure(message: String) {
            if(message.isEmpty()) onFailure(Http.CMDS.VK_ORQALI_LOGIN, resources.getString(R.string.error_no_type))
            else onFailure(Http.CMDS.VK_ORQALI_LOGIN, message)

        }



    }

    val sm = object :AuthorizeConnector{
        override fun onSuccess(idUser: String, token: String) {
            val obj = JSONObject()
            obj.put("username", idUser)
            obj.put("password", token)
            presenter.requestAndResponse(obj, Http.CMDS.LOGIN_PAYTI)
        }

        override fun onFailure(message: String) {
            Toast.makeText(Base.get.context,message,Toast.LENGTH_SHORT).show()
        }

    }
}


