package com.kania.randomgallery

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_thumbnail_image.view.imageThumbnail

class ImageRecyclerAdapter(private val items: ArrayList<ImageItem>,
                           private var mItemEventListener: OnItemEventListener?) :
    RecyclerView.Adapter<ImageRecyclerAdapter.ViewHolder>() {

    interface OnItemEventListener {
        fun onItemClicked(/*clickedItemUri: Uri*/position: Int)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bind(listener: View.OnClickListener, item: ImageItem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.imageThumbnail.setImageBitmap(getThumbnail(view.context, item.imageUri))
            } else {
                view.imageThumbnail
                    .setImageURI(thumbnailURIFromOriginalURI(view.context, item.imageUri))
            }
            view.setOnClickListener(listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail_image, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Log.d("RG", "ImageRecyclerAdapter.onBindViewHolder() called")
            //mItemEventListener?.onItemClicked(item.imageUri)
            mItemEventListener?.onItemClicked(position)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.size
}