package org.main.socforfemale.ui.activity.publish

import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.bumptech.glide.Glide
import org.main.socforfemale.R
import org.main.socforfemale.base.BaseActivity
import kotlinx.android.synthetic.main.activity_publish_image.*
import org.main.socforfemale.resources.utils.Const
import java.io.File

/**
 * Created by Michaelan on 5/26/2017.
 */
class PublishImageActivity :BaseActivity(){



    var  uri:Uri? = null
    override fun getLayout(): Int {
        return R.layout.activity_publish_image
    }

    override fun initView() {
            setSupportActionBar(toolbar)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener {
                uri  = null
                onBackPressed()
            }
        if (intent.getStringExtra(Const.PUBLISH_IMAGE) != null){
            uri = Uri.fromFile(File(intent.getStringExtra(Const.PUBLISH_IMAGE)))

                Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.1f)
                        .placeholder(R.drawable.empty_picture)
                        .error(R.drawable.empty_picture)
                        .into(loadedImage)

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