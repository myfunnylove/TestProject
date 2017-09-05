package org.main.socforfemale.pattern.signInUp

/**
 * Created by Sarvar on 28.08.2017.
 */
interface AuthorizeConnector {

    fun onSuccess(idUser:String,token:String)
    fun onFailure(message:String = "")
}