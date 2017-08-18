package org.main.socforfemale.resources.utils

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.RelativeLayout


/**
 * Created by Michaelan on 5/31/2017.
 */
object CustomAnim {

    fun SlideToAbove(rl_footer:ViewGroup) {
        var slide: Animation? = null
        slide = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -5.0f)

        slide.duration = 400
        slide.fillAfter = true
        slide.isFillEnabled = true
        rl_footer.startAnimation(slide)

        slide.setAnimationListener(object : AnimationListener {

            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {

                rl_footer.clearAnimation()

                val lp = RelativeLayout.LayoutParams(
                        rl_footer.getWidth(), rl_footer.getHeight())
                // lp.setMargins(0, 0, 0, 0);
                lp.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                rl_footer.setLayoutParams(lp)

            }

        })

    }


    fun setScaleAnimation(view: View, time:Int) {
        val anim = ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        anim.duration = time.toLong()
        view.startAnimation(anim)
    }

    fun translateBottomToTop(view: View, time:Int) {
        val anim = TranslateAnimation(0f,0f,view.getHeight().toFloat(),0f)
        anim.duration = time.toLong()
        view.startAnimation(anim)
    }
}