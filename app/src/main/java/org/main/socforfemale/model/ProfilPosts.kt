package org.main.socforfemale.model

import android.graphics.Path
import com.google.gson.annotations.SerializedName

/**
 * Created by Michaelan on 6/27/2017.
 */

data class PostList(@SerializedName("posts")     var posts:ArrayList<Posts>,
                    @SerializedName("followers") var followers:String,
                    @SerializedName("following") var following:String,
                    @SerializedName("postlar") var postlarSoni:String)

data class Posts(      @SerializedName("id")     var id:String,
                       @SerializedName("quote")  var quote:Quote,
                       @SerializedName("audios") var audios:ArrayList<Audio>,
                       @SerializedName("images") var images:ArrayList<Image>,
                       @SerializedName("like")   var like:String,
                       @SerializedName("likes")  var likes:String,
                       @SerializedName("comments")  var comments:String,
                       @SerializedName("time")   var time:String,
                       @SerializedName("user")   var user:PostUser)

data class Audio(@SerializedName("audio_id")     var audioId:String,
                 @SerializedName("post_id")      var postId:String,
                 @SerializedName("duration")     var duration:String,
                 @SerializedName("size")         var size:String,
                 @SerializedName("middle_path")  var middlePath:String,
                 @SerializedName("bitrate")      var bitrate:String,
                 @SerializedName("title")        var title:String,
                 @SerializedName("artist")       var artist:String,
                 @SerializedName("isFeatured")   var isFeatured:Int = -1)

data class Image(@SerializedName("photo_id")     var photoId:String,
                 @SerializedName("post_id")      var postId: String,
                 @SerializedName("image_orig")   var image: String,
                 @SerializedName("image_640")    var image640:String)

data class PostUser(@SerializedName("user_id") var userId:String, @SerializedName("username") var username:String, @SerializedName("photo_150") var photo:String)