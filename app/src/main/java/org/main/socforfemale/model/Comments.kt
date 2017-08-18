package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 7/10/2017.
 */
data class Comments(@SerializedName("comments")  var comments:ArrayList<Comment>)

data class Comment(@SerializedName("username")   var username:String,
                   @SerializedName("user_id")    var userId:String,
                   @SerializedName("photo_150")  var avatar:String,
                   @SerializedName("comment_id") var commentId:String,
                   @SerializedName("comment")    var comment:String)