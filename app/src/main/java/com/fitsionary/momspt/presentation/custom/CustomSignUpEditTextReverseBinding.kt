package com.fitsionary.momspt.presentation.custom

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import kotlinx.android.synthetic.main.custom_sign_up_edit_text.view.*

object CustomSignUpEditTextReverseBinding {
    @JvmStatic
    @BindingAdapter("inputText")
    fun setInputText(view: CustomSignUpEditText, content: String?) {
        val old = view.et_custom_sign_up.text.toString()
        if (old != content) {
            view.et_custom_sign_up.setText(content)
        }
    }

    @JvmStatic
    @BindingAdapter("contentAttrChanged")
    fun setInputTextInverseBindingListener(
        view: CustomSignUpEditText,
        listener: InverseBindingListener?
    ) {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                listener?.onChange()
            }
        }
        view.et_custom_sign_up.addTextChangedListener(watcher)
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "inputText", event = "contentAttrChanged")
    fun getInputText(view: CustomSignUpEditText): String {
        return view.et_custom_sign_up.text.toString()
    }
}