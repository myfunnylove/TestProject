package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 6/26/2017.
 */

data class Post(@SerializedName("user_id") var userId:String,
                @SerializedName("session") var session:String,
                @SerializedName("audios") var audios:ArrayList<String>,
                @SerializedName("images") var images:ArrayList<String>,
                @SerializedName("quote") var quote:Quote)


data class Quote(@SerializedName("text") var text:String,
                 @SerializedName("text_size") var textSize:String,
                 @SerializedName("text_color") var textColor:String)
