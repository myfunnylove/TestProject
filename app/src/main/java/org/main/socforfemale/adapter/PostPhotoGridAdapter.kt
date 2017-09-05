package org.main.socforfemale.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.main.socforfemale.R
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.Http
import org.main.socforfemale.model.Image
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.resources.zoomimageview.ImageViewer


/**
 * Created by Michaelan on 6/28/2017.
 */
class PostPhotoGridAdapter(ctx:Context,list:ArrayList<Image>) : RecyclerView.Adapter<PostPhotoGridAdapter.Holder>() {


    val context = ctx
    val images = list
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    val isVertical = true
    var hierarchyBuilder:GenericDraweeHierarchyBuilder? =null
    var cachedImages:ArrayList<Bitmap>? = null
    init {
        setHasStableIds(true)
        hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(Base.get.resources)
                .setFailureImage(VectorDrawableCompat.create(Base.get.resources, R.drawable.image_broken_variant_white, null))
                .setProgressBarImage(VectorDrawableCompat.create(Base.get.resources, R.drawable.image, null))
                .setPlaceholderImage(VectorDrawableCompat.create(Base.get.resources, R.drawable.image, null))
        cachedImages = ArrayList()
        images.forEach { img ->
            Picasso.with(context)
                    .load(Http.BASE_URL+img.image)
                    .into(object : Target{
                        override fun onPrepareLoad(p0: Drawable?) {

                        }

                        override fun onBitmapFailed(p0: Drawable?) {
                        }

                        override fun onBitmapLoaded(p0: Bitmap?, p1: Picasso.LoadedFrom?) {

                             cachedImages!!.add(p0!!)

                            log.d("IMAGES: ${cachedImages!!.get(0)}")
                        }

                    })
        }
    }
    override fun getItemCount(): Int {
        return images.size
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun onBindViewHolder(h: Holder?, i: Int) {
        val img = images.get(i)

      //  var dimenId = -1
        val itemView = h!!.itemView

        //if(i == 0)  dimenId = R.dimen.staggered_child_xlarge else   dimenId = R.dimen.staggered_child_small
//        if (i % 3 == 0)
//            dimenId = R.dimen.staggered_child_medium
//        else if (i % 5 == 0)
//            dimenId = R.dimen.staggered_child_large
//        else if (i % 7 == 0)
//            dimenId = R.dimen.staggered_child_xlarge
//        else
//            dimenId = R.dimen.staggered_child_small


        val span = if (i == 0) (images.size - 1) else 1


     //   val size = Base.get.resources.getDimensionPixelSize(dimenId)

        val params: GridLayoutManager.LayoutParams = itemView.layoutParams as GridLayoutManager.LayoutParams

        //if (isVertical) params.width = size else  params.height = size



        itemView.layoutParams = params

        if(h.photo.drawable == null){
//            Glide.with(context)
//                    .load(Http.BASE_URL+img.image640)
//
//                    .error(R.drawable.upload_error)
//                    .into(h.photo)

            Picasso.with(context)
                    .load( Http.BASE_URL+images.get(i).image640)

                    .fit()
                    .centerCrop()
                    .into(h.photo)

            h.photo.tag = Http.BASE_URL+img.image640
        }

        h.photo.setOnClickListener {

            ImageViewer.Builder(context,images)
                    .setFormatter(object : ImageViewer.Formatter<Image>{
                        override fun format(t: Image?): String {
                            return Http.BASE_URL+t!!.image
                        }

                    })
                    .setStartPosition(i)
                    .hideStatusBar(true)
                    .allowZooming(true)
                    .allowSwipeToDismiss(true)
                    .setBackgroundColor(Base.get.resources.getColor(R.color.transparent80))
                    .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                    .show()


        }
    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder {

        return Holder(inflater.inflate(R.layout.res_post_photo_item,p0,false))
    }

    class Holder(view: View) : RecyclerView.ViewHolder(view) {


        var photo:AppCompatImageView = view.findViewById(R.id.photo) as AppCompatImageView
    }
}