package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 5/23/2017.
 */
data class Follow (
        @SerializedName("users") var users:ArrayList<Users>
)

data class Users(@SerializedName("user_id")   var userId:String,
                 @SerializedName("username")  var username:String,
                 @SerializedName("photo_150") var photo150:String,
                 @SerializedName("relevance") var relevance:Int,
                 @SerializedName("follow")    var follow:Int,
                 @SerializedName("request")   var request:Int,
                 @SerializedName("close")     var close:Int)


data class Followers (
        @SerializedName("followers") var users:ArrayList<Users>
)

data class Following (
        @SerializedName("followings") var users:ArrayList<Users>
)