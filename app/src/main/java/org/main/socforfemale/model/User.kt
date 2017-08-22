package org.main.socforfemale.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 6/18/2017.
 */
data class User (
        @SerializedName("userId")
        var userId:String,
        @SerializedName("session")
        var session:String,
        @SerializedName("token")
        var token:String,
        @SerializedName("username")
        var userName:String,
        @SerializedName("password")
        var password:String,
        @SerializedName("gender")
        var gender:String, // nothing = 0, female = 1, male = 2
        @SerializedName("phone")
        var phoneOrMail:String,
        @SerializedName("smsCode")
        var smsCode:String,
        @SerializedName("first_name")
        var first_name:String,
        @SerializedName("last_name")
        var last_name:String,
        @SerializedName("profilPhoto")
        var profilPhoto:String = "",
        @SerializedName("signType")
        var signType:Int // facebook = 0, vkontakte = 1,sms = 2


)

data class UserInfo(@SerializedName("info")      var info:UserData)

data class UserData(@SerializedName("username")  var username:String,
                    @SerializedName("user_id")   var user_id:String,
                    @SerializedName("photo_org") var photo150:String ,
                    @SerializedName("close")     var close:Int,
                    @SerializedName("block")     var block:Int,
                    @SerializedName("follow")    var follow:Int,
                    @SerializedName("request")    var request:Int){

}