package org.main.socforfemale.adapter

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.rest.Http
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.model.PhotoUpload
import org.main.socforfemale.model.ProgressRequestBody
import org.main.socforfemale.model.ResponseData
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.resources.customviews.imageuploadmask.ImageUploadMask
import org.main.socforfemale.resources.customviews.imageuploadmask.ShapeMask
import org.main.socforfemale.resources.utils.CustomAnim
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.publish.PublishUniversalActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/30/2017.
 */
class PickedPhotoAdapter(ctx:Context,adapterClicker:AdapterClicker,listPhoto:ArrayList<PhotoUpload>) : RecyclerView.Adapter<PickedPhotoAdapter.Holder>() {


    var context:Context = ctx
    var clicker:AdapterClicker = adapterClicker
    var inflater:LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var list = listPhoto
    var isFirst = true
    object Photo{
        val UPLOAD = 0
        val UPDATE = 1
    }
    val user = Base.get.prefs.getUser()
    val name = RequestBody.create(MediaType.parse("text/plain"),"test_image")

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder {
       return Holder(inflater.inflate(R.layout.res_photo_item,p0,false))
    }

    override fun onBindViewHolder(h: Holder?, p1: Int) {
       val photo:PhotoUpload = list.get(p1)

        log.d("$photo")


        h!!.pr!!.setProgress(100f)
        val reqFile = ProgressRequestBody(File(photo.uri.path), object : ProgressRequestBody.UploadCallbacks{

            override fun onProgressUpdate(percentage: Int) {


                if (p1 != -1){
                    list.get(p1).progress = percentage
                    h.pr!!.setProgress(list.get(p1).progress.toFloat())
                }

            }

            override fun onError() {
                log.d("onerror")

            }

            override fun onFinish() {
                log.d("onfinish")
            }

        }, ProgressRequestBody.IMAGE_ALL)

        val body = MultipartBody.Part.createFormData("upload", File(photo.uri.path).name,reqFile)

        log.d("file: ${body.body()!!.contentType()}")

        if (p1 == list.size - 1){
            if (isFirst) CustomAnim.setScaleAnimation(h.container,500)
        }

        h.errorImg.visibility = View.GONE


//        if(h.image.tag == null || h.image.tag == photo.uri){


            Picasso.with(context)
                    .load(photo.uri)
                    .error(R.drawable.image)
                    .into(h.image)
//            h.image.tag = photo.uri
//        }

        h.container.setOnClickListener {







            if (!photo.loaded ){
                h.errorImg.visibility = View.GONE
                val call: Call<ResponseData> = Model().uploadPhotoDemo(body,name,user.userId,user.session)
                call.uploadAudioByUri(p1,photo.uri.path)
            }


        }









        if (!photo.loaded ){

            if(photo.onFail == 0){
                val call: Call<ResponseData> = Model().uploadPhotoDemo(body,name,user.userId,user.session)
                call.uploadAudioByUri(p1,photo.uri.path)
            }else{
                h.errorImg.visibility = View.VISIBLE
            }

        }else{

            h.pr!!.setProgress(100f)

        }
    }

    fun swapItems(listPhoto:ArrayList<PhotoUpload>){
        list = listPhoto
        notifyItemInserted(list.size)
    }
    class Holder(itemView:View) : RecyclerView.ViewHolder(itemView) {
        var image:AppCompatImageView by Delegates.notNull<AppCompatImageView>()
        var container:ViewGroup by Delegates.notNull<ViewGroup>()
        var errorImg:AppCompatImageView by Delegates.notNull<AppCompatImageView>()

        var pr:ImageUploadMask? = null
        init {
            image = itemView.findViewById(R.id.image) as AppCompatImageView
            errorImg = itemView.findViewById(R.id.errorImg) as AppCompatImageView
            container = itemView.findViewById(R.id.container) as ViewGroup
            pr = ImageUploadMask.Builder(Base.get)
                    .bind(image)
                    .textColorInt(Color.WHITE)
                    .maskColorInt(Color.argb(90,0,0,0))
                    .textSize(16f)
                    .cornerRadius(4f)
                    .direction(ShapeMask.Direction.DTU)
                    .margin(1f)
                    .build()
//            progress.setTextColor(Color.WHITE)
//            progress.setTextSize(16f)
//            progress.setMaskColor(Color.argb(50,0,0,0))
//            progress.setDirection(ShapeMask.Direction.LTR)
//            progress.setCornerRadius(4f)
//            progress.setMargin(1f)

        }
    }


    fun setError(position:Int,path: String){
        isFirst = false

        list.forEach{
            item -> if (item.uri.path == path) {
            log.d("${item.uri.path} this photo cannot upload")
            item.loaded = false
            item.onFail ++
        }
        }
        notifyItemChanged(position)

    }

    fun setProgress(position:Int,model: PhotoUpload){
        log.d("update holder: $position $model")
        isFirst = false
        list.set(position,model)

        notifyItemChanged(position)
    }

    fun Call<ResponseData>.uploadAudioByUri (id:Int, path:String){






        PublishUniversalActivity.loading = true
        this.enqueue(object : Callback<ResponseData> {
            override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                log.d("fail $t")
                PublishUniversalActivity.loading = false

                setError(id,path)

            }

            override fun onResponse(call: Call<ResponseData>?, response: Response<ResponseData>?) {
                log.d("result ${response!!} ")
                PublishUniversalActivity.loading = false

//                log.d("prm ${Http.getResponseData(response.body()!!.prms)} ")
                try{

                    if (response.body()!!.res == "0")
                    {
                        try{
                            val resObj = JSONObject(Http.getResponseData(response.body()!!.prms))

                            val audioId = resObj.optString("image_id")

                            PublishUniversalActivity.loadedImagesIds.add(audioId)

                            val song = list.get(id)
                            song.progress = 100
                            song.onFail = 0
                            song.loaded = true


                            setProgress(id,song)
                        }catch (e:Exception){
                            log.d("unzip image_id exception $e")
                        }

                    }else{

                        setError(id,path)

                    }
                }catch (e:Exception){
                    setError(id,path)

                }

            }

        })



    }
}