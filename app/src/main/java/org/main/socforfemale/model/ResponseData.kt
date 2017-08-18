package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 6/15/2017.
 */
data class ResponseData(@SerializedName("res") var res:String, @SerializedName("message") var message:String, @SerializedName("prms") var prms:String)