package com.kania.randomgallery

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_thumbnail_folder.view.*
import kotlinx.android.synthetic.main.item_thumbnail_folder.view.imageThumbnail
import kotlinx.android.synthetic.main.item_thumbnail_image.view.*

class FolderRecyclerAdapter(private val items: ArrayList<FolderItem>,
                            private var mItemEventListener: OnItemEventListener?) :
    RecyclerView.Adapter<FolderRecyclerAdapter.ViewHolder>() {

    interface OnItemEventListener {
        fun onItemClicked(clickedItemName: String)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bind(listener: View.OnClickListener, item: FolderItem) {
            view.imageThumbnail
                .setImageURI(thumbnailURIFromOriginalURI(view.context, item.firstImageUri))
            view.textName.text = item.name
            //view.textCount.text = item.count.toString()
            view.setOnClickListener(listener)
        }

        //TODO duplicated with imagelist
        private fun thumbnailURIFromOriginalURI(context: Context, selectedImageUri: Uri): Uri {
            val rowId = selectedImageUri.lastPathSegment?.toLong()
            return uriToThumbnail(context, "" + rowId)
        }
        private fun uriToThumbnail(context: Context, imageId: String): Uri {
            val projection = arrayOf(MediaStore.Images.Thumbnails.DATA)
            val thumbnailCursor = context.contentResolver.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection,
                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                arrayOf(imageId),
                null)
            if (thumbnailCursor == null) {
                Log.d("RG", "uriToThumbnail() null case")
                return Uri.parse("android.resource://com.kania.randomgallery/drawable/no_thumbnail")
            } else if (thumbnailCursor.moveToFirst()) {
                Log.d("RG", "uriToThumbnail() has first")
                val thumbnailColumnIndex = thumbnailCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
                val thumbnailPath = thumbnailCursor.getString(thumbnailColumnIndex)
                thumbnailCursor.close()
                return Uri.parse(thumbnailPath)
            } else {
                Log.d("RG", "uriToThumbnail() not have first")
                MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, imageId.toLong(), MediaStore.Images.Thumbnails.MINI_KIND, null)
                thumbnailCursor.close()
                return uriToThumbnail(context, imageId)
            }
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