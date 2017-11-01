package io.almayce.dev.app24awd.view.location

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

/**
 * Created by almayce on 27.09.17.
 */
class LocationVerificator(val activity: Activity) {
    private val REQUEST_ACCESS_CAMERA = 3
    private val PERMISSIONS_CAMERA = arrayOf(Manifest.permission.CAMERA)

    fun verifyCameraPermissions(): Boolean {
        // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_CAMERA,
                    REQUEST_ACCESS_CAMERA
            )
        } else return true
        return false
    }
}