package org.main.socforfemale.mvp

import android.support.v7.widget.AppCompatEditText
import com.google.gson.Gson
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.rest.Http
import org.main.socforfemale.model.UserInfo
import org.main.socforfemale.pattern.builder.SessionOut
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.log
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


class Presenter(viewer: Viewer, modeler:Model,context:BaseActivity) :IPresenter {

    val view:Viewer = viewer

    val model:Model = modeler
    val context = context
    override fun requestAndResponse(data:JSONObject, cmd:String){


        log.v("REQUEST =========>")
        log.v("JSON DATA :")
        log.e(data.toString())
        log.v("CMD: ")
        log.e(cmd)
        log.v("REQUEST =========;")


        view.initProgress()
        view.showProgress()
                 Observable.just(model.response(Http.getRequestData(data,cmd)))

                           .subscribeOn(Schedulers.io())

                            .flatMap({res -> res})
                            .flatMap({
                             res ->

                                if(res.res == "0" && (cmd == Http.CMDS.LOGIN_PAYTI || cmd == Http.CMDS.FB_ORQALI_LOGIN || cmd == Http.CMDS.VK_ORQALI_LOGIN )){

                                 val response = JSONObject(Http.getResponseData(res.prms))

                                 val user = Base.get.prefs.getUser()

                                 user.userId  = response.optString("user_id")
                                 user.session = response.optString("session")
                                    Base.get.prefs.setUser(user)

                                 val reqObj = JSONObject()
                                     reqObj.put("user_id",user.userId)
                                     reqObj.put("user",   user.userId)
                                     reqObj.put("session",user.session)


                                     log.d("http zapros $cmd resultat: $res")
                                     log.d("send data for user info data: ${reqObj}")
                                        model.response(Http.getRequestData(reqObj, Http.CMDS.USER_INFO))
                                    }else{
                                        Observable.just(res)
                                    }
                         })
                         .flatMap({infoUser ->
                             log.d("flatmap ishladi: RES: ${infoUser.res} IN PRM: ${Http.getResponseData(infoUser.prms)}")
                             if (infoUser.res == "0" && (cmd == Http.CMDS.LOGIN_PAYTI || cmd == Http.CMDS.FB_ORQALI_LOGIN || cmd == Http.CMDS.VK_ORQALI_LOGIN )){



                                 val user = Base.get.prefs.getUser()

                                 val userInfo     = Gson().fromJson<UserInfo>(Http.getResponseData(infoUser.prms),UserInfo::class.java)
                                 user.userName    = userInfo.info.username
                                 user.profilPhoto = if (userInfo.info.photo150.isNullOrEmpty()) "" else userInfo.info.photo150
                                 user.close       = userInfo.info.close
                                         //TODO GENDER AND NAME SHOULD BE GET
                                 Base.get.prefs.setUser(user)
                                 Observable.just(infoUser)
                             }else{

                             Observable.just(infoUser)

                            }
                         })

                           .cache()
                           .observeOn(AndroidSchedulers.mainThread())
                           .subscribe({
                                response ->
                               log.d("CMD : ${cmd} \n RES: ${response.res} \n IN PRM ${Http.getResponseData(response.prms)}")



                               view.hideProgress()


                                when(response.res){

                                    "0"    -> view.onSuccess(cmd, Http.getResponseData(response.prms))
                                    "1996" -> view.onFailure("",Base.get.resources.getString(R.string.error_no_type))
                                    "96"   -> {
                                        val sesion = SessionOut.Builder(context)
                                                .setErrorCode(96)
                                                .build()
                                        sesion.out()
                                    }
                                     else  -> {
                                         if (response.message != "null")
                                              view.onFailure(cmd,response.message)
                                         else
                                              view.onFailure("",Base.get.resources.getString(R.string.error_something))
                                     }

                                }

                             },{
                                    fail ->
                                    log.d("RESPONSE FAILER =========>")
                                    log.e(fail.toString())
                                                  view.hideProgress()
                               when (fail) {
                                   is SocketTimeoutException ->  view.onFailure("",Base.get.resources.getString(R.string.internet_conn_error))
                                   is UnknownHostException ->    view.onFailure("",Base.get.resources.getString(R.string.internet_conn_error))
                                   is HttpException ->           view.onFailure("",Base.get.resources.getString(R.string.internet_conn_error))
                                   else -> {
                                       view.onFailure("",Base.get.resources.getString(R.string.error_something))
                                   }
                               }
                             })


    }


