package org.main.socforfemale.ui.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONObject
import org.main.socforfemale.base.Http
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.MVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.resources.utils.Prefs
import javax.inject.Inject

/**
 * Created by Sarvar on 10.08.2017.
 */
class SettingsActivity : BaseActivity() ,Viewer{


    val userData = Base.get.prefs.getUser()
    val sex = listOf(Base.get.resources.getString(R.string.unknown),Base.get.resources.getString(R.string.male),Base.get.resources.getString(R.string.female))
    var changed = false
    val map = hashMapOf(0 to "N", 1 to "F", 2 to "M")

    @Inject
    lateinit var presenter:Presenter

    override fun getLayout(): Int = R.layout.activity_settings

    override fun initView() {

        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model()))
                .presenterModule(PresenterModule())
                .build()
                .inject(this)



        name.setText("${userData.first_name} ${userData.last_name}")
        username.setText(userData.userName)

        phone.text = if (!userData.phoneOrMail.contains("@") && userData.phoneOrMail != "") userData.phoneOrMail else resources.getString(R.string.addPhone)
        mail.text  = if (userData.phoneOrMail.contains("@") && userData.phoneOrMail != "") userData.phoneOrMail else resources.getString(R.string.addMail)

        val genderAdapter = ArrayAdapter<String>(this,R.layout.white_textview,sex)
        genderAdapter.setDropDownViewResource(R.layout.white_textview_adapter)
        gender.adapter = genderAdapter
        gender.setSelection(if (userData.gender == "N") 0
                            else if(userData.gender == "F") 1
                            else 2)

        name.addTextChangedListener(textwatcher)
        username.addTextChangedListener(textwatcher)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_save,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {



      if (changed || map.get(gender.selectedItemPosition) != Base.get.prefs.getUser().gender){
          val jsObject = JSONObject()
          jsObject.put("user_id",Base.get.prefs.getUser().userId)
          jsObject.put("session",Base.get.prefs.getUser().session)
          jsObject.put("username",username.text.toString())
          jsObject.put("name",name.text.toString())
          jsObject.put("gender", map.get(gender.selectedItemPosition))


          presenter.requestAndResponse(jsObject,Http.CMDS.CHANGE_USER_SETTINGS)
      }
        return true
    }

    override fun initProgress() {

    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onSuccess(from: String, result: String) {

        val user = Base.get.prefs.getUser()
        user.first_name = name.text.toString()
        user.gender = map.get(gender.selectedItemPosition)!!
        user.userName = username.text.toString()

        Base.get.prefs.setUser(user)

        Toast.makeText(Base.get.context,Base.get.context.resources.getString(R.string.saved),Toast.LENGTH_SHORT).show()

    }

    override fun onFailure(from: String, message: String, erroCode: String) {
        Toast.makeText(Base.get.context,message,Toast.LENGTH_SHORT).show()

    }


    val textwatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {
            changed = true
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    override fun onStop() {
        changed = false
        super.onStop()
    }
}