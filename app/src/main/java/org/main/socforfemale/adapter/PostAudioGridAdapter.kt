package org.main.socforfemale.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.bgservice.MusicService
import org.main.socforfemale.connectors.MusicPlayerListener
import org.main.socforfemale.model.Audio
import org.main.socforfemale.model.ResponseData
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.rest.Http
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URLDecoder


class PostAudioGridAdapter(ctx:Context,list:ArrayList<Audio>,musicPlayerListener: MusicPlayerListener,model: Model) : RecyclerView.Adapter<PostAudioGridAdapter.Holder>() {


    val context                    = ctx
    val audios                     = list
    val inflater                   = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val isVertical                 = true
    val PLAY                       = R.drawable.play
    val PAUSE                      = R.drawable.pause
    val model                      = model
    var user                       = Base.get.prefs.getUser()

    val player                     = musicPlayerListener
    val notFeatured                = VectorDrawableCompat.create(Base.get.resources,R.drawable.plus,context.theme)
    val featured                   = VectorDrawableCompat.create(Base.get.resources,R.drawable.playlist_remove,context.theme);


    val featureMap                 = mapOf<Int,VectorDrawableCompat>( 0 to notFeatured!!,1 to featured!! )

    override fun getItemCount(): Int {
        return audios.size
    }

    override fun onBindViewHolder(h: Holder?, i: Int) {
        val audio = audios.get(i)


        log.d("audio $audio")




        val playIcon = VectorDrawableCompat.create(Base.get.resources,PLAY,h!!.play.context.theme)

        val pauseIcon = VectorDrawableCompat.create(Base.get.resources,PAUSE,h.play.context.theme)

        log.d("after notify ${audio.isFeatured}")

       if (audio.isFeatured != -1){
           h.addFavorite.setImageDrawable(featureMap.get(audio.isFeatured))
           h.addFavorite.tag = featureMap.get(audio.isFeatured)
       }else{
           h.addFavorite.visibility = View.GONE
       }
        h.play.setImageDrawable(playIcon)

        if (audio.middlePath == MusicService.PLAYING_SONG_URL && MusicService.PLAY_STATUS == MusicService.PLAYING){
            h.play.tag = PAUSE
            h.play.setImageDrawable(pauseIcon)
        }else{
            h.play.tag = PLAY
            h.play.setImageDrawable(playIcon)
        }

        h.addFavorite.setOnClickListener{

            val reqObj = JSONObject()

            reqObj.put("user_id",user.userId)
            reqObj.put("session",user.session)
            reqObj.put("audio",   audio.audioId)

            model.responseCall(Http.getRequestData(reqObj, Http.CMDS.ADD_SONG_TO_PLAYLIST))
                    .enqueue(object : Callback<ResponseData>{
                        override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                            log.d("from add audio from favorite $t")

                        }

                        override fun onResponse(call: Call<ResponseData>?, response: Response<ResponseData>?) {
                           try{
                               val resp = response!!.body()!!
                               log.d("from add audio from favorite $resp")
                               if (resp.res == "0"){

                                   if(h.addFavorite.tag == featureMap.get(0)){
                                       h.addFavorite.setImageDrawable(featureMap.get(1))
                                       h.addFavorite.tag = featureMap.get(1)
                                       audios.get(h.adapterPosition).isFeatured = 1
                                       log.d("to notify ${h.adapterPosition}")

                                       notifyItemChanged(h.adapterPosition)
                                       notifyDataSetChanged()
                                   }else{
                                       h.addFavorite.setImageDrawable(featureMap.get(0))
                                       h.addFavorite.tag = featureMap.get(0)
                                       audios.get(h.adapterPosition).isFeatured = 0
                                       notifyItemChanged(h.adapterPosition)
                                       notifyDataSetChanged()

                                   }

                               }
                           }catch (e:Exception){}
                        }

                    })


            notifyItemChanged(h.adapterPosition)
        }

        h.play.setOnClickListener {


            log.d("audio clicked => ${audios.get(i)}")
            player.playClick(audios,i)
        }



        h.title.text    = URLDecoder.decode(audio.artist,"UTF-8")
        h.songName.text = URLDecoder.decode(audio.title,"UTF-8")
        h.duration.text = "(${audio.duration})"
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder =
            Holder(inflater.inflate(R.layout.res_post_audio_item,p0,false))

    class Holder(view: View) : RecyclerView.ViewHolder(view) {


        var container:ViewGroup = view.findViewById(R.id.container) as ViewGroup
        var title:TextView = view.findViewById(R.id.title) as TextView
        var duration:TextView = view.findViewById(R.id.duration) as TextView
        var songName:TextView = view.findViewById(R.id.songName) as TextView
        var play:AppCompatImageView = view.findViewById(R.id.play) as AppCompatImageView

        var addFavorite:AppCompatImageView = view.findViewById(R.id.addFavorite) as AppCompatImageView

    }


}