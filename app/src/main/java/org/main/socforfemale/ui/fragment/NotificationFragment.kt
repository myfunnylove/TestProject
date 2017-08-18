package org.main.socforfemale.ui.fragment

import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.bindView
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.GoNext
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
    fun connect(connActivity: GoNext){
        connectActivity   = connActivity

    }
    var emptyContainer by Delegates.notNull<LinearLayout>()
    override fun getFragmentView(): Int {
        return R.layout.fragment_notification

    }

    override fun init() {
        emptyContainer = rootView.findViewById(R.id.emptyContainer) as LinearLayout

    }
}