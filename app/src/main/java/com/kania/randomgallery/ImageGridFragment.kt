package com.kania.randomgallery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_imagegrid.*
import kotlinx.android.synthetic.main.fragment_imagegrid.view.*
import kotlinx.android.synthetic.main.fragment_imageview.*
import java.lang.RuntimeException

class ImageGridFragment() : Fragment(),
    ImageRecyclerAdapter.OnItemEventListener {

    interface OnImageEventListener {
        fun onImageSelected(clickedImageUri: Uri)
    }

    //TODO change default image
    private val defaultThumbnailUri = Uri.parse("android.resource://com.kania.randomgallery/drawable/no_thumbnail")

    private var mImageEventListener: ImageGridFragment.OnImageEventListener? = null
    private var mViewPagerEnabled: Boolean = false
    //var mFolderName: String = ""

    companion object {
        fun newInstance(folderId: String): ImageGridFragment {
            val fragment = ImageGridFragment()
            var args = Bundle()
            args.putString("folder_id", folderId)
            fragment.arguments = args;
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ImageGridFragment.OnImageEventListener) {
            mImageEventListener = context
        }
        else {
            throw RuntimeException("$context must implement OnImageEventListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        mFolderName = arguments?.getString("folder_name").toString()
//        Log.d("RG", "mFolderName(${mFolderName})")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_imagegrid, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enablePager(mViewPagerEnabled)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val folderId = arguments?.getString("folder_id").toString()
        Log.d("RG", "mFolderId(${folderId})")
        //TODO enable home button in actionbar

        showImageList(folderId)
    }

    fun onCustomBackPressed() {

    }

    private fun enablePager(enable: Boolean) {
        if (enable) {
            viewPager.visibility = View.VISIBLE
            imageList.visibility = View.GONE
        }
        else {
            viewPager.visibility = View.GONE
            imageList.visibility = View.VISIBLE
        }
        mViewPagerEnabled = enable
    }

    private fun showImageList(folderId: String) {
        val images = getImageList(folderId)
        val listAdapter = ImageRecyclerAdapter(images, this)
        val pagerAdapter = ImagePageAdapter(images)
        imageList.adapter = listAdapter
        viewPager.adapter = pagerAdapter
    }

    private fun getImageList(folderId: String): ArrayList<ImageItem> {
        Log.d("RG", "getImageList started")
        val imageList = ArrayList<ImageItem>()
        val externalUriString = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA
        )
        val imageSortOrder = "Random()"
        val imageCursor = activity?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            MediaStore.Images.ImageColumns.BUCKET_ID + "='" + folderId + "'",
            null,
            imageSortOrder
        )
        Log.d("RG", "getImageList query end")

        if (imageCursor != null) {
            imageCursor.moveToFirst()
            do {
                val name = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
                val id = imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                val contentUri = Uri.withAppendedPath(externalUriString, id.toString())
                imageList.add(ImageItem(name, contentUri))
            } while (imageCursor.moveToNext())
            imageCursor.close()
        }
        Log.d("RG", "getImageList return")
        return imageList
    }

    override fun onItemClicked(/*clickedItemUri: Uri*/position: Int) {
        Log.d("RG", "ImageGridFragment.onItemClicked() called")
        //mImageEventListener?.onImageSelected(clickedItemUri)
        viewPager.setCurrentItem(position, false)
        enablePager(true)
    }
}