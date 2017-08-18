package org.main.socforfemale.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import org.main.socforfemale.R
import org.main.socforfemale.mvp.Viewer
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.properties.Delegates


/**
 * Created by Michaelan on 5/18/2017.
 */
abstract class BaseActivity : AppCompatActivity (){

    var progress:ProgressBar by Delegates.notNull<ProgressBar>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayout())
        initView()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    abstract fun getLayout():Int

    abstract fun initView():Unit



}