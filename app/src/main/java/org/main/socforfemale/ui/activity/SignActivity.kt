package org.main.socforfemale.ui.activity

import android.content.Intent
import android.os.Handler
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.View
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.mvp.Viewer
import kotlinx.android.synthetic.main.activity_sign.*
import org.json.JSONObject
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.Http
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.User
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Prefs
import javax.inject.Inject

class SignActivity : BaseActivity() ,Viewer{


    var signMode   = -1
    val PHONE_MODE = 77
    val MAIL_MODE  = 129
    val SMS_MODE   = 3

    @Inject
    lateinit var presenter:Presenter

    var phoneStr:String      = ""
    var smsStr:  String      = ""
    override fun initProgress() {

        progressLay.visibility = View.VISIBLE
        disableAllElements()
    }



    override fun showProgress() {

    }


    override fun hideProgress() {
        progressLay.visibility = View.GONE
        enableAllElements()
    }

    override fun onSuccess(from:String,result: String) {

        if(from == Http.CMDS.TELEFONNI_JONATISH){

            phoneLay.isEnabled    = false
            mailLay.isEnabled     = false
            selectMail.isEnabled  = false
            selectPhone.isEnabled = false
            smsCodeLay.visibility = View.VISIBLE
            signMode = SMS_MODE
        }else if(from == Http.CMDS.SMSNI_JONATISH){

            phoneLay.isEnabled    = false
            mailLay.isEnabled     = false
            selectMail.isEnabled  = false
            selectPhone.isEnabled = false

            val user = User("","","","","","N",phoneStr,smsStr,"","","",signMode)
            Base.get.prefs.setUser(user)
            startActivity(Intent(this,LoginAndPassActivity().javaClass))
            this.finish()
        }

    }



    override fun onFailure(from: String, message: String, erroCode: String) {

            errorText.visibility = View.VISIBLE
            errorText.text = message

            Handler().postDelayed({
                errorText.text = ""
                errorText.visibility = View.GONE
            },3000)

    }


    override fun initView() {
        Const.TAG = "SignActivity"

        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model(),this))
                .presenterModule(PresenterModule())
                .build()
                .inject(this)
        signMode = PHONE_MODE

        val phoneDis = VectorDrawableCompat.create(resources,R.drawable.phone,selectPhone.context.theme)
        val phoneEnb = VectorDrawableCompat.create(resources,R.drawable.phone_select,selectPhone.context.theme)
        val mailDis  = VectorDrawableCompat.create(resources,R.drawable.email,selectPhone.context.theme)
        val mailEnb  = VectorDrawableCompat.create(resources,R.drawable.email_select,selectPhone.context.theme)

        selectPhone.setOnClickListener {
            signMode = PHONE_MODE


            selectPhone.setImageDrawable(phoneEnb)
            selectMail.setImageDrawable(mailDis)

            mailLay.visibility = View.GONE
            phoneLay.visibility = View.VISIBLE
        }

        selectMail.setOnClickListener {
            signMode = MAIL_MODE

            selectMail.setImageDrawable(mailEnb)
            selectPhone.setImageDrawable(phoneDis)

            phoneLay.visibility = View.GONE
            mailLay.visibility = View.VISIBLE
        }

        signUp.setOnClickListener{


            if(signMode == PHONE_MODE){
                if (phone.text.toString().length != 9){

                    phoneLay.error = resources.getString(R.string.error_incorrect_phone)


                }else{

                    val sendObject = JSONObject()
                    phoneStr = "998${phone.text.toString()}"
                    sendObject.put("phone",phoneStr)

                    presenter!!.requestAndResponse(sendObject, Http.CMDS.TELEFONNI_JONATISH)
                }


            }else if(signMode == SMS_MODE){

                if (smsCode.text.toString().length != 6){
                    smsCodeLay.error = resources.getString(R.string.sms_code_error)
                }else{
                    val sendObject = JSONObject()

                    smsStr = smsCode.text.toString()

                    sendObject.put("phone",phoneStr)
                    sendObject.put("sms",smsStr)


                    presenter!!.requestAndResponse(sendObject, Http.CMDS.SMSNI_JONATISH)
                }
            }else{
                if (!mail.text.toString().contains("@")){

                    mailLay.error = resources.getString(R.string.error_incorrect_mail)
                }else{
                    val sendObject = JSONObject()
                    phoneStr = mail.text.toString()
                    sendObject.put("phone",phoneStr)

                    presenter!!.requestAndResponse(sendObject, Http.CMDS.TELEFONNI_JONATISH)
                }
            }

        }


    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun getLayout(): Int {
        return R.layout.activity_sign


    }
    private fun disableAllElements() {

        phoneLay.isEnabled = false
        phoneLay.error = ""

        smsCodeLay.isEnabled = false
        smsCodeLay.error = ""

        mailLay.isEnabled = false
        mailLay.error = ""

        selectPhone.isEnabled = false
        selectMail.isEnabled = false
        signUp.isEnabled = false
    }
    private fun enableAllElements() {

        phoneLay.isEnabled = true
        phoneLay.error = ""
        smsCodeLay.isEnabled = true
        smsCodeLay.error = ""

        mailLay.isEnabled = true
        mailLay.error = ""
        selectPhone.isEnabled = true
        selectMail.isEnabled = true
        signUp.isEnabled = true

    }
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginActivity().javaClass))
        this.finish()
    }
}