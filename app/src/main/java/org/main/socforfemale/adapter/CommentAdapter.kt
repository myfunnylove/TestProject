package org.main.socforfemale.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.bumptech.glide.Glide
import org.main.socforfemale.R
import org.main.socforfemale.rest.Http
import org.main.socforfemale.connectors.AdapterClicker
import org.main.socforfemale.model.Comment
import org.main.socforfemale.resources.customviews.CircleImageView


class CommentAdapter(context:Context,list:ArrayList<Comment>,clicker:AdapterClicker) : RecyclerView.Adapter<CommentAdapter.Holder>() {

    val ctx= context
    var comments = list
    val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val clicker = clicker

    var animationsLocked = false
    var lastAnimatedPosition = -1
    var delayEnterAnimation = true
    override fun onBindViewHolder(h: Holder?, i: Int) {

        h!!.itemView.runEnterAnimation(i)

        val comment = comments.get(i)

        val url = if (!comment.avatar.isNullOrEmpty() && comment.avatar.startsWith("http")) comment.avatar else  Http.BASE_URL+comment.avatar
        Glide.with(ctx)
                .load(url)
                .error(R.drawable.account)
                .into(h.avatar)

        h.comment.text  = comment.comment.replace("\\n","\n")
        h.username.text = comment.username


    }

    override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): Holder {
        return Holder(inflater.inflate(R.layout.res_comment_box,p0,false))
    }

    override fun getItemCount(): Int {
        return comments.size;
    }

    class Holder(view: View) :RecyclerView.ViewHolder(view){
        val avatar    = view.findViewById(R.id.avatar)    as CircleImageView
        val username  = view.findViewById(R.id.username)  as TextView
        val container = view.findViewById(R.id.container) as ViewGroup
        val comment   = view.findViewById(R.id.comment)   as TextView
    }

    fun  swapList(list:ArrayList<Comment>){
        comments = list
        notifyDataSetChanged()
    }

    fun swapLast(list: ArrayList<Comment>) {
        val lastItemPostition = (comments.size + 1)
        comments.addAll(list)
        notifyItemRangeInserted(lastItemPostition,list.size)
    }

    fun View.runEnterAnimation(position:Int){

        //if (animationsLocked) return
        if (position > lastAnimatedPosition){

            lastAnimatedPosition = position

            this.translationY = 100f

            this.alpha        = 0f
            this.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setStartDelay(if (delayEnterAnimation) (20 * position).toLong() else 0)
                    .setInterpolator(DecelerateInterpolator(2f))
                    .setDuration(300)
                    .setListener(object : AnimatorListenerAdapter(){
                        override fun onAnimationEnd(animation: Animator?) {
                           if (translationY == 100f) translationY = 0f
                            animationsLocked = true;
                        }
                    }).start()
        }

    }
}
