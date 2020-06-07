/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

@BindingAdapter("imageUrl")
fun ImageView.bindUrl(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
                .load(imgUri)
                .apply(
                        RequestOptions()
                                .placeholder(R.drawable.loading_animation)
                                .error(R.drawable.ic_broken_image)
                )
                .into(this)
    } ?: run {
        this.setImageDrawable(null)
    }
}

@BindingAdapter("listData")
fun RecyclerView.bind(data: List<MarsProperty>?) {
    (this.adapter as? PhotoGridAdapter)?.submitList(data)
}

@BindingAdapter("marsApiStatus")
fun ImageView.bindStatus(status: MarsApiStatus?) {
    when (status) {
        MarsApiStatus.LOADING -> {
            isVisible = true
            setImageResource(R.drawable.loading_animation)
        }
        MarsApiStatus.ERROR -> {
            isVisible = true
            setImageResource(R.drawable.ic_connection_error)
        }
        MarsApiStatus.DONE -> {
            isVisible = false
        }
    }
}

@BindingAdapter("isRental")
fun TextView.isRental(marsPropertyType: String) {
    text = if (marsPropertyType == "buy") {
        context.getString(R.string.sale)
    } else {
        context.getString(R.string.rent)
    }
}
