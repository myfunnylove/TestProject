package org.main.socforfemale.rest

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.main.socforfemale.model.ResponseData
import retrofit2.Call
import retrofit2.http.*

interface API {

    @POST("index.php")
    @FormUrlEncoded
    fun request(@Field("data") data:String ) : Observable<ResponseData>

    @POST("index.php")
    @FormUrlEncoded
    fun requestCall(@Field("data") data:String ) : Call<ResponseData>

    @Multipart
    @POST("image.php")
    fun uploadPhoto(@Part file:MultipartBody.Part,
                    @Part("name") name:RequestBody,
                    @Query("user") id:String,
                    @Query("sess") session:String) :Observable<ResponseData>

    @Multipart
    @POST("audio.php")
    fun uploadAudio(@Part file:MultipartBody.Part,
                    @Part("name") name:RequestBody,
                    @Query("user") id:String,
                    @Query("sess") session:String) :Observable<ResponseData>



    @Multipart
    @POST("image.php")
    fun uploadPhotoDemo(@Part file:MultipartBody.Part,
                        @Part("name") name:RequestBody,
                        @Query("user") id:String,
                        @Query("sess") session:String) : Call<ResponseData>

    @Multipart
    @POST("audio.php")
    fun uploadAudioDemo(@Part file:MultipartBody.Part,
                        @Part("name") name:RequestBody,
                        @Query("user") id:String,
                        @Query("sess") session:String) : Call<ResponseData>

    @Multipart
    @POST("image.php")
    fun uploadAvatar(@Part file:MultipartBody.Part,
                     @Part("name") name:RequestBody,
                     @Query("user") id:String,
                     @Query("sess") session:String ,
                     @Query("profile") profil:String) : Observable<ResponseData>
}