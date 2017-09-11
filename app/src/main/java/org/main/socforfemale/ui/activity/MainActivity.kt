package org.main.socforfemale.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import me.iwf.photopicker.PhotoPicker
import okhttp3.MultipartBody
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.adapter.FeedAdapter
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.rest.Http
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.ErrorConnModule
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.*
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.ErrorConnection
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.publish.PublishImageActivity
import org.main.socforfemale.ui.activity.publish.PublishQuoteActivity
import org.main.socforfemale.ui.activity.publish.PublishSongActivity
import org.main.socforfemale.ui.activity.publish.PublishUniversalActivity
import org.main.socforfemale.ui.fragment.*
import java.io.File
import javax.inject.Inject

class MainActivity : BaseActivity(), GoNext, Viewer {


    var profilFragment:       MyProfileFragment?    = null
    var searchFragment:       SearchFragment?       = null
    var notificationFragment: NotificationFragment? = null
    var feedFragment:         FeedFragment?         = null
    var manager:              FragmentManager?      = null
    var transaction:          FragmentTransaction?  = null
    var lastFragment:         Int                   = 0
    @Inject
    lateinit var presenter:Presenter


    @Inject
    lateinit var errorConn:ErrorConnection

    var user = Base.get.prefs.getUser()

    /*
    *
    *
    * */





    companion object MyPostOffset {

        var start          = 0
        var end            = 20
        var startFeed      = 0
        var endFeed        = 20
        var getFirst       = 1
        var startFollowers = 0
        var endFollowers   = 20
        var startFollowing = 0
        var endFollowing   = 20
        var startSearch    = 0
        var endSearch      = 20

        var MY_POSTS_STATUS = "-1"

        val FIRST_TIME   = "0"
        val NEED_UPDATE  = "1"
        val AFTER_UPDATE = "2"

        var FEED_STATUS = NEED_UPDATE

        var COMMENT_POST_UPDATE = 0
        var COMMENT_COUNT       = 0
    }

    override fun getLayout(): Int {

        return R.layout.activity_main
    }

