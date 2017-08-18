package org.main.socforfemale.ui.fragment

import android.os.Bundle
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.AppCompatImageView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import butterknife.bindView
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.log
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/19/2017.
 */
class UploadFragment()  : BaseFragment(){




    override fun getFragmentView(): Int {
      return R.layout.fragment_upload
    }

    override fun init() {


    }
    companion object {
        fun newInstance(): UploadFragment {
            val newsFragment = UploadFragment()
            val args = Bundle()

            newsFragment.arguments = args
            return newsFragment
        }
    }
    var connectActivity:GoNext?     = null
    fun connect(connActivity: GoNext){
        connectActivity   = connActivity

    }
}