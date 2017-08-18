package org.main.socforfemale.ui.fragment

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
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.main.socforfemale.R
import org.main.socforfemale.adapter.FollowAdapter
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.model.User
import org.main.socforfemale.model.Users
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Prefs
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.ui.activity.FollowActivity
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/19/2017.
 */
class SearchFragment : BaseFragment(), AdapterClicker{

    var search:EditText       by Delegates.notNull<EditText>()
//    var searchResult:TextView by Delegates.notNull<TextView>()
    var list:RecyclerView     by Delegates.notNull<RecyclerView>()
    var errorImg              by Delegates.notNull<AppCompatImageView>()
    var errorText             by Delegates.notNull<TextView>()
    var progressLay           by Delegates.notNull<ViewGroup>()
    val pattern = "^[\\p{L}0-9]*$"

    var usersList:ArrayList<Users>? = null
    var adapter:FollowAdapter?      = null
    val user:User                   = Prefs.Builder().getUser()
    var changePosition              = -1
    companion object {
        var TAG:String  = "SearchFragment"

        fun newInstance(): SearchFragment {


            val newsFragment = SearchFragment()
            val args = Bundle()

            newsFragment.arguments = args
            return newsFragment

        }
    }

    var connectActivity:GoNext?     = null
    fun connect(connActivity: GoNext){
        connectActivity = connActivity

    }
    var emptyContainer by Delegates.notNull<LinearLayout>()


    override fun getFragmentView(): Int {
     return R.layout.fragment_search
    }

    override fun init() {
        Const.TAG = "SearchFragment"

        emptyContainer = rootView.findViewById(R.id.emptyContainer) as LinearLayout
        errorImg       = rootView.findViewById(R.id.errorImg)       as AppCompatImageView
        errorText      = rootView.findViewById(R.id.errorText)      as TextView
//        searchResult   = rootView.findViewById(R.id.searchResult)   as TextView
        list           = rootView.findViewById(R.id.list)           as RecyclerView
        search         = rootView.findViewById(R.id.search)         as EditText

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
                    result = Base.instance.resources.getString(R.string.error_cyrillic_letters)
                }
            }



        return result
    }


    fun swapList(users:ArrayList<Users>){
       if (users.size > 0){
           emptyContainer.visibility = View.GONE
           usersList                 = users
           adapter                   = FollowAdapter(Base.instance,users,this,1)
           list.visibility           = View.VISIBLE
           list.adapter              = adapter


       }else{
           val defaultErrorIcon = VectorDrawableCompat.create(Base.instance.resources, R.drawable.search_light,    errorImg.context.theme)
           errorImg.setImageDrawable(defaultErrorIcon)

           errorText.text = Base.instance.resources.getString(R.string.error_empty_search_result)


       }

    }


    fun failedGetList(error:String = ""){

//        progressLay.visibility = View.GONE
        log.e("FeedFragment => method => failedGetList errorCode => $error")
        if (adapter != null && adapter!!.users.size != 0){
            log.e("list bor lekin xatolik shundo ozini qoldiramiz")


            emptyContainer.visibility = View.GONE
            list.visibility = View.VISIBLE


        }else{
            log.e("list null yoki list bom bosh")

            val connectErrorIcon = VectorDrawableCompat.create(Base.instance.resources, R.drawable.network_error, errorImg.context.theme)
            val defaultErrorIcon = VectorDrawableCompat.create(Base.instance.resources, R.drawable.search_light,    errorImg.context.theme)
            if (error == ""){
                errorImg.setImageDrawable(defaultErrorIcon)
            }else{
                errorImg.setImageDrawable(connectErrorIcon)

            }
            errorText.text = error
            emptyContainer.visibility = View.VISIBLE
            list.visibility = View.GONE
        }

    }

    override fun click(position: Int) {

       val user = adapter!!.users.get(position)

       if (user.userId != this.user.userId){
           var type = ProfileFragment.FOLLOW
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
           val go = Intent(activity,FollowActivity::class.java)
           val bundle = Bundle()
           bundle.putString("username",user.username)
           bundle.putString("photo",   user.photo150)
           bundle.putString("userId",  user.userId)
           bundle.putString(ProfileFragment.F_TYPE,type)
           go.putExtra(FollowActivity.TYPE,FollowActivity.PROFIL_T)
           go.putExtras(bundle)
           startActivity(go)
       }else{
           connectActivity!!.goNext(Const.PROFIL_PAGE,"")
       }
    }

    override fun data(data: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        log.d("${requestCode} $resultCode")

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


    override fun onResume() {
        super.onResume()
        log.d("onresume")
    }

    override fun onStart() {
        super.onStart()
        log.d("onresume")

    }

}