package org.main.socforfemale.mvp

/**
 * Created by Michaelan on 6/15/2017.
 */
interface Viewer {

    fun initProgress()

    fun showProgress()

    fun hideProgress()

    fun onSuccess(from: String,result:String)

    fun onFailure(from: String,message:String,erroCode:String = "")
}