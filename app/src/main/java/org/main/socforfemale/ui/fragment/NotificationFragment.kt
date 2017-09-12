package org.main.socforfemale.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.bindView
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.pattern.builder.EmptyContainer
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/19/2017.
 */
class NotificationFragment(): BaseFragment(){
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

    fun connect(connActivity: GoNext){
        connectActivity   = connActivity

    }
    override fun getFragmentView(): Int {
        return R.layout.fragment_notification

    }

    override fun init() {
        emptyContainer = EmptyContainer.Builder()
                .setIcon(R.drawable.notification_light)
                .setText(R.string.error_empty_universal)
                .initLayoutForFragment(rootView)

                .build()

        emptyContainer.show()
    }
}