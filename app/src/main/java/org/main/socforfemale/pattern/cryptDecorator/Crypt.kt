package org.main.socforfemale.pattern.cryptDecorator

import com.google.gson.JsonObject
import org.json.JSONObject

abstract class Crypt  {
    protected var obj: String? = null

    open fun getPrm():String{
        return obj!!
    }

}