    fun filterLogin(editText :AppCompatEditText){
        RxTextView.textChangeEvents(editText)
                .delay(3, TimeUnit.MILLISECONDS)
                .filter { beforeText -> beforeText.text().toString().length >= 6 }
                .map { filteredText ->

                    val obj = JSONObject()
                    obj.put("username",filteredText.text().toString())


                 }
                .flatMap { data ->
                    log.d("Data : befor decrypt -> $data")
                    model.response(Http.getRequestData(data, Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH)) }
                .cache()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ( {

                    response ->
                    log.d("Data : succes from -> ${Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH} $response")

                    if (response.res == "0") view.onSuccess(Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH,"" )
                        else view.onFailure(Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH,Base.get.resources.getString(R.string.error_something))

                },{
                    fail ->
                    log.d("Data : fail from -> ${Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH}")

                    when (fail) {
                        is UnknownHostException ->{
                            view.onFailure(Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH,Base.get.resources.getString(R.string.internet_conn_error))

                        }
                        else -> {
                            view.onFailure(Http.CMDS.LOGIN_YOQLIGINI_TEKSHIRISH,Base.get.resources.getString(R.string.error_something))
                        }
                        }


                } )
    }




    fun uploadPhoto(body: MultipartBody.Part){
        val user = Base.get.prefs.getUser()
        log.d("upload file ketvotti: ${body.body()!!}")
        val name = RequestBody.create(MediaType.parse("text/plain"),"image/*")
        Observable.just(model.uploadPhoto(body,name,user.userId,user.session))
                .subscribeOn(Schedulers.io())
                .flatMap({res -> res})
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result ->log.d("${result}")
                    if (result.res == "0"){
                        view.onSuccess("${Const.PICK_IMAGE}", Http.getResponseData(result.prms))
                    }
                },{
                    err -> log.d(err.toString())
                })
    }

    fun uploadAvatar(body: MultipartBody.Part){
        val user = Base.get.prefs.getUser()
        log.d("upload file ketvotti: ${body.body()!!}")
        val name = RequestBody.create(MediaType.parse("text/plain"),"image/*")
        Observable.just(model.uploadAvatar(body,name,user.userId,user.session,"avatar"))
                .subscribeOn(Schedulers.io())
                .flatMap({res ->
                    log.d("$res")
                    res})
                .filter { filt -> filt.res == "0" }
                .flatMap {

                    val reqObj = JSONObject()
                    reqObj.put("user_id",user.userId)
                    reqObj.put("user",   user.userId)
                    reqObj.put("session",user.session)

                    model.response(Http.getRequestData(reqObj, Http.CMDS.USER_INFO))
                 }

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    result ->
                    log.d("FROM UPLOAD AVATAR")
                    log.d("$result")
                    log.d(Http.getResponseData(result.prms))
                    if (result.res == "0"){

                        val userInfo     = Gson().fromJson<UserInfo>(Http.getResponseData(result.prms),UserInfo::class.java)
                        user.userName    = userInfo.info.username
                        user.profilPhoto = userInfo.info.photo150
                        Base.get.prefs.setUser(user)
                        view.onSuccess(Http.CMDS.CHANGE_AVATAR, Http.getResponseData(result.prms))
                    }
                },{
                    err -> log.d(err.toString())
                })
    }


}