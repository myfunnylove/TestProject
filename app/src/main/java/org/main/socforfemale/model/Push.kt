package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by macbookpro on 17.09.17.
 */
data class Push (@SerializedName("title") var title:Int, //  1 - like,2 - comment,3 - request,4 - other...
                 @SerializedName("photo") var photo:String,
                 @SerializedName("username") var username:String,
                 @SerializedName("time") var time:String,
                 @SerializedName("action") var action:Action)

data class Action(@SerializedName("actionName") var actionName:String,
                  @SerializedName("actionId") var actionId:String)

