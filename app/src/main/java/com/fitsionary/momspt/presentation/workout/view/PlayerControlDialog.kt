package com.fitsionary.momspt.presentation.workout.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import com.fitsionary.momspt.R

class PlayerControlDialog constructor(context: Context) : Dialog(context) {
    private lateinit var btn: ImageView
    private lateinit var listener: MyDialogClickedListener

    fun start() {
        setCanceledOnTouchOutside(true)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_player_control)
        btn = findViewById(R.id.btn_control)
        btn.setOnClickListener {
            listener.onControllerClicked()
        }
        this.show()
    }

    fun setOnClickedListener(listener: () -> Unit) {
        this.listener = object : MyDialogClickedListener {
            override fun onControllerClicked() {
                listener()
            }
        }
    }

    interface MyDialogClickedListener {
        fun onControllerClicked()
    }
}