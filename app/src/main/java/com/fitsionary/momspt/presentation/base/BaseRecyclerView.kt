package com.fitsionary.momspt.presentation.base

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.fitsionary.momspt.util.listener.OnItemClickListener

abstract class BaseRecyclerView<B : ViewDataBinding, T : Any>(
    @LayoutRes private val layoutResId: Int,
    private val bindingVariableItemId: Int? = null,
    private val bindingVariableListenerId: Int? = null
) : RecyclerView.Adapter<BaseViewHolder<B, T>>() {
    private val items = mutableListOf<T>()

    fun replaceAll(items: List<T>?) {
        items?.let {
            this.items.run {
                clear()
                addAll(it)
            }
        }
    }

    var onItemClickListener = object : OnItemClickListener<T> {
        override fun onClick(item: T) {}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        object : BaseViewHolder<B, T>(
            layoutResId = layoutResId,
            parent = parent,
            bindingVariableItemId = bindingVariableItemId,
            bindingVariableListenerId = bindingVariableListenerId
        ) {}

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: BaseViewHolder<B, T>, position: Int) {
        holder.bind(items[position], onItemClickListener)
    }
}

