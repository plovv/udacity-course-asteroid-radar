package com.udacity.asteroidradar.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.main.AsteroidAdapter

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("statusIconDescription")
fun bindAsteroidStatusImageDescription(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.contentDescription = imageView.context.getString(R.string.description_icon_potentially_hazardous)
    } else {
        imageView.contentDescription = imageView.context.getString(R.string.description_icon_potentially_non_hazardous)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("asteroidStatusImageDescription")
fun bindDetailsStatusImageDescription(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.contentDescription = imageView.context.getString(R.string.description_image_potentially_hazardous)
    } else {
        imageView.contentDescription = imageView.context.getString(R.string.description_image_potentially_non_hazardous)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Asteroid>?) {
    val adapter = recyclerView.adapter as AsteroidAdapter
    adapter.submitList(data)
}

@BindingAdapter("fromImgUrl")
fun bindImageUrlWithPicasso(imgView: ImageView, url: String?) {
    if(url != null && url != ""){
        Picasso.with(imgView.context)
            .load(url)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .into(imgView)
    }
}

@BindingAdapter("networkError", "asteroids")
fun showProgressStatus(view: View, networkError: Boolean, asteroids: Any?) {
    view.visibility = if (asteroids != null) View.GONE else View.VISIBLE

    if(networkError) {
        view.visibility = View.GONE
    }
}