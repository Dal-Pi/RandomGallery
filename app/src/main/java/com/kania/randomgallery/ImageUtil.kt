package com.kania.randomgallery

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun getThumbnail(context: Context, imageUri: Uri) : Bitmap {
    return context.contentResolver.loadThumbnail(imageUri, Size(640,480), null)
}

//TODO duplicated with imagelist
fun thumbnailURIFromOriginalURI(context: Context, selectedImageUri: Uri): Uri {
    val rowId = selectedImageUri.lastPathSegment?.toLong()
    return uriToThumbnail(context, "" + rowId)
}
private fun uriToThumbnail(context: Context, imageId: String): Uri {
    //TODO change for android Q
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