package org.main.socforfemale.resources.utils

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import org.main.socforfemale.base.Base
import org.main.socforfemale.model.User
import java.util.prefs.Preferences

/**
 * Created by Michaelan on 6/18/2017.
 */
object Prefs {

    private var prefs:SharedPreferences? = null

    private val USER = "user"
    fun Builder():Prefs{
        if (prefs == null) prefs = PreferenceManager.getDefaultSharedPreferences(Base.instance)

        return Prefs
    }


    @SuppressLint("ApplySharedPref")
    fun setUser(user:User){
        log.d("set user profile $user")

        @SuppressLint("CommitPrefEdits")
        val writer = prefs!!.edit()
        writer.putString(USER,Gson().toJson(user))
        writer.commit()
    }

    fun getUser():User{
        val user = prefs!!.getString(USER,"")
        log.d("get user profile $user")
        if (user != "")
        return Gson().fromJson(user,User::class.java)
        else
            return User("","","","","","female","","","","","",-1)
    }
}