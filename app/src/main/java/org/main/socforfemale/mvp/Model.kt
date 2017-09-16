package org.main.socforfemale.mvp

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.main.socforfemale.base.Base
import org.main.socforfemale.model.PostList
import org.main.socforfemale.model.ResponseData
import retrofit2.Call

/**
 * Created by Michaelan on 6/15/2017.
 */
class Model {

    fun response(request:String) : Observable<ResponseData> = Base.get.APIClient.request(request)
    fun responseCall(request:String) : Call<ResponseData> = Base.get.APIClient.requestCall(request)

    fun uploadPhoto(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Observable<ResponseData> = Base.get.APIClient.uploadPhoto(body,name,id,session)
    fun uploadAudio(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Observable<ResponseData> = Base.get.APIClient.uploadAudio(body,name,id,session)


    fun uploadPhotoDemo(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Call<ResponseData> = Base.get.APIClient.uploadPhotoDemo(body,name,id,session)
    fun uploadAudioDemo(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Call<ResponseData> = Base.get.APIClient.uploadAudioDemo(body,name,id,session)
    fun uploadAvatar(body:MultipartBody.Part,name:RequestBody,id:String,session:String,profil:String) : Observable<ResponseData> = Base.get.APIClient.uploadAvatar(body,name,id,session,profil)



}