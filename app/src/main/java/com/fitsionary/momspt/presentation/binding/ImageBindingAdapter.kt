package com.fitsionary.momspt.presentation.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.fitsionary.momspt.R

@BindingAdapter("image_url")
fun setImageFromImageUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}

@BindingAdapter("android:src")
fun setImageFromResource(imageView: ImageView, resource: Int) {
    Glide.with(imageView.context)
        .load(resource)
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}

@BindingAdapter("profile_image_url")
fun setProfileImageFromImageUrl(imageView: ImageView, imageUrl: String) {
    Glide.with(imageView.context)
        .load(imageUrl)
        .circleCrop()
        .placeholder(R.drawable.placeholder)
        .into(imageView)
}