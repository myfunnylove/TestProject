package org.main.socforfemale.connectors

import android.os.Bundle

/**
 * Created by Michaelan on 5/23/2017.
 */
interface SignConnector {

    fun goNext(to:Int,data: String)
    fun donGo(why:String)
}