package org.main.socforfemale.ui.fragment

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.bindView
import kotlinx.android.synthetic.main.activity_settings.*
import org.main.socforfemale.R
import org.main.socforfemale.adapter.PushAdapter
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.model.Action
import org.main.socforfemale.model.Push
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.builder.EmptyContainer
import org.main.socforfemale.resources.customviews.loadmorerecyclerview.EndlessRecyclerViewScrollListener
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.Prefs
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/19/2017.
 */
class NotificationFragment(): BaseFragment(), Viewer{

    companion object {
        var TAG:String  = "NotificationFragment"

        fun newInstance(): NotificationFragment {


            val newsFragment = NotificationFragment()
            val args = Bundle()

            newsFragment.arguments = args
            return newsFragment
        }
    }
    var connectActivity:GoNext?     = null

    lateinit var emptyContainer:EmptyContainer
    lateinit var list:RecyclerView

    lateinit var adapter:PushAdapter

    var progressLay            by Delegates.notNull<ViewGroup>()
    var swipeRefreshLayout     by Delegates.notNull<SwipeRefreshLayout>()
    var scroll: EndlessRecyclerViewScrollListener?         = null
    val user = Prefs.Builder().getUser()
    lateinit var manager:LinearLayoutManager

    fun connect(connActivity: GoNext){
        connectActivity   = connActivity

    }
    override fun getFragmentView(): Int = R.layout.fragment_notification

    override fun init() {
        progressLay           = rootView.findViewById(R.id.progressLay)    as ViewGroup

        swipeRefreshLayout    = rootView.findViewById(R.id.swipeRefreshLayout)    as SwipeRefreshLayout
        list  = rootView.findViewById(R.id.list)           as RecyclerView
        manager = LinearLayoutManager(activity)
        list.layoutManager = manager
        list.setHasFixedSize(true)

        scroll = object : EndlessRecyclerViewScrollListener(manager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {

            }

            override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
            }

        }
        list.addOnScrollListener(scroll)
        swipeRefreshLayout.isEnabled  = false
        swipeRefreshLayout.setOnRefreshListener {

        }

        emptyContainer = EmptyContainer.Builder()
                .setIcon(R.drawable.notification_light)
                .setText(R.string.error_empty_universal)
                .initLayoutForFragment(rootView)

                .build()

        emptyContainer.hide()


        //TODO TEST NOTIFICATIOn

        val pushes = ArrayList<Push>()

        pushes.add(Push(Const.Push.LIKE,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))
        pushes.add(Push(Const.Push.LIKE,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))
        pushes.add(Push(Const.Push.LIKE,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))
        pushes.add(Push(Const.Push.REQUESTED,user.profilPhoto,user.userName, Action(activity.resources.getString(R.string.allow),"21")))
        pushes.add(Push(Const.Push.LIKE,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))
        pushes.add(Push(Const.Push.LIKE,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))
        pushes.add(Push(Const.Push.REQUESTED,user.profilPhoto,user.userName, Action(activity.resources.getString(R.string.allow),"21")))
        pushes.add(Push(Const.Push.REQUESTED,user.profilPhoto,user.userName, Action(activity.resources.getString(R.string.allow),"21")))
        pushes.add(Push(Const.Push.REQUESTED,user.profilPhoto,user.userName, Action(activity.resources.getString(R.string.allow),"21")))
        pushes.add(Push(Const.Push.OTHER,user.profilPhoto,user.userName, Action(activity.resources.getString(R.string.follow),"21")))
        pushes.add(Push(Const.Push.COMMENT,user.profilPhoto,user.userName, Action(user.profilPhoto,"21")))

        adapter = PushAdapter(activity,pushes)
        hideProgress()
        list.adapter = adapter
    }



    override fun initProgress() {
    }

    override fun showProgress() {
        progressLay.visibility = View.VISIBLE

    }

    override fun hideProgress() {
        progressLay.visibility = View.GONE
    }

    override fun onSuccess(from: String, result: String) {
    }

    override fun onFailure(from: String, message: String, erroCode: String) {
    }

}