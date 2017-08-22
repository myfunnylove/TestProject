package org.main.socforfemale.resources.utils

import org.main.socforfemale.R
import org.main.socforfemale.model.Color

/**
 * Created by Michaelan on 5/19/2017.
 */
object Const {


    var TAG:      String ="DEMO_APP"
    val FEED_FR:  Int    = 0
    val SEARCH_FR:Int    = 1
    val UPLOAD_FR:Int    = 2
    val NOTIF_FR: Int    = 3
    val PROFIL_FR:Int    = 4
    val SIGN_PHONE_OR_MAIL_FR:Int = 5


    /*
    *
    * Feed TYPE
    *
    * */

    val FEED_IMAGE   = 1
    val FEED_QUOTE   = 2
    val FEED_AUDIO   = 3
    val FEED_FAILED  = -1


    val TO_VALUE = "VALUE_IS?";
    val SONG_PICKED = "PICKED_SONG";
    val PUBLISH_IMAGE = "publish_image"



    val TO_POSTS = -1;
    val TO_FOLLOWERS = -2;
    val TO_FOLLOWING = -3;

    val PICK_IMAGE      = 4
    val PICK_AUDIO      = 5
    val PICK_QUOTE      = 6
    val PICK_UNIVERSAL  = 7
    val CHANGE_AVATAR   = 8
    val PICK_CROP_IMAGE = 9
    val SEARCH_USER     = 10
    val FOLLOW          = 11
    val PROFIL_PAGE     = 12
    val REFRESH_FEED    = 13
    val REFRESH_PROFILE_FEED    = 14
    val GO_COMMENT_ACTIVITY = 15

    val TO_FAIL = -1;




    val FROM_MAIN_ACTIVITY = 100;


    val colorPalette = hashMapOf(
                                 0 to Color(R.color.silver,""),
                                 1 to Color(R.color.normalTextColor,""),
                                 2 to Color(R.color.colorPrimaryDark,""),
                                 3 to Color(R.color.wetAsplalt,""),
                                 4 to Color(R.color.midnightBlue,""),
                                 5 to Color(R.color.emerald,""),
                                 6 to Color(R.color.nephrits,""),
                                 7 to Color(R.color.turquoise,""),
                                 8 to Color(R.color.greenSea,""),
                                 9 to Color(R.color.peterRiver,""),
                                 10 to Color(R.color.belizeHole,""),
                                 11 to Color(R.color.ametHist,""),
                                 12 to Color(R.color.wisteria,""),
                                 13 to Color(R.color.sunFlower,""),
                                 14 to Color(R.color.orange,""),
                                 15 to Color(R.color.carrot,""),
                                 16 to Color(R.color.pumpkin,""),
                                 17 to Color(R.color.alizarin,""),
                                 18 to Color(R.color.pomegranate,""),
                                 19 to Color(R.color.asbestos,""),
                                20 to Color(R.color.material_red_900,""),
                                21 to Color(R.color.material_pink_900,""),
                                22 to Color(R.color.material_purple_900,""),
                                23 to Color(R.color.material_deep_purple_900,""),
                                24 to Color(R.color.material_indigo_900,""),
                                25 to Color(R.color.material_blue_900,""),
                                26 to Color(R.color.material_light_blue_600,""),
                                27 to Color(R.color.material_orange_900,""),
                                29 to Color(R.color.material_brown_800,""),
                                30 to Color(R.color.material_grey_400,""),
                                31 to Color(R.color.material_grey_500,""),
                                32 to Color(R.color.material_grey_600,"")
                                )


    /*
    *
    * TEST QUOTE SETTINGS
    *
    *
    * */

    val TEXT_SIZE_DEFAULT = 16f
    val TEXT_SIZE_17 = 19f
    val TEXT_SIZE_22 = 22f


    val unselectedTabs = hashMapOf<Int,Int>(
            FEED_FR to R.drawable.feed,
            SEARCH_FR to R.drawable.search,
            NOTIF_FR to R.drawable.notification,
            PROFIL_FR to R.drawable.account
    )

    val selectedTabs = hashMapOf<Int,Int>(
            FEED_FR to R.drawable.feed_select,
            SEARCH_FR to R.drawable.search_select,
            NOTIF_FR to R.drawable.notification_select,
            PROFIL_FR to R.drawable.account_select
    )
}