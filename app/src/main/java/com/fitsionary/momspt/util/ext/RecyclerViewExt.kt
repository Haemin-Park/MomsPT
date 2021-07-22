package com.fitsionary.momspt.util.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitsionary.momspt.presentation.base.BaseRecyclerView

@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceAll")
fun RecyclerView.replaceAll(list: List<Any>?) {
    (this.adapter as? BaseRecyclerView<*, Any>)?.run {
        replaceAll(list)
        notifyDataSetChanged()
    }
}