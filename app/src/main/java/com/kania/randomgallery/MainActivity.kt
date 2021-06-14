package com.kania.randomgallery

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity(),
    FolderListFragment.OnFolderEventListener,
    ImageGridFragment.OnImageEventListener {

    private val REQUEST_PERMISSION_STORAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            setInitialFragmentWithPermission()
        }
        else {
            //TODO restore
        }
    }

    private fun setInitialFragmentWithPermission() {
        if (hasStoragePermission()) {
            setInitialFragment()
        }
        else {
            requestStoragePermissionForResult()
        }
    }

    private fun hasStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        val storagePermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val permissionState = checkSelfPermission(storagePermission)
        return permissionState != PackageManager.PERMISSION_DENIED
    }

    private fun requestStoragePermissionForResult() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_STORAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (hasStoragePermission()) {
                setInitialFragment()
            }
            else {
                finish()
            }
        }
    }

    private fun setInitialFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, FolderListFragment.newInstance())
            .commit()
    }

    override fun onFolderSelected(clickedFolderId: String) {
        Log.d("RG", "onFolderSelected() called")
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ImageGridFragment.newInstance(clickedFolderId))
            .addToBackStack(null)
            .commit()
    }

    override fun onImageSelected(clickedImageUri: Uri) {
        Log.d("RG", "onImageSelected() called")
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainer, ImageViewFragment.newInstance(clickedImageUri))
//            .addToBackStack(null)
//            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
