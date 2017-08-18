package org.main.socforfemale.model

import android.net.Uri

/**
 * Created by Michaelan on 6/23/2017.
 */
data class PhotoUpload (var uri: Uri, var loaded:Boolean = false, var progress:Int = 0,var onFail:Int = 0)