    override fun initView() {
        Const.TAG = "MainActivity"
        startIntroAnimation()

        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model(),this))
                .presenterModule(PresenterModule())
                .errorConnModule(ErrorConnModule(this,true))
                .build()
                .inject(this)
        setPager()

    }

    fun setPager(): Unit {
        initFragments()
        setFragment(Const.FEED_FR)


//        if (feedFragment != null && feedFragment!!.feedAdapter == null){
//            feedFragment!!.showProgress()
//        }

        errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
            override fun connected() {
                log.d("connected")
                val reqObj = JSONObject()
                reqObj.put("user_id" , user.userId)
                reqObj.put("session", user.session)
                reqObj.put("start",   startFeed)
                reqObj.put("end",     endFeed)

                presenter.requestAndResponse(reqObj, Http.CMDS.FEED)

            }

            override fun disconnected() {
                log.d("disconnected")


            }

        })


        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.feed_select))
        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.search))
        val view: View = layoutInflater.inflate(R.layout.res_upload_view, null)

        tablayout.addTab(tablayout.newTab().setCustomView(view))
        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.notification))
        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.account))



        tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
                log.d("Unselected -> ${p0!!.position}")

                if (p0.position != Const.UPLOAD_FR) p0.setIcon(Const.unselectedTabs.get(p0.position)!!)
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {

                hideSoftKeyboard()

                when (p0!!.position) {

                    Const.PROFIL_FR -> {
                        log.i("profil page select $MY_POSTS_STATUS")
                        if (MY_POSTS_STATUS != AFTER_UPDATE) {


                            errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                                override fun connected() {
                                    val reqObj = JSONObject()
                                    reqObj.put("user_id", user.userId)
                                    reqObj.put("session", user.session)
                                    reqObj.put("user",    user.userId)
                                    reqObj.put("start",   start)
                                    reqObj.put("end",     end)
                                    presenter!!.requestAndResponse(reqObj, Http.CMDS.MY_POSTS)


                                }

                                override fun disconnected() {

                                }

                            })

                        }
                        lastFragment = p0.position
                        p0.setIcon(Const.selectedTabs.get(p0.position)!!)
                        setFragment(p0.position)



                    }
                    Const.FEED_FR -> {
                        log.i("feed page select")

                        if (FEED_STATUS != AFTER_UPDATE){



                            errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                                override fun connected() {
                                    val reqObj = JSONObject()
                                    reqObj.put("user_id", user.userId)
                                    reqObj.put("session", user.session)
                                    reqObj.put("start",   startFeed)
                                    reqObj.put("end",     endFeed)

                                    log.d("feed page select $reqObj")
                                    presenter!!.requestAndResponse(reqObj, Http.CMDS.FEED)



                                }

                                override fun disconnected() {

                                }

                            })

                        }
                        lastFragment = p0.position

                        p0.setIcon(Const.selectedTabs.get(p0.position)!!)
                        setFragment(p0.position)
                    }
                    Const.UPLOAD_FR -> {
                        goNext(Const.PICK_UNIVERSAL, "")
                    }
                    else -> {
                        lastFragment = p0.position
                        p0.setIcon(Const.selectedTabs.get(p0.position)!!)
                        setFragment(p0.position)
                    }
                }

            }

        })

    }


    override fun goNext(to: Int, data: String) {

        var intent: Intent? = null
        when (to) {
            Const.TO_POSTS -> {

            }
            Const.TO_FOLLOWERS -> {


                intent = Intent(this, FollowActivity().javaClass)
                intent.putExtra(FollowActivity.TYPE, FollowActivity.FOLLOWERS)
                intent.putExtra("userId",user.userId)

            }
            Const.TO_FOLLOWING -> {

                intent = Intent(this, FollowActivity().javaClass)
                intent.putExtra(FollowActivity.TYPE, FollowActivity.FOLLOWING)
                intent.putExtra("userId",user.userId)
            }


            Const.PICK_IMAGE -> {
                log.d("Pick Image ga o'tish")

                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .start(this, Const.PICK_IMAGE)
            }
            Const.CHANGE_AVATAR -> {
                log.i("Pick Image ga o'tish")

                PhotoPicker.builder()
                        .setPhotoCount(1)

                        .start(this, Const.CHANGE_AVATAR)
            }

            Const.PICK_QUOTE -> {
                intent = Intent(this, PublishQuoteActivity().javaClass)

            }

            Const.PICK_AUDIO -> {
                intent = Intent(this, PublishSongActivity().javaClass)

            }

            Const.PICK_UNIVERSAL -> {

                intent = Intent(this, PublishUniversalActivity().javaClass)
            }


            Const.SEARCH_USER -> {



                errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                    override fun connected() {
                        val reqObj = JSONObject()
                        reqObj.put("user_id", user.userId)
                        reqObj.put("session", user.session)
                        reqObj.put("start",   startSearch)
                        reqObj.put("end",     endSearch)
                        reqObj.put("user",    data)
                        presenter!!.requestAndResponse(reqObj, Http.CMDS.SEARCH_USER)




                    }

                    override fun disconnected() {
                        hideSoftKeyboard()
                    }

                })
            }

            Const.FOLLOW -> {
                errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                    override fun connected() {
                        presenter!!.requestAndResponse(JSONObject(data), Http.CMDS.FOLLOW)





                    }

                    override fun disconnected() {

                    }

                })

            }

            Const.PROFIL_PAGE -> {
                tablayout.getTabAt(Const.PROFIL_FR)!!.select()

            }
            Const.REFRESH_FEED -> {

                errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                    override fun connected() {


                        val reqObj = JSONObject()
                        reqObj.put("user_id", user.userId)
                        reqObj.put("session", user.session)
                        reqObj.put("start",   startFeed)
                        reqObj.put("end",     endFeed)


                        presenter.requestAndResponse(reqObj, Http.CMDS.FEED)


                    }

                    override fun disconnected() {

                    }

                })
            }

            Const.REFRESH_PROFILE_FEED ->{


                errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
                    override fun connected() {
                        val reqObj = JSONObject()
                        reqObj.put("user_id", user.userId)
                        reqObj.put("session", user.session)
                        reqObj.put("user",    user.userId)
                        reqObj.put("start",   start)
                        reqObj.put("end",     end)

                        MY_POSTS_STATUS = FIRST_TIME
                        presenter.requestAndResponse(reqObj, Http.CMDS.MY_POSTS)





                    }

                    override fun disconnected() {

                    }

                })
            }
            else -> {

            }
        }

        if (intent != null) startActivityForResult(intent, Const.FROM_MAIN_ACTIVITY)
    }

    override fun donGo(why: String) {
       // Toast.makeText(Base.get.applicationContext, why, Toast.LENGTH_SHORT).show()
    }


    fun initFragments() {

        var bundle = Bundle()
        bundle.putString("photo",     user.profilPhoto)
        bundle.putString("username",  user.userName)
        bundle.putString("firstName", user.first_name)
        bundle.putString("userId",    user.userId)
        bundle.putString(ProfileFragment.F_TYPE, ProfileFragment.SETTINGS)
        profilFragment = MyProfileFragment.newInstance(bundle)
        profilFragment!!.connect(this)
        searchFragment = SearchFragment.newInstance()
        searchFragment!!.connect(this)

        notificationFragment = NotificationFragment.newInstance()
        notificationFragment!!.connect(this)

        feedFragment = FeedFragment.newInstance()
        feedFragment!!.connect(this)


    }

    @SuppressLint("CommitTransaction")
    fun setFragment(id: Int = Const.FEED_FR) {

        if (manager == null) manager = supportFragmentManager

        transaction = manager!!.beginTransaction()
//        val fr1 = manager!!.findFragmentByTag(FeedFragment.TAG)
//        val fr2 = manager!!.findFragmentByTag(SearchFragment.TAG)
//        val fr3 = manager!!.findFragmentByTag(NotificationFragment.TAG)
//        val fr4 = manager!!.findFragmentByTag(ProfileFragment.TAG)
        when (id) {
            Const.FEED_FR -> {

                if (searchFragment!!.isAdded && !searchFragment!!.isHidden) transaction!!.hide(searchFragment)
                if (notificationFragment!!.isAdded && !notificationFragment!!.isHidden) transaction!!.hide(notificationFragment)
                if (profilFragment!!.isAdded && !profilFragment!!.isHidden) transaction!!.hide(profilFragment)

                if (feedFragment!!.isAdded) {
                    if (feedFragment!!.isHidden)
                        transaction!!.show(feedFragment)
                } else
                    transaction!!.add(R.id.pager, feedFragment, FeedFragment.TAG)



            }

            Const.SEARCH_FR -> {

                if (feedFragment!!.isAdded && !feedFragment!!.isHidden) transaction!!.hide(feedFragment)
                if (notificationFragment!!.isAdded && !notificationFragment!!.isHidden) transaction!!.hide(notificationFragment)
                if (profilFragment!!.isAdded && !profilFragment!!.isHidden) transaction!!.hide(profilFragment)

                if (searchFragment!!.isAdded) {
                    if (searchFragment!!.isHidden)
                        transaction!!.show(searchFragment)
                } else
                    transaction!!.add(R.id.pager, searchFragment, SearchFragment.TAG)
            }

            Const.NOTIF_FR -> {

                if (feedFragment!!.isAdded && !feedFragment!!.isHidden) transaction!!.hide(feedFragment)
                if (searchFragment!!.isAdded && !searchFragment!!.isHidden) transaction!!.hide(searchFragment)
                if (profilFragment!!.isAdded && !profilFragment!!.isHidden) transaction!!.hide(profilFragment)

                if (notificationFragment!!.isAdded) {
                    if (notificationFragment!!.isHidden)
                        transaction!!.show(notificationFragment)
                } else
                    transaction!!.add(R.id.pager, notificationFragment, NotificationFragment.TAG)


            }
            Const.PROFIL_FR -> {

                if (feedFragment!!.isAdded && !feedFragment!!.isHidden) transaction!!.hide(feedFragment)
                if (searchFragment!!.isAdded && !searchFragment!!.isHidden) transaction!!.hide(searchFragment)
                if (notificationFragment!!.isAdded && !notificationFragment!!.isHidden) transaction!!.hide(notificationFragment)


                log.d("${profilFragment!!.isAdded}")
                if (profilFragment!!.isAdded) {
                    if (profilFragment!!.isHidden)
                        transaction!!.show(profilFragment)
                } else
                    transaction!!.add(R.id.pager, profilFragment, ProfileFragment.TAG)


            }


        }

        transaction!!.commit()

    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log.d("MainActivity -> OnactivityResult: req:${requestCode} res: ${resultCode} intent: ${if (data != null) true else false}")

        when (resultCode) {
            Activity.RESULT_OK -> {

                var photos: List<String>? = null

                when (requestCode) {
                    Const.PICK_IMAGE -> {
                        if (data != null) {
                            photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)

                        }

                        if (photos != null) {

                            val intent = Intent(this, PublishImageActivity().javaClass);
                            intent.putExtra(Const.PUBLISH_IMAGE, photos.get(0))
                            startActivityForResult(intent, Const.FROM_MAIN_ACTIVITY)
                        }
                    }
                    Const.CHANGE_AVATAR -> {
                        if (data != null) {
                            photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)

                        }
                        if (photos != null) {
                            log.d("picked photo ${photos.get(0)}")

                            photos.get(0).uploadAvatar()
                        }
                    }
                    Const.GO_COMMENT_ACTIVITY ->{
                        if (feedFragment != null && !feedFragment!!.isHidden){
                            try {

                                //TODO COMMENTLANI SONINI OLIB KELISH
//                               feedFragment!!.feedAdapter!!.feeds.posts.get(COMMENT_POST_UPDATE).comments = COMMENT_COUNT.toString()
//                               feedFragment!!.feedAdapter!!.notifyItemChanged(COMMENT_POST_UPDATE)
                            }catch (e :Exception){}

                        }
                    }
                }
            }

            Const.PICK_UNIVERSAL -> {
                log.d("after posting go feed $MY_POSTS_STATUS")
                FEED_STATUS  = NEED_UPDATE
                if (feedFragment!!.feedAdapter != null){
                    MyPostOffset.startFeed = 0
                    MyPostOffset.endFeed   = 1
                }

                MY_POSTS_STATUS = NEED_UPDATE

                val tab = tablayout.getTabAt(0)
                tab!!.setIcon(Const.selectedTabs.get(0)!!)
                tab.select()

            }

            else -> {

                log.d("lastfragment -> ${lastFragment}")
                log.d("profil followers count -> ${FFFFragment.followersCount}")
                if (requestCode == Const.CHANGE_AVATAR){
                    try{
                        profilFragment!!.createProgressForAvatar(FeedAdapter.CANCEL_PROGRESS);
                    }catch (e:Exception){}
                }

                if  (lastFragment == 4 && FFFFragment.followersCount != -1 && profilFragment != null ){
                    MyProfileFragment.FOLLOWING = FFFFragment.followersCount.toString()
                    profilFragment!!.postAdapter!!.updateFollowersCount()
                }
                val tab = tablayout.getTabAt(lastFragment)
                tab!!.setIcon(Const.selectedTabs.get(lastFragment)!!)
                tab.select()

                setFragment(lastFragment)

            }
        }
    }
    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {

        log.d("onbackpressed")


        if (doubleBackToExitPressedOnce) {

            MyPostOffset.start          = 0
            MyPostOffset.end            = 20
            MyPostOffset.startFeed      = 0
            MyPostOffset.endFeed        = 20
            MyPostOffset.startFollowers = 0
            MyPostOffset.endFollowers   = 20
            MyPostOffset.startFollowing = 0
            MyPostOffset.endFollowing   = 20
            MY_POSTS_STATUS             = NEED_UPDATE
            FEED_STATUS                 = NEED_UPDATE
            COMMENT_POST_UPDATE         = 0
            COMMENT_COUNT               = 0
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, resources.getString(R.string.exit_to_press_again), Toast.LENGTH_SHORT).show();

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun initProgress() {

    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onSuccess(from: String, result: String) {

        log.d("success from: $from result: $result")
        when (from) {

            Http.CMDS.MY_POSTS -> {
                log.d("my post status $MY_POSTS_STATUS")
                try{
                    val postList: PostList = Gson().fromJson(result, PostList::class.java)

                    profilFragment!!.initFF(postList)
                    if (postList.posts.size > 0){
                        MY_POSTS_STATUS = AFTER_UPDATE
                        profilFragment!!.swapPosts(postList)
                    }else{
                        val emptyPost = ArrayList<Posts>()
                        emptyPost.add(Posts("-1", Quote("","",""),ArrayList<Audio>(),ArrayList<Image>(),"0","0","","", PostUser("","","http")))
                        val emptyPosts: PostList = PostList(emptyPost,postList.followers,postList.following,postList.postlarSoni)
                        profilFragment!!.swapPosts(emptyPosts)

                    }

                }catch (e:Exception){

                    profilFragment!!.failedGetList()

                }
            }

            Http.CMDS.CHANGE_AVATAR -> {

                user = Base.get.prefs.getUser()
                try{
                profilFragment!!.createProgressForAvatar(FeedAdapter.HIDE_PROGRESS);
                }catch (e:Exception){}
                profilFragment!!.setAvatar(user.profilPhoto)
            }

            Http.CMDS.SEARCH_USER -> {

                val follow = Gson().fromJson<Follow>(result, Follow::class.java)

                searchFragment!!.swapList(follow.users)
            }

            Http.CMDS.FEED -> {

                try{

                    val postList: PostList = Gson().fromJson(result, PostList::class.java)
                   feedFragment!!.hideProgress()
                    if (postList.posts.size > 0){
                       FEED_STATUS = AFTER_UPDATE
                       feedFragment!!.swapPosts(postList)
                   }else{
                       feedFragment!!.failedGetList()

                   }

                }catch (e:Exception){
                    feedFragment!!.failedGetList()

                }

            }

            Http.CMDS.GET_FOLLOWERS -> {

            }
        }
    }

    override fun onFailure(from: String, message: String, erroCode: String) {

        log.d("error from: $from message: $message")
        Functions.show(message)


        when(from){
            Http.CMDS.FEED        -> Handler().postDelayed({feedFragment!!.failedGetList(message)},1500)

            Http.CMDS.MY_POSTS    -> Handler().postDelayed({profilFragment!!.failedGetList(message)},1500)
            Http.CMDS.SEARCH_USER -> Handler().postDelayed({searchFragment!!.failedGetList(message)},1500)
        }
    }

    private fun String.uploadAvatar() {
        try{
            profilFragment!!.createProgressForAvatar(FeedAdapter.SHOW_PROGRESS);
        }catch (e:Exception){}
        val reqFile = ProgressRequestBody(File(this), object : ProgressRequestBody.UploadCallbacks {

            override fun onProgressUpdate(percentage: Int) {


            }

            override fun onError() {
                log.d("onerror")

            }

            override fun onFinish() {
                log.d("onfinish")
            }

        }, ProgressRequestBody.IMAGE_ALL)
        val body = MultipartBody.Part.createFormData("upload", File(this).name, reqFile)
        presenter.uploadAvatar(body)

    }


    fun startIntroAnimation(){

        val actionbarsize = Functions.DPtoPX(56f,Base.get)
        tablayout.translationY = actionbarsize.toFloat()


        tablayout.animate()
                .translationY(0f)
                .setDuration(300)
                .setStartDelay(300)
    }
    fun hideSoftKeyboard() {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (this.currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
            }
        }
    }



}




