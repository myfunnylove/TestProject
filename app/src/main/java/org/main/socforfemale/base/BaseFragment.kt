package org.main.socforfemale.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.main.socforfemale.resources.utils.bindView
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/18/2017.
 */
abstract class BaseFragment : Fragment(){

    var rootView by Delegates.notNull<View>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        rootView = inflater!!.inflate(getFragmentView(),container,false)

        init()
        return rootView

    }


    abstract fun getFragmentView():Int

    abstract fun init():Unit

}