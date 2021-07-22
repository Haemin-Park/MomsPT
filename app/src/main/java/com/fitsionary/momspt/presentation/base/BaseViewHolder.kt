package com.fitsionary.momspt.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<B : ViewDataBinding, T>(
    @LayoutRes layoutResId: Int,
    parent: ViewGroup,
    private val bindingVariableId: Int?
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
) {
    private val binding: B = DataBindingUtil.bind(itemView)!!

    fun bind(item: T) {
        try {
            bindingVariableId?.let {
                binding.setVariable(it, item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}