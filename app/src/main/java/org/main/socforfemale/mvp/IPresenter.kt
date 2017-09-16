package org.main.socforfemale.mvp

import org.json.JSONObject

/**
 * Created by macbookpro on 13.09.17.
 */
interface IPresenter {
    fun requestAndResponse(data: JSONObject, cmd:String)
}