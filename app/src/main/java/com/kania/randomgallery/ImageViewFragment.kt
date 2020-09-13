//package com.kania.randomgallery
//
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import kotlinx.android.synthetic.main.fragment_imagegrid.*
//import kotlinx.android.synthetic.main.fragment_imageview.*
//
//class ImageViewFragment : Fragment() {
//
//    companion object {
//        fun newInstance(imageUri: Uri): ImageViewFragment {
//            val fragment = ImageViewFragment()
//            var args = Bundle()
//            args.putParcelable("image_uri", imageUri)
//            fragment.arguments = args;
//            return fragment
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_imageview, container, false)
//        return view
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val imageUrl = arguments?.getParcelable<Uri>("image_uri")
//        Log.d("RG", "imageUrl(${imageUrl.toString()})")
//        //TODO enable home button in actionbar
//
//        image.setImageURI(imageUrl)
//    }
//
//    private fun showImageList(folderName: String) {
//        val images = getImageList(folderName)
//
//        val randomImageList = RandomUtil.getRandomList(images)
//
//        val adapter = ImageRecyclerAdapter(randomImageList, this)
//        imageList.adapter = adapter
//    }
//
//    private fun getImageList(folderName: String): ArrayList<ImageItem> {
//        val imageList = ArrayList<ImageItem>()
//        val externalUriString = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val imageProjection = arrayOf(
//            MediaStore.Images.Media._ID,
//            MediaStore.Images.Media.DISPLAY_NAME,
//            MediaStore.Images.Media.DATA
//        )
//        val imageCursor = activity?.contentResolver?.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            imageProjection,
//            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + "='" + folderName + "'",
//            null,
//            null
//        )
//
//        if (imageCursor != null) {
//            imageCursor.moveToFirst()
//            do {
//                val name = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
//                val id = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                val contentUri = Uri.withAppendedPath(externalUriString, id.toString())
//                val thumbnailUri = thumbnailURIFromOriginalURI(contentUri)
//                imageList.add(ImageItem(name, thumbnailUri, contentUri))
//            } while (imageCursor.moveToNext())
//            imageCursor.close()
//        }
//        return imageList
//    }
//
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
//}