package org.main.socforfemale.adapter

import android.content.Context
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import org.main.socforfemale.R
import org.main.socforfemale.model.Push
import org.main.socforfemale.resources.customviews.CircleImageView
import org.main.socforfemale.resources.utils.Const

/**
 * Created by myfunnylove on 17.09.17.
 */
class PushAdapter(private val ctx:Context,private val list:ArrayList<Push> ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int = list.get(position).title



    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){

            Const.Push.LIKE -> return Like(inflater.inflate(R.layout.res_item_push_like,parent,false))

            Const.Push.COMMENT -> return Comment(inflater.inflate(R.layout.res_item_push_like,parent,false))

            Const.Push.REQUESTED -> return Requested(inflater.inflate(R.layout.res_item_push_requested,parent,false))

            else -> return Other(inflater.inflate(R.layout.res_item_push_requested,parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        val push = list.get(position)

        when(getItemViewType(position)){

            Const.Push.LIKE -> {

                val like = holder as Like

                    Picasso.with(ctx)
                            .load(push.photo)
                            .error(VectorDrawableCompat.create(ctx.resources,R.drawable.account,ctx.theme))
                            .into(like.avatar)

                like.username.text = ctx.resources.getString(R.string.user,push.username)
                like.body.text = ctx.resources.getString(R.string.pushLikeBody)

                Picasso.with(ctx)
                        .load(push.action.actionName)
                        .error(VectorDrawableCompat.create(ctx.resources,R.drawable.image_broken_variant_white,ctx.theme))
                        .into(like.mypost)

                like.mypost.setOnClickListener{
                    Toast.makeText(ctx,"Like pressed ",Toast.LENGTH_SHORT).show()
                }
            }

            Const.Push.COMMENT -> {

                val comment = holder as Comment
                Picasso.with(ctx)
                        .load(push.photo)
                        .error(VectorDrawableCompat.create(ctx.resources,R.drawable.account,ctx.theme))
                        .into(comment.avatar)
                comment.username.text = ctx.resources.getString(R.string.user,push.username)
                comment.body.text = ctx.resources.getString(R.string.pushCommentBody)

                Picasso.with(ctx)
                        .load(push.action.actionName)
                        .error(VectorDrawableCompat.create(ctx.resources,R.drawable.image_broken_variant_white,ctx.theme))
                        .into(comment.mypost)

                comment.mypost.setOnClickListener{
                    Toast.makeText(ctx,"Comment pressed ",Toast.LENGTH_SHORT).show()
                }
            }

            Const.Push.REQUESTED -> {

                val requested = holder as Requested
                Picasso.with(ctx)
                        .load(push.photo)
                        .error(VectorDrawableCompat.create(ctx.resources,R.drawable.account,ctx.theme))
                        .into(requested.avatar)

                requested.username.text = ctx.resources.getString(R.string.user,push.username)

                requested.body.text = ctx.resources.getString(R.string.pushRequestBody)


                requested.action.setText(push.action.actionName)

                requested.action.setOnClickListener{
                    Toast.makeText(ctx,"requested pressed ",Toast.LENGTH_SHORT).show()
                }
            }

            else -> {

                val other = holder as Other
                Picasso.with(ctx)
                        .load(push.photo)
                        .error(VectorDrawableCompat.create(ctx.resources,R.drawable.account,ctx.theme))
                        .into(other.avatar)

                other.username.text = ctx.resources.getString(R.string.user,push.username)

                other.body.text = ctx.resources.getString(R.string.pushFollowBody)

                other.action.setText(push.action.actionName)

                other.action.setOnClickListener{
                    Toast.makeText(ctx,"other pressed ",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }


    class Like(view: View) : RecyclerView.ViewHolder(view) {

        val container  = view.findViewById(R.id.container) as ViewGroup
        val avatar = view.findViewById(R.id.avatar) as CircleImageView
        val username = view.findViewById(R.id.username) as TextView
        val body = view.findViewById(R.id.body) as TextView
        val mypost = view.findViewById(R.id.mypost) as ImageView

    }

    class Comment(view: View) : RecyclerView.ViewHolder(view) {
        val container  = view.findViewById(R.id.container) as ViewGroup
        val avatar = view.findViewById(R.id.avatar) as CircleImageView
        val username = view.findViewById(R.id.username) as TextView
        val body = view.findViewById(R.id.body) as TextView
        val mypost = view.findViewById(R.id.mypost) as ImageView
    }

    class Requested(view: View) : RecyclerView.ViewHolder(view) {
        val container  = view.findViewById(R.id.container) as ViewGroup
        val avatar = view.findViewById(R.id.avatar) as CircleImageView
        val username = view.findViewById(R.id.username) as TextView
        val body = view.findViewById(R.id.body) as TextView
        val action = view.findViewById(R.id.action) as Button
    }

    class Other(view: View) : RecyclerView.ViewHolder(view) {
        val container  = view.findViewById(R.id.container) as ViewGroup
        val avatar = view.findViewById(R.id.avatar) as CircleImageView
        val username = view.findViewById(R.id.username) as TextView
        val body = view.findViewById(R.id.body) as TextView
        val action = view.findViewById(R.id.action) as Button
    }
}