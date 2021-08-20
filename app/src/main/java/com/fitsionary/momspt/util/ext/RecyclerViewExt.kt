package com.fitsionary.momspt.util.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fitsionary.momspt.presentation.base.BaseRecyclerViewAdapter

@Suppress("UNCHECKED_CAST")
@BindingAdapter("bindItems")
fun RecyclerView.bindItems(list: List<Any>?) {
    (this.adapter as? BaseRecyclerViewAdapter<*, Any>)?.submitList(list)
}