package com.fitsionary.momspt.presentation.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.fitsionary.momspt.util.listener.OnItemClickListener

abstract class BaseViewHolder<B : ViewDataBinding, T>(
    @LayoutRes layoutResId: Int,
    parent: ViewGroup,
    private val bindingVariableItemId: Int?,
    private val bindingVariableListenerId: Int?
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
) {
    val binding: B = DataBindingUtil.bind(itemView)!!

    fun bind(item: T, listener: OnItemClickListener<T>) {
        try {
            bindingVariableItemId?.let {
                binding.setVariable(it, item)
            }
            bindingVariableListenerId?.let {
                binding.setVariable(it, listener)
            }
            binding.executePendingBindings()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}