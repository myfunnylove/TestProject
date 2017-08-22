package org.main.socforfemale.base

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import org.json.JSONObject
import org.main.socforfemale.connectors.API
import org.main.socforfemale.resources.utils.Functions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


/**
 * Created by Michaelan on 6/15/2017.
 */
object Http {

    val PRMS             = "prms"
    val CMD              = "cmd"
    public val BASE_URL         = "http://api.maydon.net/new/"
    /*
    *
    *
    *
    *
    * QUERY CMD'S
    *
    *
    *
    *
    * */
    object CMDS{
        val TELEFONNI_JONATISH         = "1"
        val SMSNI_JONATISH             = "2"
        val LOGIN_YOQLIGINI_TEKSHIRISH = "3"
        val ROYXATDAN_OTISH            = "4"
        val LOGIN_PAYTI                = "5"
        val FB_ORQALI_LOGIN            = "6"
        val VK_ORQALI_LOGIN            = "7"
        val FB_VA_VK_ORQALI_REG        = "8"
        val POST                       = "9"
        val MY_POSTS                   = "10"
        val CHANGE_AVATAR              = "00"
        val FOLLOW                     = "13"
        val USER_INFO                  = "15"
        val SEARCH_USER                = "17"
        val LIKE_BOSISH                = "11"
        val FEED                       = "22"
        val GET_FOLLOWERS              = "14"
        val GET_FOLLOWING              = "16"
        val WRITE_COMMENT              = "23"
        val GET_COMMENT_LIST           = "24"
        val DELETE_POST                = "25"
        val CHANGE_POST                = "26"
        val CHANGE_USER_SETTINGS       = "27"
    }


    fun getRequestData(obj: JSONObject, cmd:String):String{

//        val crypt = MCrypt()
//        val prm = MCrypt.bytesToHex(MCrypt().encrypt(obj.toString()))

        val jsObj = JSONObject()

        jsObj.put(PRMS, String(Base64.encode(obj.toString().toByteArray(),0)).replace("\n",""))
        jsObj.put(CMD,cmd)
        jsObj.put("lang","ru")

        return jsObj.toString()
    }

    fun getResponseData(prm:String):String = String(Base64.decode(prm,0))



}