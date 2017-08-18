package org.main.socforfemale.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.main.socforfemale.base.BaseFragment
import org.main.socforfemale.connectors.GoNext
import org.main.socforfemale.ui.fragment.*
import kotlin.properties.Delegates

/**
 * Created by Michaelan on 5/18/2017.
 */
class MainMenuPagerAdapter(fm: FragmentManager?,map:HashMap<Int,BaseFragment>) : FragmentPagerAdapter(fm) {


    var mainFragments by Delegates.notNull<HashMap<Int,BaseFragment>>()
    init {


        mainFragments = HashMap(map)

    }
    override fun getItem(p0: Int): BaseFragment {
      return  mainFragments.get(p0)!!
    }

    override fun getCount(): Int {
        return mainFragments.size
    }



}