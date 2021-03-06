package org.main.socforfemale.resources.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.main.socforfemale.base.Base
import org.main.socforfemale.model.Song
import java.io.File
import android.net.ConnectivityManager
import android.widget.Toast
import android.os.Build
import android.view.WindowManager
import android.graphics.Point
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.NumberKeyListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import org.json.JSONException
import org.json.JSONObject




/**
 * Created by Michaelan on 5/26/2017.
 */
object Functions {

    private var screenWidth = 0
    private var screenHeight = 0

    val TAG:String = "Functions-> "
    fun DPtoPX(dp:Float,ctx: Context):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,ctx.resources.displayMetrics).toInt()
    }
    fun SPtoPX(dp:Float,ctx: Context):Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,dp,ctx.resources.displayMetrics).toInt()
    }
    fun DPtoSP(dp:Float,ctx: Context):Float{
        return ((DPtoPX(dp,ctx)) / SPtoPX(dp,ctx).toFloat()).toFloat()
    }



    fun getSongList(ctx:Context):ArrayList<Song>{
        var songList:ArrayList<Song> = ArrayList()
        var musicCursor: Cursor? = null
        try{
            val contentResolver = ctx.contentResolver
            val musicUri  = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            musicCursor = contentResolver.query(musicUri,null,null,null,null)

            if (musicCursor != null && musicCursor.moveToFirst()){




                songList = ArrayList<Song>()
                val titleColumn:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

                val idColumn:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
                val artistColumn:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

                val sizeColumn:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
                val durationColumn:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val path:Int = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)

                val size = musicCursor.getColumnIndex(MediaStore.Audio.Media.SIZE)

                val length = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)


                do{

                    log.d("size : ${musicCursor.getLong(size)} length: ${musicCursor.getLong(length)}")
                    songList.add(
                            Song(
                                    musicCursor.getLong(idColumn),
                                    musicCursor.getString(titleColumn),
                                    musicCursor.getString(artistColumn),
                                    musicCursor.getLong(durationColumn),
                                    musicCursor.getLong(sizeColumn),
                                    false,
                                    musicCursor.getString(path),
                                    0

                            ))
                }while (musicCursor.moveToNext())
            }
        }catch (e:Exception){
                log.d(e.toString())
            return ArrayList()
        }finally {
            musicCursor!!.close()
            return songList
        }
    }


    fun Long.generateSongDuration():String{
        return "${(this % 60000) / 1000}:${this / 60000}"
    }


    fun checkPermissions(ctx : Activity):Boolean{
        val MULTIPLE_PERMISSIONS = 10
        val permissions = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        )
        var res = 0

        val listPermissionNeeded = ArrayList<String>()

        for (i in permissions){
            res = ContextCompat.checkSelfPermission(Base.get,i)

            if (res != PackageManager.PERMISSION_GRANTED){
                listPermissionNeeded.add(i)
            }
        }

        if (listPermissionNeeded.isNotEmpty())
        {
            val arr:Array<String> = listPermissionNeeded.toTypedArray()
            ActivityCompat.requestPermissions(ctx,arr,MULTIPLE_PERMISSIONS)
            return false
        }else{
            return true
        }
    }



    fun getFile(path:File):MultipartBody.Part?{

        log.d("getFile ${path.absolutePath}")
        val reqFile = RequestBody.create(MediaType.parse("file"),path)


        return MultipartBody.Part.createFormData("upload",path.name,reqFile)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }


    fun show(str:String){
        Toast.makeText(Base.get,str, Toast.LENGTH_SHORT).show()

    }


    fun getScreenHeight(c: Context): Int {
        if (screenHeight === 0) {
            val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenHeight = size.y
        }

        return screenHeight
    }

    fun getScreenWidth(c: Context): Int {
        if (screenWidth == 0) {
            val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            screenWidth = size.x
        }

        return screenWidth
    }

    fun isAndroid5(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }


    @Throws(JSONException::class)
    fun jsonToBundle(jsonObject: JSONObject): Bundle {
        val bundle = Bundle()
        val iter = jsonObject.keys()
        while (iter.hasNext()) {
            val key = iter.next() as String
            val value = jsonObject.getString(key)
            bundle.putString(key, value)
        }
        return bundle
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (activity.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
            }
        }
    }

    var EditCardWatcher: TextWatcher = object : TextWatcher {
        // int len = 0;
        internal var text = ""
        internal var editingBefore = false
        internal var editingOnChanged = false
        internal var editingAfter = false

        override fun afterTextChanged(str: Editable) {
            if (!editingAfter && editingBefore && editingOnChanged) {
                editingAfter = true
                str.replace(0, str.length, text)
                // str.append(text);


                editingBefore = false
                editingOnChanged = false
                editingAfter = false
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                       after: Int) {
            if (!editingBefore) {
                editingBefore = true
                // text = clearText(s.toString());

            }
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int,
                                   count: Int) {
            val d = " "
            if (!editingOnChanged && editingBefore) {
                editingOnChanged = true
                text = clearText(s.toString())
                if (text.length > 4 && text.length <= 8) {
                    text = "${text.substring(0, 4)}$d${text.substring(4, text.length)}"
                } else if (text.length > 8 && text.length <= 12) {

                    text =   "${text.substring(0, 4)}$d${text.substring(4, 8)}$d${text.substring(8, text.length)}"
                } else if (text.length > 12 && text.length <= 16) {
                    text = "${text.substring(0, 4)}$d${text.substring(4, 8)}$d${text.substring(8, 12)}$d${text.substring(12, text.length)}"
                }

            }
        }
    }

    fun clearText(s: String): String {
        var s = s
        s = s.replace("-".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        s = s.trim { it <= ' ' }
        return s
    }

    fun clearText(edit: EditText): String {
        var s = edit.text.toString()
        s = s.replace("-".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        s = s.trim { it <= ' ' }
        return s
    }

    fun clearEdit(edit: EditText): String {
        var s = edit.text.toString()
        s = s.replace("-".toRegex(), "")
        s = s.replace(" ".toRegex(), "")
        s = s.trim { it <= ' ' }
        return s
    }

    var EditCardKey: NumberKeyListener = object : NumberKeyListener() {

        override fun getInputType(): Int {
            return InputType.TYPE_CLASS_NUMBER
        }

        override fun getAcceptedChars(): CharArray {
            return charArrayOf(' ', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0')
        }
    }
}