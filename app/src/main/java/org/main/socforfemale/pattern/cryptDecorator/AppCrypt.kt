package org.main.socforfemale.pattern.cryptDecorator

import com.google.gson.JsonObject
import org.json.JSONObject

/**
 * Created by Sarvar on 12.09.2017.
 */
class AppCrypt(JSobject: String) : Crypt() {

    init {
        obj = JSobject
    }
}