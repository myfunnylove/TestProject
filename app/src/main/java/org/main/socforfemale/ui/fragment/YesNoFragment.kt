package org.main.socforfemale.ui.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import org.main.socforfemale.R

/**
 * Created by macbookpro on 06.09.17.
 */
class YesNoFragment : DialogFragment() {

    var listener:DialogClickListener? = null
    companion object {
        var mInstance:YesNoFragment? = null
        val NO = 1
        val YES = 2
        val TAG = "yesnofragment"
        fun instance() : YesNoFragment{

            if (mInstance == null) mInstance = YesNoFragment()

            return mInstance!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_dialog,container,false)

        view.findViewById(R.id.no).setOnClickListener {
            listener!!.click(NO)
        }

        view.findViewById(R.id.yes).setOnClickListener {
            listener!!.click(YES)
        }

        return view
    }

    fun setDialogClickListener(dialogClickListener: DialogClickListener){
        listener = dialogClickListener
    }

    interface DialogClickListener{
        fun click(whichButton:Int)
    }
}