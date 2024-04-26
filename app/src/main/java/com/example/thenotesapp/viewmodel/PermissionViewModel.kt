package com.example.thenotesapp.viewmodel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel

class PermissionViewModel(private var application: Application) : AndroidViewModel(application) {

    fun checkImagePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                application.baseContext, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ActivityCompat.checkSelfPermission(
                application.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                application.baseContext, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun askForImagePermission(fragment: Fragment) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            fragment.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                ), STORAGE_PERM_REQ_CODE_ON_13
            )
        } else {
            fragment.requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ), STORAGE_PERM_REQ_CODE_BELOW_13
            )
        }
    }


    companion object {
        val STORAGE_PERM_REQ_CODE_ON_13 = 100
        val STORAGE_PERM_REQ_CODE_BELOW_13 = 101
    }
}