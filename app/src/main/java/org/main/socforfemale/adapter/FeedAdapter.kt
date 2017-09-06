package org.main.socforfemale.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.gson.Gson
import com.nineoldandroids.animation.AnimatorSet
import com.squareup.picasso.Picasso
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.Http
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.connectors.MusicPlayerListener
import org.main.socforfemale.model.*
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.resources.customviews.CircleImageView
import org.main.socforfemale.resources.customviews.CustomManager
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.CommentActivity
import org.main.socforfemale.ui.activity.MainActivity
import org.main.socforfemale.ui.activity.SettingsActivity
import org.main.socforfemale.ui.fragment.FFFFragment
import org.main.socforfemale.ui.fragment.FeedFragment
import org.main.socforfemale.ui.fragment.MyProfileFragment
import org.main.socforfemale.ui.fragment.ProfileFragment
import org.ocpsoft.prettytime.PrettyTime
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.properties.Delegates


class FeedAdapter(context: Context,
                  feedsMap: PostList,

                  adapterClicker: AdapterClicker,
                  musicPlayerListener: MusicPlayerListener,
                  profilOrFeed:Boolean = false,
                  followType:String = "",
                  postUser:PostUser? = null

                  ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var ctx                   = context
    var feeds                 = feedsMap
    var inflater              = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var clicker               = adapterClicker
    val like                  = R.drawable.like_select
    val unLike                = R.drawable.like
    var profile               = Base.get.prefs.getUser()
    val model                 = Model()
    val pOrF                  = profilOrFeed
    val FOLLOW_TYPE           = followType
    var user                  = Base.get.prefs.getUser()
    val postUser              = postUser
    var lastAnimationPosition = -1
    var itemsCount            = 0
    var activity:Activity?    = null
    var disableAnimation      = false
    var cachedLists           = HashMap<String,String>()
    var changeId              = -1
    val player                = musicPlayerListener

    companion object {

        val ANIMATED_ITEM_COUNT        = 0
        val HEADER                     = 1
        val BODY                       = 2
        val ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button"
        val ACTION_LIKE_IMAGE_CLICKED  = "action_like_image_button"
        val VIEW_TYPE_DEFAULT          = 1
        val VIEW_TYPE_LOADER           = 2
        val ACCELERATE_INTERPOLATOR    = AccelerateInterpolator()
        val OVERSHOOT_INTERPOLATOR     = OvershootInterpolator(4f)
        val likeAnimations             = HashMap<RecyclerView.ViewHolder,AnimatorSet>()

    }


    fun View.runEnterAnimation(position:Int){
        if(disableAnimation || position < lastAnimationPosition){
            return
        }


        if (position > ANIMATED_ITEM_COUNT){
            lastAnimationPosition = position
            this.translationY =Functions.getScreenHeight(ctx).toFloat()
            this.animate()
                    .translationY(0f)
                    .setInterpolator(DecelerateInterpolator(3f))
                    .setDuration(700)
                    .start()
        }
    }

    override fun getItemCount(): Int {

        return feeds.posts.size
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(p0: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {

       if (viewType == HEADER){
           return ProfileHeaderHolder(inflater.inflate(R.layout.user_profil_header, p0!!, false))

       }else if(viewType == BODY){
           return  Holder(inflater.inflate(R.layout.res_feed_block_image, p0!!, false))
       }else {
           return  Holder(inflater.inflate(R.layout.res_feed_block_image, p0!!, false))
       }

    }

    override fun getItemViewType(position: Int): Int {

        if (pOrF){
            if (position == 0) return HEADER else return BODY
        }else{
            return BODY
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, i: Int) {

        val type = getItemViewType(i)
         holder!!.itemView.runEnterAnimation(i)

        if (type == BODY) {
            val h = holder as Holder
            val post = feeds.posts.get(i)
            log.e("=============== posts count => ${feeds.posts.size}")

            log.wtf("=============== start => ")

            log.wtf("post id:       ${post.id}")
            log.wtf("post audios:   ${post.audios}")
            log.wtf("post images:   ${post.images}")
            log.wtf("post quote:    ${post.quote}")
            log.wtf("post comments: ${post.comments}")
            log.wtf("post like:     ${post.like}")
            log.wtf("post likes:    ${post.likes}")
            log.wtf("post time:     ${post.time}")
            log.wtf("post user:     ${post.user}")
            log.wtf("=============== end ; ")


            val icon: VectorDrawableCompat?
            if (post.like == "0")
                icon = VectorDrawableCompat.create(Base.get.resources, unLike, h.likeIcon.context.theme)
            else
                icon = VectorDrawableCompat.create(Base.get.resources, like, h.likeIcon.context.theme)

            h.likeIcon.setImageDrawable(icon)

            h.likeCount.setCurrentText(post.likes)



            if (likeAnimations.containsKey(h)){
                likeAnimations.get(h)!!.cancel()
            }

            likeAnimations.remove(h);

//            h.commentCount.text = post.comments

            if(pOrF == true && changeId == i){

                h.quote.visibility      = View.GONE
                h.quoteEdit.visibility  = View.VISIBLE
                h.quoteEdit.setText(post.quote.text)
                h.sendChange.visibility = View.VISIBLE
                h.sendChange.setOnClickListener {

                    val quote:Quote = post.quote
                    quote.text = h.quoteEdit.text.toString()

                    val js = JSONObject()
                    js.put("post_id",post.id)
                    js.put("quote", JSONObject(Gson().toJson(quote)))
                    js.put("user_id", profile.userId )
                    js.put("session", profile.session)
                    log.d ("changequote send data $js")
                    model.responseCall(Http.getRequestData(js, Http.CMDS.CHANGE_POST))
                            .enqueue(object : Callback<ResponseData>{
                                override fun onResponse(p0: Call<ResponseData>?, response: Response<ResponseData>?) {
                                    try{
                                        log.d("result change quote success $response")
                                        log.d("result change quote success ${response!!.body()}")
                                        log.d("result after changed ${feeds.posts.get(changeId)}")
                                        if (response.body()!!.res == "0"){
                                            feeds.posts.get(changeId).quote.text = h.quoteEdit.text.toString()
                                            val newChange = changeId
                                            changeId = -1
                                            notifyItemChanged(newChange)
                                        }
                                    }catch (e :Exception){

                                    }

                                }

                                override fun onFailure(p0: Call<ResponseData>?, p1: Throwable?) {

                                    log.d("result change quote failer $p1")
                                }

                            })
                }
            }else{
                h.quote.visibility     = View.VISIBLE
                h.quote.text           = post.quote.text
                h.quoteEdit.visibility = View.GONE
                h.quoteEdit.clearComposingText()
                h.sendChange.visibility = View.GONE

            }

            var photo = ""
            try {
                photo = if (post.user.photo.startsWith("http")) post.user.photo else Http.BASE_URL + post.user.photo
            } catch (e: Exception) {
                photo = "http"
            }

            Picasso.with(ctx)
                    .load(photo)
                    .error(VectorDrawableCompat.create(Base.get.resources, R.drawable.account,null))
                    .into(h.avatar)

            if (h.quote.tag == null || h.quote.tag != post.id) {

                h.quote.tag = post.id

                h.username.text = post.user.username
                //TODO

                h.name.visibility = View.GONE
                val prettyTime = PrettyTime()
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val date2 = formatter.parse(post.time) as Date



                h.time.text = prettyTime.format(date2)



                if (post.quote.textSize != "") {
                    try {
                        h.quote.textSize = post.quote.textSize.toFloat()
                    } catch (e: Exception) {
                    }
                }
                try {

                    h.quote.setTextColor(ContextCompat.getColor(Base.get, Const.colorPalette.get(post.quote.textColor.toInt())!!.drawable))

                } catch (e: Exception) {

                }



                if (post.images.size > 0) {


                       h.images.visibility = View.VISIBLE

                       var span = (post.images.size - 1)

                       if ((post.images.size > 1)) {
                           if (post.images.size == 2) {
                               span = 2
                           } else {
                               span = (post.images.size - 1)
                           }
                       } else {
                           span = 1
                       }

                       val manager = CustomManager(ctx, span)
                       val adapter = PostPhotoGridAdapter(ctx, post.images)

                       manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                           override fun getSpanSize(i: Int): Int {
                               if (i == 0) {
                                   if (post.images.size == 2)
                                       return 1
                                   else
                                       return (manager.spanCount)
                               } else return 1
                           }

                       }

                       h.images.layoutManager = manager
                       h.images.setHasFixedSize(true)
                       h.images.adapter = adapter




                } else {
                    h.images.visibility = View.GONE
                }

                if (post.audios.size > 0) {
                    h.audios.visibility = View.VISIBLE

                    val span = 1


                    val manager = CustomManager(ctx, span)
                    val adapter = PostAudioGridAdapter(ctx, post.audios,object :MusicPlayerListener{
                        override fun playClick(listSong: ArrayList<Audio>, position: Int) {

                            try{
                                player.playClick(listSong,position)


                                if (FeedFragment.playedSongPosition != -1 ){
                                    log.d("position $i => ${FeedFragment.playedSongPosition} $position")

                                    FeedFragment.cachedSongAdapters!!.get(FeedFragment.playedSongPosition)!!.notifyDataSetChanged()

                                    FeedFragment.cachedSongAdapters!!.get(i)!!.notifyDataSetChanged()

                                }else{

                                    FeedFragment.cachedSongAdapters!!.get(i)!!.notifyDataSetChanged()

                                }

                                FeedFragment.playedSongPosition = i
                            }catch (e :Exception){

                            }

                        }

                    })
                     FeedFragment.cachedSongAdapters!!.put(i,adapter)
//            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
//                override fun getSpanSize(i: Int): Int {
//                    if (i == 0){
//                        if (post.images.size == 2)
//                            return 1
//                        else
//                            return (manager.spanCount)
//                    } else return 1
//                }
//
//            }

                    h.audios.layoutManager = manager
                    h.audios.setHasFixedSize(true)
                    h.audios.adapter = adapter

                } else {
                    h.audios.visibility = View.GONE
                }


                h.likeLay.setOnClickListener {
                    if (feeds.posts.get(i).like == "0") {

                        feeds.posts.get(i).like = "1"
                        feeds.posts.get(i).likes = (feeds.posts.get(i).likes.toInt() + 1).toString()
                        h.likeIcon.setImageDrawable(VectorDrawableCompat.create(Base.get.resources, like, h.likeIcon.context.theme));
                    } else {
                        feeds.posts.get(i).likes = (feeds.posts.get(i).likes.toInt() - 1).toString()

                        feeds.posts.get(i).like = "0"
                        h.likeIcon.setImageDrawable(VectorDrawableCompat.create(Base.get.resources, unLike, h.likeIcon.context.theme));

                    }


                    disableAnimation = true
//                    notifyDataSetChanged()



                        holder.updateLikesCounter(true)




                    val reqObj = JSONObject()

                    reqObj.put("user_id", profile.userId)
                    reqObj.put("session", profile.session)
                    reqObj.put("post_id", post.id)

                    log.d("request data $reqObj")

                    model.responseCall(Http.getRequestData(reqObj, Http.CMDS.LIKE_BOSISH))
                            .enqueue(object : retrofit2.Callback<ResponseData> {
                                override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                                    log.d("follow on fail $t")
                                }

                                override fun onResponse(call: Call<ResponseData>?, response: Response<ResponseData>?) {
//                                    if (response!!.isSuccessful) {
//                                        log.d("like on response $response")
//                                        log.d("like on response ${response.body()!!.res}")
//                                        log.d("like on response ${Http.getResponseData(response.body()!!.prms)}")
//
//
//                                        try {
//
//                                            val req = JSONObject(Http.getResponseData(response.body()!!.prms))
//                                            if (req.has("likes")) {
//                                                feeds.posts.get(i).likes = req.optString("likes")
//                                                if (feeds.posts.get(i).like == "0") {
//
//                                                    feeds.posts.get(i).like = "1"
//
//                                                } else {
//
//                                                    feeds.posts.get(i).like = "0"
//
//                                                }
//                                                log.d("on refresh ${h.quote.tag == null} ${h.quote.tag != post.id} post ${post.id}")
//                                                disableAnimation = true
//                                                notifyDataSetChanged()
//                                            }
//                                        } catch (e: Exception) {
//
//                                        }
//                                    } else {
//                                        Toast.makeText(Base.get, Base.get.resources.getString(R.string.internet_conn_error), Toast.LENGTH_SHORT).show()
//                                    }

                                }

                            })
                }

                h.commentLay.setOnClickListener {
                    val goCommentActivity = Intent(ctx, CommentActivity::class.java)
                    goCommentActivity.putExtra("postId", post.id.toInt())
                    val startingLocation = IntArray(2)
                    h.commentLay.getLocationOnScreen(startingLocation)
                    goCommentActivity.putExtra(CommentActivity.LOCATION, startingLocation[1])
                    if (activity != null){
                        MainActivity.COMMENT_POST_UPDATE = i
                        activity!!.startActivityForResult(goCommentActivity,Const.GO_COMMENT_ACTIVITY)
                        activity!!.overridePendingTransition(0, 0)
                    }else{
                        ctx.startActivity(goCommentActivity)
                    }
                }
                h.avatar.setOnClickListener{
                    if (!pOrF) clicker.click(i)

                }
                h.topContainer.setOnClickListener {

                    if (!pOrF) clicker.click(i)

                }


                h.popup.setOnClickListener {

                    val popup = PopupMenu(ctx,h.popup)
                   if (pOrF == true && profile.userId == postUser!!.userId){
                       popup.inflate(R.menu.menu_own_feed)
                   }else{
                       popup.inflate(R.menu.menu_feed)
                   }
                    popup.setOnMenuItemClickListener{
                        item ->
                        when(item.itemId){
                            R.id.delete ->{

                                val reqObj = JSONObject()

                                reqObj.put("user_id", profile.userId)
                                reqObj.put("session", profile.session)
                                reqObj.put("post_id", post.id)

                                log.d("request data for delete post $reqObj")

                                model.responseCall(Http.getRequestData(reqObj, Http.CMDS.DELETE_POST)).enqueue(object : Callback<ResponseData>{
                                    override fun onResponse(p0: Call<ResponseData>?, p1: Response<ResponseData>?) {
                                        try{

                                            feeds.postlarSoni = "${feeds.postlarSoni.toInt()-1}"
                                            feeds.posts.removeAt(i)
                                            notifyItemRemoved(i)
                                            notifyItemRangeChanged(i, feeds.posts.size)
                                            notifyItemChanged(0)

                                            log.d("onresponse from delete post $p1")
                                        }catch (e:Exception){

                                        }
                                    }

                                    override fun onFailure(p0: Call<ResponseData>?, p1: Throwable?) {
                                        log.d("onfail from delete post $p1")
                                    }

                                })
                            }

                            R.id.change -> {

                                if (changeId == -1){
                                    changeId = i
                                    notifyItemChanged(i)
                                }
                            }
                        }
                        false
                    }

                    popup.show()
                }

            }

        }else if (type == HEADER){
            log.d("HEADER")
            val h = holder as ProfileHeaderHolder

            h.follow.tag  = FOLLOW_TYPE
            h.follow.text = FOLLOW_TYPE

            Picasso.with(ctx)
                    .load(postUser!!.photo)
                    .error(VectorDrawableCompat.create(Base.get.resources, R.drawable.account,null))

                    .into(h.avatar)
//            Glide.with(ctx)
//                    .load(postUser!!.photo)
//                    .asBitmap()
////                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .error(VectorDrawableCompat.create(Base.get.resources, R.drawable.account,null))
//                    .into(h.avatar)
////                    .into(object : SimpleTarget<Bitmap>(100,100){
////                        override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap>?) {
////                            h.avatar.setImageBitmap(resource)
////                        }
////
////                    })

            h.username.text  = postUser.username
            h.posts.text  =    feeds.postlarSoni
//            h.firstName.text =

            h.followers.text = feeds.followers
            h.following.text = feeds.following
            h.followersLay.setOnClickListener{   clicker.click(Const.TO_FOLLOWERS)}
            h.followingLay.setOnClickListener{clicker.click(Const.TO_FOLLOWING)}

            h.avatar.setOnClickListener{
                clicker.click(Const.CHANGE_AVATAR)

            }

            h.follow.setOnClickListener {


                if(h.follow.tag != ProfileFragment.SETTINGS){
                    log.d("follow button type ${h.follow.tag}")



                    val reqObj = JSONObject()

                    reqObj.put("user_id",user.userId)
                    reqObj.put("session",user.session)
                    reqObj.put("user",   postUser.userId)

                    log.d("request data $reqObj")

                    model.responseCall(Http.getRequestData(reqObj,Http.CMDS.FOLLOW))
                            .enqueue(object : retrofit2.Callback<ResponseData> {
                                override fun onFailure(call: Call<ResponseData>?, t: Throwable?) {
                                    log.d("follow on fail $t")
                                }

                                override fun onResponse(call: Call<ResponseData>?, response: Response<ResponseData>?) {
                                    if (response!!.isSuccessful){
                                        log.d("follow on response $response")
                                        log.d("follow on response ${response.body()!!.res}")
                                        log.d("follow on response ${Http.getResponseData(response.body()!!.prms)}")
                                        FFFFragment.OZGARGAN_USERNI_IDSI = postUser.userId.toInt()
                                        if ((h.follow.tag == ProfileFragment.UN_FOLLOW || h.follow.tag == ProfileFragment.REQUEST) && response.body()!!.res == "0"){
                                            h.follow.tag     = ProfileFragment.FOLLOW
                                            h.follow.text    = ProfileFragment.FOLLOW
                                            FFFFragment.QAYSI_HOLATGA_OZGARDI = ProfileFragment.FOLLOW
                                            ProfileFragment.FOLLOW_TYPE       = ProfileFragment.FOLLOW

                                            h.followers.text = "${h.followers.text.toString().toInt() -  1}"
                                            MainActivity.MY_POSTS_STATUS = MainActivity.FIRST_TIME
                                        }else if (h.follow.tag == ProfileFragment.FOLLOW && response.body()!!.res == "0"){

                                            try{

                                                val req = JSONObject(Http.getResponseData(response.body()!!.prms))
                                                if (req.optString("request") == "1"){


                                                    h.follow.tag = ProfileFragment.REQUEST
                                                    h.follow.text = ProfileFragment.REQUEST
                                                    FFFFragment.QAYSI_HOLATGA_OZGARDI = ProfileFragment.REQUEST
                                                    ProfileFragment.FOLLOW_TYPE       = ProfileFragment.REQUEST


                                                }else if (req.optString("request") == "0"){

                                                    h.follow.tag  = ProfileFragment.UN_FOLLOW
                                                    h.follow.text = ProfileFragment.UN_FOLLOW
                                                    h.followers.text = "${h.followers.text.toString().toInt() +  1}"
                                                    FFFFragment.QAYSI_HOLATGA_OZGARDI = ProfileFragment.UN_FOLLOW
                                                    ProfileFragment.FOLLOW_TYPE       = ProfileFragment.UN_FOLLOW



                                                }
                                                MainActivity.MY_POSTS_STATUS = MainActivity.FIRST_TIME

                                            }catch (e : Exception){

                                            }


                                        }

                                        MainActivity.FEED_STATUS = MainActivity.NEED_UPDATE
                                    }else{

                                        Toast.makeText(Base.get, Base.get.resources.getString(R.string.internet_conn_error), Toast.LENGTH_SHORT).show()

                                    }


                                }

                            })
                }else{

                    val goSettingActivity = Intent(ctx,SettingsActivity::class.java)

                    ctx.startActivity(goSettingActivity)
                }
            }
        }
    }


    fun swapFirstItem(postList: PostList){
        disableAnimation = false
        feeds.posts.add(0,postList.posts.get(0))
        notifyDataSetChanged()
    }
    fun swapLast20Item(postList: PostList){
        disableAnimation = false

        val lastItemPostition = (feeds.posts.size + 1)
        feeds.posts.addAll(postList.posts)
        notifyItemRangeInserted(lastItemPostition,postList.posts.size)
    }
    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var images        by Delegates.notNull<RecyclerView>()
        var audios        by Delegates.notNull<RecyclerView>()
        var avatar        by Delegates.notNull<CircleImageView>()
        var name          by Delegates.notNull<TextView>()
        var quote         by Delegates.notNull<TextView>()
        var quoteEdit     by Delegates.notNull<EditText>()
        var likeCount     by Delegates.notNull<TextSwitcher>()
        var commentCount  by Delegates.notNull<TextView>()
        var time          by Delegates.notNull<TextView>()
        var username      by Delegates.notNull<TextView>()
        var likeIcon      by Delegates.notNull<AppCompatImageView>()
        var popup         by Delegates.notNull<AppCompatImageView>()
        var likeLay       by Delegates.notNull<LinearLayout>()
        var commentLay    by Delegates.notNull<LinearLayout>()
        var topContainer  by Delegates.notNull<ViewGroup>()
        var sendChange    by Delegates.notNull<AppCompatImageButton>()
        init {
            images       = itemView.findViewById(R.id.images)       as RecyclerView
            audios       = itemView.findViewById(R.id.audios)       as RecyclerView
            avatar       = itemView.findViewById(R.id.avatar)       as CircleImageView
            name         = itemView.findViewById(R.id.name)         as TextView
            quote        = itemView.findViewById(R.id.commentText)  as TextView
            quoteEdit    = itemView.findViewById(R.id.commentEditText)  as EditText
            likeCount    = itemView.findViewById(R.id.likeCount)    as TextSwitcher
            commentCount = itemView.findViewById(R.id.commentCount) as TextView
            time         = itemView.findViewById(R.id.time)         as TextView
            username     = itemView.findViewById(R.id.username)     as TextView
            likeIcon     = itemView.findViewById(R.id.likeIcon)     as AppCompatImageView
            popup        = itemView.findViewById(R.id.popup)        as AppCompatImageView
            likeLay      = itemView.findViewById(R.id.likeLay)      as LinearLayout
            commentLay   = itemView.findViewById(R.id.commentLay)   as LinearLayout
            topContainer = itemView.findViewById(R.id.topContainer) as ViewGroup
            sendChange   = itemView.findViewById(R.id.sendChangedQuote) as AppCompatImageButton
        }
    }


    class ProfileHeaderHolder(rootView:View) : RecyclerView.ViewHolder(rootView){


        var followersLay    by Delegates.notNull<LinearLayout>()
        var followingLay    by Delegates.notNull<LinearLayout>()
        var avatar          by Delegates.notNull<AppCompatImageView>()
        var followers       by Delegates.notNull<TextView>()
        var following       by Delegates.notNull<TextView>()
        var username        by Delegates.notNull<TextView>()
        var firstName       by Delegates.notNull<TextView>()
        var posts           by Delegates.notNull<TextView>()
        var follow          by Delegates.notNull<Button>()


        init {
            followersLay = rootView.findViewById(R.id.followersLay) as LinearLayout
            followingLay = rootView.findViewById(R.id.followingLay) as LinearLayout
            avatar       = rootView.findViewById(R.id.avatar)       as AppCompatImageView
            followers    = rootView.findViewById(R.id.followers)    as TextView
            following    = rootView.findViewById(R.id.following)    as TextView
            username     = rootView.findViewById(R.id.username)     as TextView
            firstName    = rootView.findViewById(R.id.firstName)    as TextView
            posts        = rootView.findViewById(R.id.posts)        as TextView
            follow       = rootView.findViewById(R.id.follow)       as Button

        }
    }

    fun updateProfilPhoto(path: String) {
        postUser!!.photo = Http.BASE_URL + path
        feeds.posts.forEach { post ->

            post.user = postUser


        }
        notifyDataSetChanged()


    }

    fun Holder.updateLikesCounter(animated:Boolean){
        val currentLikesCount  = feeds.posts.get(this.adapterPosition).likes.toInt()
        if (animated){
            this.likeCount.setText(currentLikesCount.toString())
        }else{
            this.likeCount.setCurrentText(currentLikesCount.toString())
        }


    }

    fun updateFollowersCount() {
        feeds.following = MyProfileFragment.FOLLOWING
        notifyItemChanged(0)
    }


//    fun updateHeaderButton(holder:Holder,animated:Boolean){
//
//
//        val liked = VectorDrawableCompat.create(Base.get.resources, like, holder.likeIcon.context.theme)
//        val unliked = VectorDrawableCompat.create(Base.get.resources,unLike, holder.likeIcon.context.theme)
//
//
//        if (animated){
//
//
//            if (!likeAnimations.containsKey(holder)){
//                val animatorSet = AnimatorSet()
//                likeAnimations.put(holder,animatorSet)
//
//
//                val rotationAnim = ObjectAnimator.ofFloat(like, "rotation", 0f, 360f)
//                rotationAnim.setDuration(300)
//                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR)
//
//
//                val bounceAnimX = ObjectAnimator.ofFloat(like, "scaleX", 0.2f, 1f)
//                bounceAnimX.setDuration(300)
//                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR)
//
//
//                val bounceAnimY = ObjectAnimator.ofFloat(like, "scaleY", 0.2f, 1f)
//                bounceAnimY.setDuration(300)
//                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR)
//                bounceAnimY.addListener(object : Animator.AnimatorListener{
//                    override fun onAnimationRepeat(p0: Animator?) {
//
//                    }
//
//                    override fun onAnimationEnd(p0: Animator?) {
//
//                        likeAnimations.remove(holder);
//
//                    }
//
//                    override fun onAnimationCancel(p0: Animator?) {
//                    }
//
//                    override fun onAnimationStart(p0: Animator?) {
//
//                            holder.likeIcon.setImageDrawable(liked);
//
//                    }
//
//
//
//                })
//
//                animatorSet.play(rotationAnim)
//                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim)
//                animatorSet.start()
//                log.d("animate")
//            }else{
//                log.d("animate 2")
//
//                if (likedPositions.contains(holder.getPosition())) {
//                    holder.likeIcon.setImageDrawable(liked);
//                    } else {
//                    holder.likeIcon.setImageDrawable(unliked);
//                    }
//            }
//        }
//    }
}