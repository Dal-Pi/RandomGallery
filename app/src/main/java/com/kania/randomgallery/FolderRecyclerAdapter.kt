package com.kania.randomgallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_thumbnail_folder.view.*

class FolderRecyclerAdapter(private val items: ArrayList<FolderItem>,
                            private var mItemEventListener: OnItemEventListener?) :
    RecyclerView.Adapter<FolderRecyclerAdapter.ViewHolder>() {

    interface OnItemEventListener {
        fun onItemClicked(clickedItemName: String)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bind(listener: View.OnClickListener, item: FolderItem) {
            view.imageThumbnail.setImageURI(item.thumbnailUri)
            view.textName.text = item.name
            //view.textCount.text = item.count.toString()
            view.setOnClickListener(listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context).inflate(R.layout.item_thumbnail_folder, parent, false)
        return FolderRecyclerAdapter.ViewHolder(inflatedView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            Log.d("RG", "FolderRecyclerAdapter.onBindViewHolder() called")
            mItemEventListener?.onItemClicked(item.name)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun getItemCount() = items.size
}