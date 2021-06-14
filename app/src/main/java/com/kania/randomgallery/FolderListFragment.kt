package com.kania.randomgallery

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.fragment_folderlist.*
import java.lang.RuntimeException

class FolderListFragment() : Fragment(),
    FolderRecyclerAdapter.OnItemEventListener {

    interface OnFolderEventListener {
        fun onFolderSelected(clickedFolderName: String)
    }

    //TODO change default image
    private val defaultThumbnailUri = Uri.parse("android.resource://com.kania.randomgallery/drawable/no_thumbnail")

    private var mFolderEventListener: OnFolderEventListener? = null

    companion object {
        fun newInstance(): FolderListFragment {
            return FolderListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFolderEventListener) {
            mFolderEventListener = context
        }
        else {
            throw RuntimeException("$context must implement OnFolderEventListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_folderlist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showFolderList()
    }

    private fun showFolderList() {
        val folders = getFolderList()
        val adapter = FolderRecyclerAdapter(folders, this)
        folderList.adapter = adapter
//        folderList.addItemDecoration(
//            DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
//        )
    }

    private fun getFolderList(): ArrayList<FolderItem> {
        val folderList = ArrayList<FolderItem>()
        val folderIdSet = mutableSetOf<String>()
        val externalUriString = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        //val dataString = MediaStore.Images.ImageColumns.DATA
        //val idString = MediaStore.Images.Media._ID
        //val idString = MediaStore.Images.Media.BUCKET_ID
        //val displayNameString = MediaStore.Images.Media.DISPLAY_NAME
        //TODO "distinct" can only use until android P (Q will be occurred error)
        //val bucketDisplayNameString = "distinct " + MediaStore.Images.Media.DISPLAY_NAME
//        val projection = arrayOf(
//            //dataString,
//            //idString,
//            //displayNameString,
//            bucketDisplayNameString,
//            //MediaStore.Images.Media.MIME_TYPE
//        )
        val folderProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
        val folderSortOrder = "Random()"

        //val cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, "", null,MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" asc");
        val folderCursor = activity?.contentResolver?.query(externalUriString, folderProjection, null, null, folderSortOrder)

        if (folderCursor != null) {
            folderCursor.moveToFirst()
            do {
                val folderId = folderCursor.getString(folderCursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.BUCKET_ID))
                if (folderIdSet.contains(folderId)) {
                    continue
                } else {
                    folderIdSet.add(folderId)
                }

                val folderName = folderCursor.getString(folderCursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                Log.d("RG", "BUCKET_ID : $folderId, BUCKET_DISPLAY_NAME : $folderName")
                //val id = cursor.getString(cursor.getColumnIndexOrThrow(idString))
                //val contentUri = Uri.withAppendedPath(externalUri, id.toString())
                //val thumbnailString = thumbnailURIFromOriginalURI(contentUri)
                //folderList.add(FolderItem(thumbnailString, name))
                val imageProjection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.Media.DATA,
                )
                //TODO just select latest 1
                //val firstImageOrder = " ?? DESC LIMIT 1"
                val imageCursor = activity?.contentResolver?.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageProjection,
                    MediaStore.Images.ImageColumns.BUCKET_ID + "='" + folderId + "'",
                    null,
                    null/*firstImageOrder*/
                )

                if (imageCursor != null) {
                    imageCursor.moveToFirst()
                    val id = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                    val contentUri = Uri.withAppendedPath(externalUriString, id.toString())
                    folderList.add(FolderItem(folderId, folderName, contentUri))
                    imageCursor.close()
                }
                else {
                    folderList.add(FolderItem(folderId, folderName, defaultThumbnailUri))
                }
            } while (folderCursor.moveToNext())
            folderCursor.close()
        }
        return folderList
    }

//    private fun thumbnailURIFromOriginalURI(selectedImageUri: Uri): Uri {
//        val rowId = selectedImageUri.lastPathSegment?.toLong()
//        return uriToThumbnail("" + rowId)
//    }
//
//    private fun uriToThumbnail(imageId: String): Uri {
//        val projection = arrayOf(MediaStore.Images.Thumbnails.DATA)
//        val thumbnailCursor = activity?.contentResolver?.query(
//            MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
//            projection,
//            MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
//            arrayOf(imageId),
//            null)
//        if (thumbnailCursor == null) {
//            return defaultThumbnailUri
//        } else if (thumbnailCursor.moveToFirst()) {
//            val thumbnailColumnIndex = thumbnailCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA)
//            val thumbnailPath = thumbnailCursor.getString(thumbnailColumnIndex)
//            thumbnailCursor.close()
//            return Uri.parse(thumbnailPath)
//        } else {
//            MediaStore.Images.Thumbnails.getThumbnail(activity?.contentResolver, imageId.toLong(), MediaStore.Images.Thumbnails.MINI_KIND, null)
//            thumbnailCursor.close()
//            return uriToThumbnail(imageId)
//        }
//    }

    override fun onItemClicked(clickedItemId: String) {
        Log.d("RG", "FolderListFragment.onItemClicked() called")
        mFolderEventListener?.onFolderSelected(clickedItemId)
    }
}