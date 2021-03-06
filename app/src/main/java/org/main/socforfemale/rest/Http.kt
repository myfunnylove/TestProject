package org.main.socforfemale.rest

import android.util.Base64
import org.json.JSONObject
import org.main.socforfemale.pattern.cryptDecorator.AppCrypt
import org.main.socforfemale.pattern.cryptDecorator.B64DecoderCryptDecorator
import org.main.socforfemale.pattern.cryptDecorator.B64EncoderCryptDecorator


/**
 * Created by Michaelan on 6/15/2017.
 */
object Http {

    val PRMS             = "prms"
    val CMD              = "cmd"
    public val BASE_URL  = "http://api.maydon.net/new/"
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
        val CLOSE_PROFIL               = "31"
        val ADD_SONG_TO_PLAYLIST       = "32"
        val GET_PLAYLIST               = "33"

    }


    fun getRequestData(obj: JSONObject, cmd:String):String{

//        val crypt = MCrypt()
//        val prm = MCrypt.bytesToHex(MCrypt().encrypt(obj.toString()))

        val jsObj = JSONObject()

        jsObj.put(PRMS, B64EncoderCryptDecorator(AppCrypt(obj.toString())).getPrm())
        jsObj.put(CMD,cmd)
        jsObj.put("lang","ru")

        return jsObj.toString()
    }

    fun getResponseData(prm:String):String = B64DecoderCryptDecorator(AppCrypt(prm)).getPrm()



}