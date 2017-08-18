package org.main.socforfemale.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Michaelan on 5/27/2017.
 */
data class Song(var songId:Long,var songTitle:String, var songArtist:String,var songDuration:Long, var songSize:Long,var selected:Boolean,var songPath:String,var bitRate:Long,  var loaded:Boolean = false, var progress:Int = 0,var onFail:Int = 0) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Song> = object : Parcelable.Creator<Song> {
            override fun createFromParcel(source: Parcel): Song = Song(source)
            override fun newArray(size: Int): Array<Song?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readLong(),
    source.readString(),
    source.readString(),
    source.readLong(),
    source.readLong(),
    1 == source.readInt(),
    source.readString(),
    source.readLong(),
    1 == source.readInt(),
    source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(songId)
        dest.writeString(songTitle)
        dest.writeString(songArtist)
        dest.writeLong(songDuration)
        dest.writeLong(songSize)
        dest.writeInt((if (selected) 1 else 0))
        dest.writeString(songPath)
        dest.writeLong(bitRate)
        dest.writeInt((if (loaded) 1 else 0))
        dest.writeInt(progress)
    }
}