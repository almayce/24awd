package io.almayce.dev.app24awd.view.location

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.R
import kotlinx.android.synthetic.main.content_location.*
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.app_bar_location.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.maps.model.*
import io.almayce.dev.app24awd.presenter.LocationPresenter
import android.view.Menu
import android.view.MenuItem


/**
 * Created by almayce on 17.10.17.
 */
class LocationActivity : MvpAppCompatActivity(),
        LocationView,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMapLongClickListener {

    @InjectPresenter
    lateinit var pr: LocationPresenter
    private var control: LocationActivityControl? = null
    private var locationVerificator: LocationVerificator? = null
    private var maps: GoogleMap? = null
    private var location: Location? = null
    private var keeper: LocationMapsKeeper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        setSupportActionBar(toolbar)
        pr.deserialize()

        control = LocationActivityControl(this)
        keeper = LocationMapsKeeper(this)
        mvMaps.onCreate(savedInstanceState)
        mvMaps.getMapAsync(this)

        initMaps()
        initActionBar()
        initUI()

        locationVerificator?.verifyCameraPermissions()
    }

    private fun initUI() {
        fabCurrent.setOnClickListener({
            if (location != null) {
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                        LatLng(location!!.latitude,
                                location!!.longitude), 12f)
                maps?.animateCamera(cameraUpdate)
            }
        })

        btAddPhoto.setOnClickListener({ view ->
            if (location != null)
                getMarkerPhoto()
        })
    }

    private fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Локация"
    }

    private fun initMaps() {
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }

    override fun onMapReady(m: GoogleMap?) {
        maps = m
        maps?.getUiSettings()?.setMyLocationButtonEnabled(false)
        maps?.setMyLocationEnabled(true)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                LatLng(55.7, 37.6), 6f)
        maps?.moveCamera(cameraUpdate)
        maps?.setOnMapLongClickListener(this)
        maps?.setOnMarkerClickListener { marker ->
            control?.markerRemoveDialog(marker)
            true
        }
        maps?.setOnPolylineClickListener { polyline ->
            control?.polylineRemoveDialog(polyline)
            true
        }
        requestLocation()
        pr.addAllMarkers()
    }

    fun removeMarker(marker: Marker) {
        pr.removeMarker(marker)
        marker.remove()
    }

    fun removePolyline(polyline: Polyline) {
        polyline.remove()
    }

    override fun onMapLongClick(pos: LatLng) {
        try {
            pr.drawRoute(this, location!!, pos)
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
        val markerOptions = MarkerOptions()
                .position(pos)
                .title("Point")
                .icon(pr.getDefaultIcon())

        val marker: Marker? = maps?.addMarker(markerOptions)
        pr.addMarker(marker, "")
    }

    override fun addMarker(bmp: Bitmap) {
        val markerIcon = pr.getMarkerIconFromDrawable(bmp)
        val latlng = LatLng(location!!.latitude, location!!.longitude)
        val markerOptions = MarkerOptions()
                .position(latlng)
                .title("Point")
                .icon(markerIcon)

        val marker: Marker? = maps?.addMarker(markerOptions)
        pr.addMarker(marker, path.toString())
    }

    override fun addMarker(marker: LocationMarker) {
        var markerIcon = pr.getDefaultIcon()
        if (marker.photoPath != "")
            markerIcon = pr.getMarkerIconFromPath(marker.photoPath)

        val latlng = LatLng(marker.lat, marker.lng)
        val markerOptions = MarkerOptions()
                .position(latlng)
                .title(marker.title)
                .icon(markerIcon)

        maps?.addMarker(markerOptions)
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    fun getMarkerPhoto() {
        var intentCapture = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "temp")
        path = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO);
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            var projection = arrayOf<String>(MediaStore.Images.Media.DATA);
            var cursor = getContentResolver().query(path, projection, null, null, null);
            var index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            var capturedImageFilePath = cursor.getString(index);
            cursor.close()
path = Uri.parse(capturedImageFilePath)
            pr.transformBitmap(capturedImageFilePath)
        }
    }

    override fun addPolyline(options: PolylineOptions) {
        options.clickable(true)
        maps?.addPolyline(options)
    }

    private fun requestLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, this)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1f, this);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    override fun onLocationChanged(loc: Location) {
        location = loc
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> share()
            else -> onBackPressed()
        }
        return true
    }

    fun share() {
        val sharingIntent = Intent(android.content.Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Я здесь")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "${location?.latitude} " +
                "\n${location?.longitude}" +
                "\n\nhttp://maps.google.com/maps?q=+${location?.latitude},+${location?.longitude}")
        startActivity(Intent.createChooser(sharingIntent, "Send via:"))
    }

    public override fun onResume() {
        super.onResume()
        mvMaps.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mvMaps.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mvMaps.onLowMemory()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}