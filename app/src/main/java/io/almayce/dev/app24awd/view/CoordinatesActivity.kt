package io.almayce.dev.app24awd.view

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import kotlinx.android.synthetic.main.app_bar_coordinates.*
import kotlinx.android.synthetic.main.content_coordinates.*

/**
 * Created by almayce on 30.10.17.
 */
class CoordinatesActivity : MvpAppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinates)
        setSupportActionBar(toolbar)
        initActionBar()
        initUI()
        initLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() {
        locationManager = getSystemService(MvpAppCompatActivity.LOCATION_SERVICE) as LocationManager
        with(locationManager) {
            requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, this@CoordinatesActivity)
            requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1f, this@CoordinatesActivity)
            location = getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
    }

    private fun initUI() {
        fabConfirm.setOnClickListener({
            if (path == null) send(false) else send(true)
        })
        btAddPhoto.setOnClickListener({
            getPhoto()
        })
    }

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
        title = "Мои координаты"
    }

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    private fun getPhoto() {
        val intentCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp")
        path = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            var projection = arrayOf<String>(MediaStore.Images.Media.DATA);
//            var cursor = getContentResolver().query(path, projection, null, null, null);
//            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            cursor.moveToFirst()
//            var capturedImageFilePath = cursor.getString(index);
//            cursor.close()
//            pr.transformBitmap(capturedImageFilePath)
//        }
//    }

    private fun send(withPhoto: Bool) {
        val subject = "Мои координаты"
        val text = "${location?.latitude} " +
                "\n${location?.longitude}" +
                "\n\nhttp://maps.google.com/maps?q=+${location?.latitude},+${location?.longitude}" +
                "\n\n${etMessage.text}"

        val emailIntent = Intent(ACTION_SEND)
        with(emailIntent) {
            type = "application/image"
            putExtra(EXTRA_SUBJECT, subject)
            putExtra(EXTRA_TEXT, text)
            if (withPhoto) putExtra(EXTRA_STREAM, path)
        }
        startActivity(createChooser(emailIntent, "Send via:"))
    }

    override fun onLocationChanged(loc: Location?) = let { location = loc }
    override fun onStatusChanged(p0: Str?, p1: Int, p2: Bundle?) = Unit
    override fun onProviderEnabled(p0: Str?) = Unit
    override fun onProviderDisabled(p0: Str?) = Unit
}