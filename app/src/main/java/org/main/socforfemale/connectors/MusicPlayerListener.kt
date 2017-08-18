package org.main.socforfemale.connectors

import org.main.socforfemale.model.Audio

/**
 * Created by Sarvar on 07.08.2017.
 */
interface MusicPlayerListener {


    fun playClick(listSong:ArrayList<Audio>,position:Int)
}