package io.almayce.dev.app24awd.view

import android.content.ContentValues
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.R
import kotlinx.android.synthetic.main.app_bar_coordinates.*
import kotlinx.android.synthetic.main.content_coordinates.*

/**
 * Created by almayce on 30.10.17.
 */
class CoordinatesActivity : MvpAppCompatActivity(), LocationListener {

    var locationManager: LocationManager? = null
    var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coordinates)
        setSupportActionBar(toolbar)
        initActionBar()
        initUI()
        initLocation()
    }

    private fun initLocation() {
        locationManager = getSystemService(MvpAppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, this)
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1f, this)
        location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    private fun initUI() {
        fabConfirm.setOnClickListener({
            if (path == null) send(false) else send(true)
        })
        btAddPhoto.setOnClickListener({
            getPhoto()
        })
    }

    fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Мои координаты"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    fun getPhoto() {
        var intentCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp")
        path = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO);
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

    fun send(withPhoto: Boolean) {
        val subject = "Мои координаты"
        val text = "${location?.latitude} " +
                "\n${location?.longitude}" +
                "\n\nhttp://maps.google.com/maps?q=+${location?.latitude},+${location?.longitude}" +
                "\n\n${etMessage.text.toString()}"

        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "application/image"
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        if (withPhoto) emailIntent.putExtra(Intent.EXTRA_STREAM, path)
        startActivity(Intent.createChooser(emailIntent, "Send via:"))
    }

    override fun onLocationChanged(loc: Location?) {
        location = loc
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }
}