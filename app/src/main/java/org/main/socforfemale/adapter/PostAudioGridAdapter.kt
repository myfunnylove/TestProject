package org.main.socforfemale.adapter

import android.content.Context
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.bgservice.MusicService
import org.main.socforfemale.connectors.MusicPlayerListener
import org.main.socforfemale.model.Audio
import java.net.URLDecoder

/**
 * Created by Michaelan on 6/28/2017.
 */
class PostAudioGridAdapter(ctx:Context,list:ArrayList<Audio>,musicPlayerListener: MusicPlayerListener) : RecyclerView.Adapter<PostAudioGridAdapter.Holder>() {


    val context                    = ctx
    val audios                     = list
    val inflater                   = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val isVertical                 = true
    val PLAY                       = R.drawable.play
    val PAUSE                      = R.drawable.pause
    val player                     = musicPlayerListener
    override fun getItemCount(): Int {
        return audios.size
    }

    override fun onBindViewHolder(h: Holder?, i: Int) {
        val audio = audios.get(i)







        val playIcon = VectorDrawableCompat.create(Base.get.resources,PLAY,h!!.play.context.theme)

        val pauseIcon = VectorDrawableCompat.create(Base.get.resources,PAUSE,h.play.context.theme)



        h.play.setImageDrawable(playIcon)
        if (audio.middlePath == MusicService.PLAYING_SONG_URL && MusicService.PLAY_STATUS == MusicService.PLAYING){
            h.play.tag = PAUSE
            h.play.setImageDrawable(pauseIcon)
        }

        else{
            h.play.tag = PLAY
            h.play.setImageDrawable(playIcon)
        }

        h.play.setOnClickListener {



            player.playClick(audios,i)
        }

        h.duration.text = audio.duration
        h.title.text    = URLDecoder.decode(audio.artist,"UTF-8")
        h.songName.text = URLDecoder.decode(audio.title,"UTF-8")
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder {

        return Holder(inflater.inflate(R.layout.res_post_audio_item,p0,false))
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {


        var container:ViewGroup = view.findViewById(R.id.container) as ViewGroup
        var title:TextView = view.findViewById(R.id.title) as TextView
        var songName:TextView = view.findViewById(R.id.songName) as TextView
        var play:AppCompatImageView = view.findViewById(R.id.play) as AppCompatImageView

        var duration:TextView = view.findViewById(R.id.duration) as TextView

    }


}