package org.main.socforfemale.di.modules

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import org.main.socforfemale.pattern.builder.ErrorConnection

@Module
class ErrorConnModule(private val activity: AppCompatActivity,private val isShow :Boolean) {





    @Provides
    fun activity() : AppCompatActivity = activity
    @Provides
    fun show() : Boolean = isShow



    @Provides
    fun errorconn(activity: AppCompatActivity,show:Boolean): ErrorConnection {

        return ErrorConnection.Builder(activity)
                              .init()
                              .initClickListener()
                              .show(show)
                              .build()
    }
}