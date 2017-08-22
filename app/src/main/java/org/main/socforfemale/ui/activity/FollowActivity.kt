package org.main.socforfemale.ui.activity

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.google.gson.Gson
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.base.Http
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.Followers
import org.main.socforfemale.model.Following
import org.main.socforfemale.model.PostList
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.fragment.FFFFragment
import org.main.socforfemale.ui.fragment.MyProfileFragment
import org.main.socforfemale.ui.fragment.ProfileFragment
import org.main.socforfemale.ui.fragment.SearchFragment
import javax.inject.Inject

import kotlinx.android.synthetic.main.activity_follow.*

class FollowActivity : BaseActivity(), GoNext,Viewer {



    companion object {
        val LIST_T    = 1
        val PROFIL_T  = 2
        val FOLLOWERS = 3
        val FOLLOWING = 4
        val TYPE      = "type"

        var start     = 0
        var end       = 40


    }

    var manager:FragmentManager?        = null
    var transaction:FragmentTransaction?= null

    @Inject
    lateinit var presenter:Presenter
    var user                            = Base.get.prefs.getUser()
    var profilFragment:ProfileFragment? = null
    var followersFragment:FFFFragment?  = null
    var userID                          = ""
    override fun getLayout(): Int {
        return R.layout.activity_follow
    }

