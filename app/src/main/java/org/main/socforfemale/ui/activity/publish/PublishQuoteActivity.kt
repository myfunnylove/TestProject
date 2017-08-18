package org.main.socforfemale.ui.activity.publish

import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_follow.*
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import kotlinx.android.synthetic.main.activity_publish_quote.*
import org.main.socforfemale.adapter.ColorPaletteAdapter
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.resources.utils.Const
import java.io.File

/**
 * Created by Michaelan on 5/26/2017.
 */
class PublishQuoteActivity :BaseActivity(){



    var  TEXT_SIZE = 16f

    override fun getLayout(): Int {
        return R.layout.activity_publish_quote
    }

    override fun initView() {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {

                onBackPressed()
            }

        colorList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        colorList.setHasFixedSize(true)

        colorList.adapter = ColorPaletteAdapter(object  : AdapterClicker{
            override fun data(data: String) {

            }

            override fun click(position: Int) {
                quoteText.setTextColor(ContextCompat.getColor(applicationContext,Const.colorPalette.get(position)!!.drawable))
            }

        },this,Const.colorPalette)

        quoteText.setTextSize(Const.TEXT_SIZE_DEFAULT)
        textSize1.setTextSize(Const.TEXT_SIZE_DEFAULT)
        textSize2.setTextSize(Const.TEXT_SIZE_17)
        textSize3.setTextSize(Const.TEXT_SIZE_22)
        textSize1.setOnClickListener {
            TEXT_SIZE = Const.TEXT_SIZE_DEFAULT
            quoteText.textSize = TEXT_SIZE
        }
        textSize2.setOnClickListener {
            TEXT_SIZE = Const.TEXT_SIZE_17
            quoteText.textSize = TEXT_SIZE
        }
        textSize3.setOnClickListener {
            TEXT_SIZE = Const.TEXT_SIZE_22
            quoteText.textSize = TEXT_SIZE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_publish,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.publish -> {
                Toast.makeText(this,"Published",Toast.LENGTH_SHORT).show()
            }
        }

        return true
    }
}