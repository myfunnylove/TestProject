package org.main.socforfemale.pattern.signInUp

import android.app.Activity
import android.content.Intent
import android.support.design.widget.TextInputEditText
import org.main.socforfemale.R

/**
 * Created by Sarvar on 28.08.2017.
 */
class SimpleoAuth private constructor(builder: Builder):SocialNetwork {


    private val context: Activity
    private val authorizeConnector: AuthorizeConnector
    private val loginEdit: TextInputEditText
    private val passwordEdit: TextInputEditText
    private lateinit var username:String
    private lateinit var  password:String

    class Builder(val context: Activity,val authorizeConnector: AuthorizeConnector,val loginEdit: TextInputEditText,val passwordEdit: TextInputEditText) {


        fun build(): SimpleoAuth = SimpleoAuth(this)
    }

    init {
        context  = builder.context
        loginEdit = builder.loginEdit
        passwordEdit = builder.passwordEdit
        authorizeConnector = builder.authorizeConnector
    }

    override fun register() {

        username = loginEdit.text.toString()
        password = passwordEdit.text.toString()


    }

    override fun login() {

        if (username.length < 6) {

            authorizeConnector.onFailure(context.resources.getString(R.string.username_field_less_5))

        } else if (password.length < 5) {
            authorizeConnector.onFailure(context.resources.getString(R.string.password_field_less_5))


        } else {

            authorizeConnector.onSuccess(username,password)

        }
    }

    override fun result(requestCode: Int, resultCode: Int, data: Intent?): Boolean = true

    override fun cancel() {
        passwordEdit.text.clear()
        loginEdit.text.clear()
    }
}