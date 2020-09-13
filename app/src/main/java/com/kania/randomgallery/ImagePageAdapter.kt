package com.kania.randomgallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import kotlinx.android.synthetic.main.item_image.view.*

class ImagePageAdapter(private val list: ArrayList<ImageItem>): PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val view = inflater.inflate(R.layout.item_image, container, false)
        view.image.setImageURI(list[position].imageUri)
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View?)
    }

    override fun getCount(): Int = list.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

}