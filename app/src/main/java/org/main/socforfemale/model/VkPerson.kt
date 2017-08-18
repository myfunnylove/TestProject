package org.main.socforfemale.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Michaelan on 6/12/2017.
 */
data class VkPerson (var firstName:String,
                     var lastName:String,
                     var sex:Int,
                     var birthday:String,
                     var city:String,
                     var photoMaxOrig:String,
                     var online:Boolean,
                     var username:String,
                     var mobilePhone:Boolean,
                     var homePhone:String,
                     var universityName:String,
                     var facultyName:String,
                     var gradutaionYear:String,
                     var status:String,
                     var canPost:Boolean,
                     var canSeeAllPosts:Boolean,
                     var canWritePrivateMessage:Boolean
                     ) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<VkPerson> = object : Parcelable.Creator<VkPerson> {
            override fun createFromParcel(source: Parcel): VkPerson = VkPerson(source)
            override fun newArray(size: Int): Array<VkPerson?> = arrayOfNulls(size)
        }
    }

    constructor(source: Parcel) : this(
    source.readString(),
    source.readString(),
    source.readInt(),
    source.readString(),
    source.readString(),
    source.readString(),
    1 == source.readInt(),
    source.readString(),
    1 == source.readInt(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    source.readString(),
    1 == source.readInt(),
    1 == source.readInt(),
    1 == source.readInt()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(firstName)
        dest.writeString(lastName)
        dest.writeInt(sex)
        dest.writeString(birthday)
        dest.writeString(city)
        dest.writeString(photoMaxOrig)
        dest.writeInt((if (online) 1 else 0))
        dest.writeString(username)
        dest.writeInt((if (mobilePhone) 1 else 0))
        dest.writeString(homePhone)
        dest.writeString(universityName)
        dest.writeString(facultyName)
        dest.writeString(gradutaionYear)
        dest.writeString(status)
        dest.writeInt((if (canPost) 1 else 0))
        dest.writeInt((if (canSeeAllPosts) 1 else 0))
        dest.writeInt((if (canWritePrivateMessage) 1 else 0))
    }
}