package org.main.socforfemale.connectors

/**
 * Created by Michaelan on 5/23/2017.
 */
interface GoNext {

    fun goNext(to:Int,data:String)
    fun donGo(why:String)
}