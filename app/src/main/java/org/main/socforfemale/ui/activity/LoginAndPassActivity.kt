package org.main.socforfemale.ui.activity

import android.content.Intent
import android.os.Handler
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.squareup.picasso.Picasso
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login_and_password.*
import okhttp3.MultipartBody

import org.json.JSONObject
import org.main.socforfemale.base.Http
import org.main.socforfemale.model.ProgressRequestBody
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Prefs
import org.main.socforfemale.resources.utils.log
import java.io.File

/**
 * Created by Michaelan on 6/16/2017.
 */
class LoginAndPassActivity :BaseActivity(),Viewer{

    var presenter:Presenter? = null
    var isLoginFree = false
    var username = ""
    var password = ""
    var from = -1

    override fun getLayout(): Int {
        return R.layout.activity_login_and_password
    }

    override fun initView() {
        Const.TAG = "LoginAndPassActivity"

        from = intent.getIntExtra("from",from)

        if(Prefs.Builder().getUser().profilPhoto != ""){
            log.d("photo: ${Prefs.Builder().getUser().profilPhoto }")
            Picasso.with(this)
                    .load(Prefs.Builder().getUser().profilPhoto)
                    .error(android.R.drawable.stat_notify_error)
                    .into(profilPhoto)
        }

        presenter = Presenter(this)

        login.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if(s!!.toString().length >= 6) presenter!!.filterLogin(login)
                else login.setLoginResult()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


        signUp.setOnClickListener {

            username = login.text.toString()
            password = pass.text.toString()

            if (!isLoginFree){

                loginLay.error = resources.getString(R.string.username_not_free_error)

            }else if(username.length < 6){

                loginLay.error = resources.getString(R.string.username_field_less_5)
                login.setLoginResult(R.drawable.close_circle_outline)

            }else if(password.length < 5){

                passLay.error = resources.getString(R.string.password_field_less_5)

            }else{
                val obj = JSONObject()
                obj.put("username",username)
                obj.put("password",password)
                obj.put("photo",Prefs.Builder().getUser().profilPhoto)
                when(from){
                    -1 -> {
                        obj.put("phone", Prefs.Builder().getUser().phoneOrMail)

                        presenter!!.requestAndResponse(obj, Http.CMDS.ROYXATDAN_OTISH)
                    }

                    LoginActivity.FACEBOOK -> {
                        val user = Prefs.Builder().getUser()

                        obj.put("id",user.userId)
                        obj.put("token",user.token)
                        obj.put("type","fb")
                        presenter!!.requestAndResponse(obj, Http.CMDS.FB_VA_VK_ORQALI_REG)

                    }

                    LoginActivity.VKONTAKTE -> {
                        val user = Prefs.Builder().getUser()

                        obj.put("id",user.userId)
                        obj.put("token",user.token)
                        obj.put("type","vk")
                        presenter!!.requestAndResponse(obj, Http.CMDS.FB_VA_VK_ORQALI_REG)
                    }

                    else -> {
                        obj.put("phone", Prefs.Builder().getUser().phoneOrMail)

                        presenter!!.requestAndResponse(obj, Http.CMDS.ROYXATDAN_OTISH)
                    }

                }
            }
        }
    }

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

    override fun onSuccess(from: String, result: String) {

        if(from == Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH){

            isLoginFree = true
            login.setLoginResult(R.drawable.check_circle_outline)
        }else {
            val response = JSONObject(result)

            val user = Prefs.Builder().getUser()
            log.d("login: $username password: $password")
            user.userId   = response.optString("user_id")
            user.session  = response.optString("session")
            user.password = password
            user.userName = username
            Prefs.setUser(user)


            startActivity(Intent(this,MainActivity().javaClass))
            this.finish()
        }
    }

    override fun onFailure(from: String, message: String, erroCode: String) {
        if(from == Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH){
            isLoginFree = false
            login.setLoginResult(R.drawable.close_circle_outline)
        }else{
            errorText.visibility = View.VISIBLE
            errorText.text = message

            Handler().postDelayed({
                errorText.text = ""
                errorText.visibility = View.GONE
            },3000)
        }
    }


    fun AppCompatEditText.setLoginResult(drawable:Int = 0){
        if(drawable != 0){
            val drawableCompat = VectorDrawableCompat.create(resources,drawable,this.context.theme)
            this.setCompoundDrawablesWithIntrinsicBounds(null,null,drawableCompat,null)
        }else{
            this.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null)

        }
    }


    private fun disableAllElements() {

        loginLay.isEnabled = false
        loginLay.isErrorEnabled = false

        passLay.isEnabled = false
        passLay.isErrorEnabled = false



        signUp.isEnabled = false
    }
    private fun enableAllElements() {

        loginLay.isEnabled = true
        loginLay.isErrorEnabled = true

        passLay.isEnabled = true
        passLay.isErrorEnabled = true
        signUp.isEnabled = true

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginActivity().javaClass))
        this.finish()
    }
}

