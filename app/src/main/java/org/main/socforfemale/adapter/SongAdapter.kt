package org.main.socforfemale.adapter

import android.content.Context
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatRadioButton
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import org.main.socforfemale.R
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.model.Song
import org.main.socforfemale.resources.customviews.SwitchButton
import org.main.socforfemale.resources.utils.log
import java.text.DecimalFormat
import kotlin.properties.Delegates


class SongAdapter(clicker:AdapterClicker, ctx:Context, list:ArrayList<Song>) : RecyclerView.Adapter<SongAdapter.Adapter>() {
    val adapterClicker = clicker
    val context = ctx
    var songs = list
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onBindViewHolder(h: Adapter?, p1: Int) {
        val song = songs.get(p1)
        log.d("${song.songId} ${song.songTitle} ${song.songArtist} ${song.songDuration} ${song.songSize}")


        h!!.songArtist.setText(song.songArtist)
        h  .songName.setText(song.songTitle)


        if (song.selected) h.songCheck.isChecked = true else h.songCheck.isChecked = false
        h.songDuration.setText("${song.songDuration.formateMilliSeccond()} | ")
        h.songSize.setText(song.songSize.getSize())
        h.container.setOnClickListener {
                    selecterSong(p1,!h.songCheck.isChecked)
                    adapterClicker.click(p1)
        }


//        h.songCheck.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
//            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//                    selecterSong(p1,!isChecked)
//                    adapterClicker.click(p1)
//            }
//
//        })
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Adapter {
        return Adapter(inflater.inflate(R.layout.res_song_item,p0,false))
    }

    class Adapter(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var songName     by Delegates.notNull<TextView>()
        var songArtist   by Delegates.notNull<TextView>()
        var songSize     by Delegates.notNull<TextView>()
        var songDuration by Delegates.notNull<TextView>()
        var container    by Delegates.notNull<ViewGroup>()
        var songCheck    by Delegates.notNull<AppCompatRadioButton>()
        init {
            songName = itemView.findViewById(R.id.songName) as TextView
            songArtist = itemView.findViewById(R.id.songArtist) as TextView
            songSize = itemView.findViewById(R.id.songSize) as TextView
            songDuration = itemView.findViewById(R.id.songDuration) as TextView
            songArtist = itemView.findViewById(R.id.songArtist) as TextView
            songCheck = itemView.findViewById(R.id.songCheck) as AppCompatRadioButton
            container = itemView.findViewById(R.id.container) as ViewGroup

        }
    }

    fun Long.getSize():String{
        val df = DecimalFormat("0.00")

        val sizeKb = 1024.0f
        val sizeMo = sizeKb * sizeKb
        val sizeGo = sizeMo * sizeKb
        val sizeTerra = sizeGo * sizeKb


        if (this < sizeMo)
            return df.format(this / sizeKb) + " Kb"
        else if (this < sizeGo)
            return df.format(this / sizeMo) + " Mb"
        else if (this < sizeTerra)
            return df.format(this / sizeGo) + " Gb"

        return ""
    }


    fun Long.formateMilliSeccond(): String {
        var finalTimerString:String = ""
        var secondsString:String

        // Convert total duration into time
        val hours = (this / (1000 * 60 * 60)).toInt()
        val minutes = (this % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (this % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours.toString() + ":"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString

        //      return  String.format("%02d Min, %02d Sec",
        //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
        //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
        //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

        // return timer string
        return finalTimerString
    }


    fun selecterSong(pos:Int,isCheck:Boolean){
        var res:ArrayList<Song> = ArrayList()
        for (i in songs.indices){

            val song:Song = songs.get(i)
                song.selected = false

            res.add(song)

        }

        res.get(pos).selected = isCheck
        songs = res

        this.notifyDataSetChanged()
    }
}