package org.main.socforfemale.ui.activity

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_settings.*
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.rest.Http
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.ErrorConnModule
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.ResponseData
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.builder.ErrorConnection
import org.main.socforfemale.pattern.builder.SessionOut
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.fragment.YesNoFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Sarvar on 10.08.2017.
 */
class SettingsActivity : BaseActivity() ,Viewer{


    val userData = Base.get.prefs.getUser()
    val sex = listOf(Base.get.resources.getString(R.string.unknown),Base.get.resources.getString(R.string.male),Base.get.resources.getString(R.string.female))
    var changed = false
    val map = hashMapOf(0 to "N", 1 to "F", 2 to "M")
    val model                 = Model()

    @Inject
    lateinit var presenter:Presenter

    @Inject
    lateinit var errorConn: ErrorConnection

    override fun getLayout(): Int = R.layout.activity_settings

    override fun initView() {
        log.d("close profile: ${userData.close}")
        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model(),this))
                .presenterModule(PresenterModule())
                .errorConnModule(ErrorConnModule(this,false))
                .build()
                .inject(this)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle(resources.getString(R.string.settings))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }

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

        switchCloseAccount.isChecked = if(Base.get.prefs.getUser().close == 1 ) true else false
        switchCloseAccount.setOnCheckedChangeListener{view, isChecked ->
            val js = JSONObject()
            js.put("session",Base.get.prefs.getUser().session)
            js.put("user_id",Base.get.prefs.getUser().userId)
            model.responseCall(Http.getRequestData(js, Http.CMDS.CLOSE_PROFIL))
                    .enqueue(object :Callback<ResponseData>{
                        override fun onResponse(call: Call<ResponseData>?, response: Response<ResponseData>?) {
                            log.d("close profil $response")
                            try{
                               if (response!!.body()!!.res == "0"){
                                   val user = Base.get.prefs.getUser()
                                   log.d("closed :${user.close}")

                                   user.close = if(user.close == 1) 0 else 1
                                   Base.get.prefs.setUser(user)
                               }
                           }catch (e:Exception){
                                switchCloseAccount.isChecked = if(Base.get.prefs.getUser().close == 1 ) true else false
                           }

                        }

                        override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                            log.d("close profil fail $t")
                            switchCloseAccount.isChecked = if(Base.get.prefs.getUser().close == 1 ) true else false

                        }

                    })
         }
        quitLay.setOnClickListener {
                val dialog = YesNoFragment.instance()
                        dialog.setDialogClickListener(object : YesNoFragment.DialogClickListener{
                            override fun click(whichButton: Int) {

                                if (whichButton == YesNoFragment.NO){
                                    dialog.dismiss()
                                }else{
                                    dialog.dismiss()

                                    MainActivity.start          = 0
                                    MainActivity.end            = 20
                                    MainActivity.startFeed      = 0
                                    MainActivity.endFeed        = 20
                                    MainActivity.startFollowers = 0
                                    MainActivity.endFollowers   = 20
                                    MainActivity.startFollowing = 0
                                    MainActivity.endFollowing   = 20
                                    MainActivity.MY_POSTS_STATUS = MainActivity.NEED_UPDATE
                                    MainActivity.FEED_STATUS = MainActivity.NEED_UPDATE
                                    MainActivity.COMMENT_POST_UPDATE = 0
                                    MainActivity.COMMENT_COUNT = 0
                                    val sesion = SessionOut.Builder(this@SettingsActivity)
                                            .setErrorCode(96)
                                            .build()
                                    sesion.out()
                                }

                            }

                        })
                dialog.show(supportFragmentManager,YesNoFragment.TAG)

        }
    }


    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {


    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_save,menu)
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {



      if (changed || map.get(gender.selectedItemPosition) != Base.get.prefs.getUser().gender){

          errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
              override fun connected() {
                  log.d("connected")

                  val jsObject = JSONObject()
                  jsObject.put("user_id",Base.get.prefs.getUser().userId)
                  jsObject.put("session",Base.get.prefs.getUser().session)
                  jsObject.put("username",username.text.toString())
                  jsObject.put("name",name.text.toString())
                  jsObject.put("gender", map.get(gender.selectedItemPosition))


                  presenter.requestAndResponse(jsObject, Http.CMDS.CHANGE_USER_SETTINGS)

              }

              override fun disconnected() {
                  log.d("disconnected")

                    Toast.makeText(this@SettingsActivity,resources.getString(R.string.internet_conn_error),Toast.LENGTH_SHORT).show()
              }

          })

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


    override fun onBackPressed() {
        username.hideKeyboard()
        name.hideKeyboard()

        Functions.hideSoftKeyboard(this)

        super.onBackPressed()
    }

    fun View.showKeyboard() {
        this.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}