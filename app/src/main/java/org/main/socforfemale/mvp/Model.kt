package org.main.socforfemale.mvp

import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.main.socforfemale.base.Http
import org.main.socforfemale.model.ResponseData
import org.main.socforfemale.resources.utils.log
import retrofit2.Call

/**
 * Created by Michaelan on 6/15/2017.
 */
class Model {

    fun response(request:String) : Observable<ResponseData> = Http.getRestService().request(request)
    fun responseCall(request:String) : Call<ResponseData> = Http.getRestService().requestCall(request)

    fun uploadPhoto(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Observable<ResponseData> = Http.getRestService().uploadPhoto(body,name,id,session)
    fun uploadAudio(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Observable<ResponseData> = Http.getRestService().uploadAudio(body,name,id,session)


    fun uploadPhotoDemo(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Call<ResponseData> = Http.getRestService().uploadPhotoDemo(body,name,id,session)
    fun uploadAudioDemo(body:MultipartBody.Part,name:RequestBody,id:String,session:String) : Call<ResponseData> = Http.getRestService().uploadAudioDemo(body,name,id,session)
    fun uploadAvatar(body:MultipartBody.Part,name:RequestBody,id:String,session:String,profil:String) : Observable<ResponseData> = Http.getRestService().uploadAvatar(body,name,id,session,profil)

}