package com.kania.randomgallery

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_thumbnail_folder.view.*
import kotlinx.android.synthetic.main.item_thumbnail_folder.view.imageThumbnail

class FolderRecyclerAdapter(private val items: ArrayList<FolderItem>,
                            private var mItemEventListener: OnItemEventListener?) :
    RecyclerView.Adapter<FolderRecyclerAdapter.ViewHolder>() {

    interface OnItemEventListener {
        fun onItemClicked(clickedItemName: String)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bind(listener: View.OnClickListener, item: FolderItem) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.imageThumbnail.setImageBitmap(getThumbnail(view.context, item.firstImageUri))
            } else {
                view.imageThumbnail
                    .setImageURI(thumbnailURIFromOriginalURI(view.context, item.firstImageUri))
            }
            view.textName.text = item.name
            //view.textCount.text = item.count.toString()
            view.setOnClickListener(listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail_folder, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Log.d("RG", "FolderRecyclerAdapter.onBindViewHolder() called")
            mItemEventListener?.onItemClicked(item.bucketId)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.size
}