package org.main.socforfemale.ui.activity

import android.content.Intent
import android.os.Handler
import android.support.graphics.drawable.VectorDrawableCompat
import android.view.View
import android.widget.Toast
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.mvp.Viewer
import kotlinx.android.synthetic.main.activity_sign.*
import org.json.JSONObject
import org.main.socforfemale.base.Base
import org.main.socforfemale.rest.Http
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.ErrorConnModule
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.User
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.pattern.builder.ErrorConnection
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.JavaCodes
import javax.inject.Inject

class SignActivity : BaseActivity() ,Viewer{


    var signMode   = -1
    val PHONE_MODE = 77
    val MAIL_MODE  = 129
    val SMS_MODE   = 3

    @Inject
    lateinit var presenter:Presenter
    @Inject
    lateinit var errorConn: ErrorConnection

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


            selectMail.isEnabled  = false
            selectPhone.isEnabled = false
            smsCode.visibility = View.VISIBLE
            signMode = SMS_MODE
        }else if(from == Http.CMDS.SMSNI_JONATISH){

            phone.isEnabled    = false
            mail.isEnabled     = false
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
                .errorConnModule(ErrorConnModule(this,false))
                .build()
                .inject(this)
        signMode = PHONE_MODE

        phone.addTextChangedListener(JavaCodes.EditTelephoneCodeWatcher)
        phone.setKeyListener(Functions.EditCardKey)

        selectPhone.setOnClickListener {
            signMode = PHONE_MODE


            selectPhone.setBackgroundDrawable(resources.getDrawable(R.drawable.sign_edittext_block_top_enabled))
            selectMail.setBackgroundDrawable(resources.getDrawable(R.drawable.sign_edittext_block_top_disabled))
            phone.visibility = View.VISIBLE
            mail.visibility = View.GONE
        }

        selectMail.setOnClickListener {
            signMode = MAIL_MODE
            selectPhone.setBackgroundDrawable(resources.getDrawable(R.drawable.sign_edittext_block_top_disabled))
            selectMail.setBackgroundDrawable(resources.getDrawable(R.drawable.sign_edittext_block_top_enabled))

            phone.visibility = View.GONE
            mail.visibility = View.VISIBLE
        }

        signUp.setOnClickListener{




            errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                override fun connected() {

                    if(signMode == PHONE_MODE){
                        if (Functions.clearEdit(phone).length != 9){

                            phone.error = resources.getString(R.string.error_incorrect_phone)


                        }else{

                            val sendObject = JSONObject()
                            phoneStr = "998${Functions.clearEdit(phone)}"
                            sendObject.put("phone",phoneStr)

                            presenter.requestAndResponse(sendObject, Http.CMDS.TELEFONNI_JONATISH)
                        }


                    }else if(signMode == SMS_MODE){

                        if (smsCode.text.toString().length != 6){
                            smsCode.error = resources.getString(R.string.sms_code_error)
                        }else{
                            val sendObject = JSONObject()

                            smsStr = smsCode.text.toString()

                            sendObject.put("phone",phoneStr)
                            sendObject.put("sms",smsStr)


                            presenter!!.requestAndResponse(sendObject, Http.CMDS.SMSNI_JONATISH)
                        }
                    }else{
                        if (!mail.text.toString().contains("@")){

                            mail.error = resources.getString(R.string.error_incorrect_mail)
                        }else{
                            val sendObject = JSONObject()
                            phoneStr = mail.text.toString()
                            sendObject.put("phone",phoneStr)

                            presenter!!.requestAndResponse(sendObject, Http.CMDS.TELEFONNI_JONATISH)
                        }
                    }


                }

                override fun disconnected() {
                    Toast.makeText(this@SignActivity,resources.getString(R.string.internet_conn_error), Toast.LENGTH_SHORT).show()

                }

            })

        }


    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    override fun getLayout(): Int {
        return R.layout.activity_sign


    }
    private fun disableAllElements() {

        phone.isEnabled = false
//        phone.error = ""

        smsCode.isEnabled = false
//        smsCode.error = ""

        mail.isEnabled = false
//        mail.error = ""

        selectPhone.isEnabled = false
        selectMail.isEnabled = false
        signUp.isEnabled = false
    }
    private fun enableAllElements() {

        phone.isEnabled = true
//        phone.error = ""
        smsCode.isEnabled = true
//        smsCode.error = ""

        mail.isEnabled = true
//        mail.error = ""
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