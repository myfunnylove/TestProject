package org.main.socforfemale.ui.activity

import android.widget.ArrayAdapter
import android.widget.SpinnerAdapter
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.resources.utils.log
import kotlinx.android.synthetic.main.activity_settings.*
import org.main.socforfemale.resources.utils.Prefs

/**
 * Created by Sarvar on 10.08.2017.
 */
class SettingsActivity : BaseActivity() {

    val userData = Prefs.Builder().getUser()
    val sex = listOf(Base.instance.resources.getString(R.string.male),Base.instance.resources.getString(R.string.female))
    override fun getLayout(): Int {
        return R.layout.activity_settings
    }

    override fun initView() {

        name.setText("${userData.first_name} ${userData.last_name}")
        username.setText(userData.userName)

        phone.text = if (!userData.phoneOrMail.contains("@") && userData.phoneOrMail != "") userData.phoneOrMail else resources.getString(R.string.addPhone)
        mail.text  = if (userData.phoneOrMail.contains("@") && userData.phoneOrMail != "") userData.phoneOrMail else resources.getString(R.string.addMail)

        val genderAdapter = ArrayAdapter<String>(this,R.layout.white_textview,sex)
        genderAdapter.setDropDownViewResource(R.layout.white_textview_adapter)
        gender.adapter = genderAdapter
        gender.setSelection(if (userData.gender == "female") 0 else 1)
    }
}