package org.main.socforfemale.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import io.reactivex.Observable
import org.main.socforfemale.R
import org.main.socforfemale.adapter.FollowAdapter
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.model.User
import org.main.socforfemale.model.Users
import org.main.socforfemale.pattern.builder.EmptyContainer
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Prefs
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.FollowActivity
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/19/2017.
 */
class SearchFragment : BaseFragment(), AdapterClicker{


    /*
    *
    * Properties
    *
    * */
    var search:EditText       by Delegates.notNull<EditText>()
//    var searchResult:TextView by Delegates.notNull<TextView>()
    var list:RecyclerView     by Delegates.notNull<RecyclerView>()

    var progressLay           by Delegates.notNull<ViewGroup>()
    val pattern = "^[\\p{L}0-9]*$"

    var usersList:ArrayList<Users>? = null
    var adapter:FollowAdapter?      = null
    val user:User                   = Base.get.prefs.getUser()

    lateinit var emptyContainer: EmptyContainer

    var changePosition              = -1


    /*STATIC PROPERTIES*/
    companion object {
        var TAG:String  = "SearchFragment"

        fun newInstance(): SearchFragment {


            val newsFragment = SearchFragment()
            val args = Bundle()

            newsFragment.arguments = args
            return newsFragment

        }

        var choosedUserId = ""
        var chooseUserFstatus = ""
    }

    var connectActivity:GoNext?     = null
    fun connect(connActivity: GoNext){
        connectActivity = connActivity

    }


    override fun getFragmentView(): Int {
     return R.layout.fragment_search
    }

    override fun init() {
        Const.TAG = "SearchFragment"


        list           = rootView.findViewById(R.id.list)           as RecyclerView
        search         = rootView.findViewById(R.id.search)         as EditText

        emptyContainer = EmptyContainer.Builder()
                .setIcon(R.drawable.search_light)
                .setText(R.string.error_empty_search_result)
                .initLayoutForFragment(rootView)
                .build()

        list.layoutManager = LinearLayoutManager(activity)
        list.setHasFixedSize(true)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        val drawableCompat = VectorDrawableCompat.create(activity.resources,R.drawable.search,search.context.theme)
        search.setCompoundDrawablesWithIntrinsicBounds(drawableCompat,null,null,null)
//        }else{

//        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.toString().length >= 3){
                    val res  =s.toString().isOkString()
                    if (res == ""){
                        val letter = s.toString().replace(pattern,"")
                        try{
                            connectActivity!!.goNext(Const.SEARCH_USER,letter)
                        }catch (e:Exception){}
                    }else{
                        connectActivity!!.donGo(res)
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    fun String.isOkString():String{

        var result = ""


            for (i in 0..this.length - 1) {
                if (Character.UnicodeBlock.of(this.get(i)) == Character.UnicodeBlock.CYRILLIC) {
                    result = Base.get.resources.getString(R.string.error_cyrillic_letters)
                }
            }



        return result
    }


    fun swapList(users:ArrayList<Users>){

       if (users.size > 0){
           emptyContainer.hide()

           usersList                 = users
           adapter                   = FollowAdapter(Base.get,users,this,1)
           list.visibility           = View.VISIBLE
           list.adapter              = adapter


       }else{
           emptyContainer.show()



       }

    }


    fun failedGetList(error:String = ""){

//        progressLay.visibility = View.GONE
        log.e("FeedFragment => method => failedGetList errorCode => $error")
        if (adapter != null && adapter!!.users.size != 0){
            log.e("list bor lekin xatolik shundo ozini qoldiramiz")


            emptyContainer.hide()

            list.visibility = View.VISIBLE


        }else{
            log.e("list null yoki list bom bosh")

            emptyContainer.show()

            list.visibility = View.GONE
        }

    }

    override fun click(position: Int) {

       val user = adapter!!.users.get(position)
        log.d("result from search user -> ${user}")

        if (user.userId != this.user.userId){
           val type = user.setStatusUserFactory()


           choosedUserId = user.userId
           chooseUserFstatus = type

           val go = Intent(activity,FollowActivity::class.java)
           val bundle = Bundle()
           bundle.putString("username",user.username)
           bundle.putString("photo",   user.photo150)
           bundle.putString("userId",  user.userId)
           bundle.putString(ProfileFragment.F_TYPE,type)
            log.d("result from search user -> ${bundle}")

            go.putExtra(FollowActivity.TYPE,FollowActivity.PROFIL_T)
           go.putExtras(bundle)
           startActivityForResult(go,Const.FROM_SEARCH_TO_PROFIL)
       }else{
           connectActivity!!.goNext(Const.PROFIL_PAGE,"")
       }

    }

    override fun data(data: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log.d("${requestCode} $resultCode")
        if (requestCode == Const.FROM_SEARCH_TO_PROFIL){


            adapter!!.users.forEach { user ->

                if (user.userId == choosedUserId) run {

                        user.setStatusFactory(chooseUserFstatus)

                }


            }

            adapter!!.notifyDataSetChanged()
            chooseUserFstatus = ""
            choosedUserId = ""
        }
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.toString().length >= 3){
                    val res  =s.toString().isOkString()
                    if (res == ""){
                        val letter = s.toString().replace(pattern,"")
                        try{
                            connectActivity!!.goNext(Const.SEARCH_USER,letter)
                        }catch (e:Exception){}
                    }else{
                        connectActivity!!.donGo(res)
                    }
                }else{
                    adapter!!.users.clear()
                    adapter!!.notifyDataSetChanged()
                    list.adapter = adapter
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }


    override fun onResume() {
        super.onResume()
        log.d("onresume")
        search.showKeyboard()

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        log.d("onHiddenChanged ")
        if(hidden){
            search.hideKeyboard()

        }else{
            search.showKeyboard()

        }
    }

    override fun onStop() {
        super.onStop()
    }
    override fun onStart() {
        super.onStart()
        log.d("onresume")

    }
    fun View.showKeyboard() {
        this.requestFocus()
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }


    fun Users.setStatusFactory(type:String):Users{

        when(type){
            ProfileFragment.FOLLOW ->{
                this.follow = 0
                this.request = 0
            }

            ProfileFragment.REQUEST ->{
                this.follow = 0
                this.request = 1

            }

            ProfileFragment.UN_FOLLOW ->{
                this.follow = 1
                this.request = 0

            }
        }

        return this
    }


    fun Users.setStatusUserFactory():String{

        val user = this
        var type = ""
        if (user.follow == 0 && user.request == 0){

            log.d("${user.userId} -> ${user.username}ga follow qilinmagan")
            type =  ProfileFragment.FOLLOW

        }else if (user.follow == 1 && user.request == 0){

            log.d("${user.userId} -> ${user.username}ga follow qilingan")
            type =  ProfileFragment.UN_FOLLOW
        }else if (user.follow == 0 && user.request == 1){

            log.d("${user.userId} -> ${user.username}ga zapros tashalgan")
            type =  ProfileFragment.REQUEST

        }else {
            log.d("${user.userId} -> ${user.username}da xato holat ")
            type =  ProfileFragment.FOLLOW


        }
        return type
    }
}