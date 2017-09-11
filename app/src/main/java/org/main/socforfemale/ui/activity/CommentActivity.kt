package org.main.socforfemale.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.google.gson.Gson
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.mvp.Viewer
import kotlinx.android.synthetic.main.activity_comment.*
import org.json.JSONObject
import org.main.socforfemale.adapter.CommentAdapter
import org.main.socforfemale.rest.Http
import org.main.socforfemale.model.Comments
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.resources.utils.log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import org.main.socforfemale.base.Base
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.resources.customviews.loadmorerecyclerview.EndlessRecyclerViewScrollListener
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Functions
import javax.inject.Inject


/**
 * Created by Michaelan on 7/10/2017.
 */
class CommentActivity :BaseActivity(),Viewer,AdapterClicker{


    var postId    = -1
    @Inject
    lateinit var presenter:Presenter
    val user      = Base.get.prefs.getUser()
    var scroll: EndlessRecyclerViewScrollListener? = null

 //   var commentList:   ArrayList<Comment>? = null
    var commentAdapter:CommentAdapter?     = null
    var drawingStartLocation               = 0
    companion object {

        var start = 0
        var end   = 10

        val LOCATION = "location"
    }

    override fun getLayout(): Int {

        return R.layout.activity_comment
    }

    override fun initView() {
        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model(),this))
                .presenterModule(PresenterModule())
                .build()
                .inject(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle(resources.getString(R.string.headerComment))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }
        Const.TAG = "CommentActivity"
        drawingStartLocation = intent.getIntExtra(LOCATION,0)

        contentRoot.viewTreeObserver.addOnPreDrawListener (object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                contentRoot.viewTreeObserver.removeOnPreDrawListener(this)
                startIntroAnimation()
                return true
            }

        })

        postId = intent.getIntExtra("postId",-1)
        log.d("postId $postId")


        if (postId != -1){

            /*send data for get comment list*/
            val obj = JSONObject()
            obj.put("post_id",   postId)
            obj.put("start",   0)
            obj.put("end",    end)
            obj.put("order",  "ASC")

            obj.put("user_id",user.userId)
            obj.put("session",user.session)
            presenter.requestAndResponse(obj, Http.CMDS.GET_COMMENT_LIST)
        }else{

            list.visibility           = View.GONE
            emptyContainer.visibility = View.VISIBLE
        }

        sendComment.setOnClickListener {
            if(commentText.text.isNotEmpty()){
                send()
            }else{
                Toast.makeText(Base.get,Base.get.resources.getString(R.string.error_empty_comment),Toast.LENGTH_SHORT).show()
            }

        }

        commentText.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        commentText.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    if(commentText.text.isNotEmpty()){
                        send()
                    }else{
                        Toast.makeText(Base.get,Base.get.resources.getString(R.string.error_empty_comment),Toast.LENGTH_SHORT).show()
                    }
                    return true
                }

                return false
            }

        })
        val manager = LinearLayoutManager(this)
        scroll = object : EndlessRecyclerViewScrollListener(manager) {
            override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
                var lastVisibleItemPosition = 0
                val totalItemCount = mLayoutManager.itemCount

                lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()


                // If the total item count is zero and the previous isn't, assume the
                // list is invalidated and should be reset back to initial state
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = this.startingPageIndex
                    this.previousTotalItemCount = totalItemCount
                    if (totalItemCount == 0) {
                        this.loading = true
                    }
                }
                // If it’s still loading, we check to see if the dataset count has
                // changed, if so we conclude it has finished loading and update the current page
                // number and total item count.
                if (loading && totalItemCount > previousTotalItemCount) {
                    loading = false
                    previousTotalItemCount = totalItemCount
                }

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too
                if (!loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
                    currentPage++
                    Log.d("APPLICATION_DEMO", "currentPage" + currentPage)
                    onLoadMore(currentPage, totalItemCount, view)
                    loading = true
                }

            }

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (commentAdapter != null && commentAdapter!!.comments.size >= 10){
                    log.d("on more $page $totalItemsCount ")
                    val obj = JSONObject()
                    obj.put("post_id",postId)
                    start = commentAdapter!!.comments.size
                    obj.put("start",  start)
                    obj.put("end",    end)
                    obj.put("order",  "ASC")
                    obj.put("user_id",user.userId)
                    obj.put("session",user.session)
                    presenter.requestAndResponse(obj, Http.CMDS.GET_COMMENT_LIST)
                }
            }



        }
        list.layoutManager = manager
        list.setHasFixedSize(true)

        list.addOnScrollListener(scroll)

    }

    override fun initProgress() {
        emptyContainer.visibility = View.GONE
    }

    override fun click(position: Int) {
    }

    override fun showProgress() {
    }

    override fun data(data: String) {
    }

    override fun hideProgress() {
        emptyContainer.visibility = View.GONE

    }

    override fun onSuccess(from: String, result: String) {

        log.d("cmd: $from -> result $result")

        if (from == Http.CMDS.WRITE_COMMENT){
            commentText.setText("")

            val obj = JSONObject()
            obj.put("post_id",postId)
            start = commentAdapter!!.comments.size
            obj.put("start",  start)
            obj.put("end",    end)
            obj.put("order",  "ASC")
            obj.put("user_id",user.userId)
            obj.put("session",user.session)
            presenter.requestAndResponse(obj, Http.CMDS.GET_COMMENT_LIST)

        }else if (from == Http.CMDS.GET_COMMENT_LIST){
            val comment = Gson().fromJson<Comments>(result,Comments::class.java)
            list.visibility           = View.VISIBLE
            emptyContainer.visibility = View.GONE
//            commentList = comment.comments
//            if (commentList!!.size > 0 && commentAdapter == null){
//
//                commentAdapter = CommentAdapter(this,commentList!!,this)
//
//                list.adapter = commentAdapter
//
//            }else if(commentList!!.size > 0 && commentAdapter != null){
//                commentAdapter!!.swapList(commentList!!)
//                commentAdapter!!.animationsLocked = false
//                commentAdapter!!.delayEnterAnimation = false
//                list.smoothScrollBy(0,list.getChildAt(0).height * commentAdapter!!.comments.size)
//            }else{
//            }

            if (commentAdapter == null){
                log.d("COMMENT ADAPTER NULLGA TENG")
                commentAdapter = CommentAdapter(this,comment.comments,this)
                list.adapter = commentAdapter


            }else if(end == 10){
                log.d("COMMENT ADAPTER NULL EMAS")

                commentAdapter!!.animationsLocked = false
                commentAdapter!!.delayEnterAnimation = false
                commentAdapter!!.swapLast(comment.comments)
               try{
                   list.smoothScrollBy(0,list.getChildAt(0).height * commentAdapter!!.comments.size)
               }catch (e:Exception){}

            }
        }

    }

    override fun onFailure(from: String, message: String, erroCode: String) {

        log.d("fail from $from -> message $message")
        if(commentAdapter == null) {
            emptyContainer.visibility = View.VISIBLE
            list.visibility           = View.GONE
        }

    }

    fun send(){
        val obj = JSONObject()
        obj.put("post_id",postId)
        obj.put("comm",commentText.text.toString())

        obj.put("user_id",user.userId)
        obj.put("session",user.session)
        commentText.text.clear()
        presenter.requestAndResponse(obj, Http.CMDS.WRITE_COMMENT)
    }

    override fun onBackPressed() {
        start = 0
        end = 10
        commentText.hideKeyboard()
        Functions.hideSoftKeyboard(this)
        commentAdapter = null
        contentRoot.animate()
                .translationY(Functions.getScreenHeight(this).toFloat())
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    }})
                .start()





    }


    fun startIntroAnimation(){


        contentRoot.scaleY  = 0.1f
        contentRoot.pivotY  = drawingStartLocation.toFloat()
        commentBoxLay.translationY = 100f

        contentRoot.animate()
                .scaleY(1f)
                .setDuration(200)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator?) {
                            //TODO update comment list
                            commentBoxLay.animate().translationY(0f)
                                    .setInterpolator(DecelerateInterpolator())
                                    .setDuration(200)
                                    .start();
                    }
                })
                .start()
    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    fun View.showKeyboard() {
        this.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}