    override fun initView() {
        Const.TAG = "FollowActivity"

        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model()))
                .presenterModule(PresenterModule())
                .build()
                .inject(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }
        showFragment(intent.getIntExtra(TYPE,-1))


    }

    fun showFragment(int:Int){
        log.d("show fragment $int")
        if (manager == null) manager = supportFragmentManager

        transaction = manager!!.beginTransaction()
        if (int == LIST_T){

            val searchFragment = SearchFragment.newInstance()
            searchFragment.connect(this)

            transaction!!.add(R.id.container,searchFragment,SearchFragment.TAG)

        }else if (int == PROFIL_T){

            if (profilFragment == null){
                profilFragment = ProfileFragment.newInstance(intent.extras)
                profilFragment!!.connect(this)
            }
            userID = intent.extras.getString("userId")

            log.d("profile type ${intent.extras.getString(ProfileFragment.F_TYPE)}")
            if (followersFragment != null && followersFragment!!.isAdded && !followersFragment!!.isHidden) transaction!!.hide(followersFragment)
            if (profilFragment!!.isAdded) {
                if (profilFragment!!.isHidden)
                    transaction!!.show(profilFragment)
            } else
                transaction!!.add(R.id.container, profilFragment, ProfileFragment.TAG)

            log.d("close profil logic: ${intent.extras.getString(ProfileFragment.F_TYPE) != ProfileFragment.REQUEST || (intent.extras.getInt("close",-1) == 0 && intent.extras.getString(ProfileFragment.F_TYPE) != ProfileFragment.REQUEST)}")
           if (intent.extras.getString(ProfileFragment.F_TYPE) != ProfileFragment.REQUEST || (intent.extras.getInt("close",-1) == 0 && intent.extras.getString(ProfileFragment.F_TYPE) != ProfileFragment.REQUEST)){
               val reqObj = JSONObject()
               reqObj.put("user_id",user.userId)
               reqObj.put("session",user.session)
               reqObj.put("user",   userID)
               reqObj.put("start",  start)
               reqObj.put("end",    end)


               presenter.requestAndResponse(reqObj, Http.CMDS.MY_POSTS)
           }
        }else if (int == FOLLOWERS ){
            userID = intent.extras.getString("userId")
            val header = Bundle()
            header.putString("header", Base.get.resources.getString(R.string.followers))
            if (followersFragment == null){
                followersFragment = FFFFragment.newInstance(header)
                followersFragment!!.connect(this)
            }
            if (profilFragment != null && profilFragment!!.isAdded && !profilFragment!!.isHidden) transaction!!.hide(profilFragment)
            if (followersFragment!!.isAdded) {
                if (followersFragment!!.isHidden)
                    transaction!!.show(followersFragment)
            } else
                transaction!!.add(R.id.container, followersFragment, FFFFragment.TAG)

//            transaction!!.add(R.id.container,followersFragment,FFFFragment.TAG)
            val obj = JSONObject()
            obj.put("user_id",user.userId)
            obj.put("session",user.session)
            obj.put("user",   userID)

            obj.put("start",  MainActivity.startFollowers)
            obj.put("end",    MainActivity.endFollowers)
            presenter.requestAndResponse(obj,Http.CMDS.GET_FOLLOWERS)

        }else if (int == FOLLOWING){
            userID = intent.extras.getString("userId")
            val header = Bundle()
            header.putString("header", Base.get.resources.getString(R.string.following))
            if (followersFragment == null){
                followersFragment = FFFFragment.newInstance(header)
                followersFragment!!.connect(this)
            }
            if (profilFragment != null && profilFragment!!.isAdded && !profilFragment!!.isHidden) transaction!!.hide(profilFragment)
            if (followersFragment!!.isAdded) {
                if (followersFragment!!.isHidden)
                    transaction!!.show(followersFragment)
            } else
                transaction!!.add(R.id.container, followersFragment, FFFFragment.TAG)

//            transaction!!.add(R.id.container,followersFragment,FFFFragment.TAG)
            val obj = JSONObject()
            obj.put("user_id",user.userId)
            obj.put("user",   userID)
            obj.put("session",user.session)
            obj.put("start",  MainActivity.startFollowing)
            obj.put("end",    MainActivity.endFollowing)
            presenter.requestAndResponse(obj,Http.CMDS.GET_FOLLOWING)

        }
            transaction!!.addToBackStack("")
            transaction!!.commit()
    }

    override fun goNext(to: Int, data: String) {
        when(to){
            Const.REFRESH_PROFILE_FEED ->{
                val reqObj = JSONObject()
                reqObj.put("user_id", user.userId)
                reqObj.put("session", user.session)
                reqObj.put("user",    userID)
                reqObj.put("start",   data)
                reqObj.put("end",     end)

                presenter.requestAndResponse(reqObj, Http.CMDS.MY_POSTS)
            }

            Const.TO_FOLLOWERS -> showFragment(FOLLOWERS)

            Const.TO_FOLLOWING -> showFragment(FOLLOWING)
        }
    }

    override fun donGo(why: String) {
    }

    override fun initProgress() {

    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun onSuccess(from: String, result: String) {

        if (from == Http.CMDS.MY_POSTS){
            try{
                val postList: PostList = Gson().fromJson(result, PostList::class.java)

                profilFragment!!.initFF(postList)
                if (postList.posts.size > 0){
                    profilFragment!!.swapPosts(postList)
                }else{
                    profilFragment!!.failedGetList()

                }

            }catch (e:Exception){

                profilFragment!!.failedGetList()

            }
        }else if (from == Http.CMDS.GET_FOLLOWERS){

            val follow = Gson().fromJson<Followers>(result, Followers::class.java)


            log.d("followersla olindi -> ${follow.users}")
            if(manager!!.findFragmentByTag(FFFFragment.TAG) != null){

                followersFragment!!.swapList(follow.users)
                FFFFragment.followersCount = MyProfileFragment.FOLLOWING.toInt()
                log.d("get followers -> ${FFFFragment.followersCount}")
            }
        }
        else if (from == Http.CMDS.GET_FOLLOWING){

            val follow = Gson().fromJson<Following>(result, Following::class.java)


            log.d("followingla olindi -> ${follow.users}")
            if(manager!!.findFragmentByTag(FFFFragment.TAG) != null){

                followersFragment!!.swapList(follow.users)
                FFFFragment.followersCount = MyProfileFragment.FOLLOWING.toInt()
                log.d("get following -> ${FFFFragment.followersCount}")

            }
        }
    }

    override fun onFailure(from: String, message: String, erroCode: String) {


        when(from ){
            Http.CMDS.MY_POSTS -> profilFragment!!.closeLoadMore()
            Http.CMDS.GET_FOLLOWING     -> Handler().postDelayed({followersFragment!!.failedGetList(message)},1500)
            Http.CMDS.GET_FOLLOWERS     -> Handler().postDelayed({followersFragment!!.failedGetList(message)},1500)
        }
    }

    override fun onBackPressed() {
        if (manager!!.backStackEntryCount == 1){


            this.finish()
        }else{

            super.onBackPressed()
        }
    }
}