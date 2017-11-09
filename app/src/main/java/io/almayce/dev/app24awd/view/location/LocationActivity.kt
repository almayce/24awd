package io.almayce.dev.app24awd.view.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.*
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.presenter.LocationPresenter
import kotlinx.android.synthetic.main.app_bar_location.*
import kotlinx.android.synthetic.main.content_location.*


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
    lateinit var maps: GoogleMap

    private var control: LocationActivityControl? = null
    private var locationVerificator: LocationVerificator? = null
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        setSupportActionBar(toolbar)
        pr.deserialize()

        control = LocationActivityControl(this)
        with(mvMaps) {
            onCreate(savedInstanceState)
            getMapAsync(this@LocationActivity)
        }


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
                maps.animateCamera(cameraUpdate)
            }
        })

        btAddPhoto.setOnClickListener({ view ->
            if (location != null)
                getMarkerPhoto()
        })
    }

    private fun initActionBar() = with(supportActionBar!!) {
        setDisplayHomeAsUpEnabled(true)
        setDisplayShowHomeEnabled(true)
        title = "Локация"
    }

    private fun initMaps() = try {
        MapsInitializer.initialize(this)
    } catch (e: GooglePlayServicesNotAvailableException) {
        e.printStackTrace()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(m: GoogleMap) {
        maps = m
        with(maps) {
            uiSettings.isMyLocationButtonEnabled = false
            isMyLocationEnabled = true
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    LatLng(55.7, 37.6), 6f)
            moveCamera(cameraUpdate)
            setOnMapLongClickListener(this@LocationActivity)
            setOnMarkerClickListener { marker ->
                control?.markerRemoveDialog(marker)
                true
            }
            setOnPolylineClickListener { polyline ->
                control?.polylineRemoveDialog(polyline)
                true
            }
        }
        requestLocation()
        pr.addAllMarkers()
    }

    fun removeMarker(marker: Marker) {
        pr.removeMarker(marker)
        marker.remove()
    }

    fun removePolyline(polyline: Polyline) = polyline.remove()

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

        val marker: Marker? = maps.addMarker(markerOptions)
        pr.addMarker(marker, "")
    }

    override fun addMarker(bmp: Bitmap) {
        val markerIcon = pr.getMarkerIconFromDrawable(bmp)
        val latlng = LatLng(location!!.latitude, location!!.longitude)
        val markerOptions = MarkerOptions()
                .position(latlng)
                .title("Point")
                .icon(markerIcon)

        val marker: Marker? = maps.addMarker(markerOptions)
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

        maps.addMarker(markerOptions)
    }

    private val CAMERA_PHOTO = 111
    private var path: Uri? = null

    private fun getMarkerPhoto() {
        val intentCapture = Intent(ACTION_IMAGE_CAPTURE)
        val contentValues = ContentValues()
        contentValues.put(Images.Media.TITLE, "temp")
        path = contentResolver.insert(Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
        intentCapture.putExtra(EXTRA_OUTPUT,
                path)

        startActivityForResult(intentCapture, CAMERA_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val projection = arrayOf(Images.Media.DATA)
            val cursor = contentResolver.query(path, projection, null, null, null)
            with(cursor) {
                val index = getColumnIndexOrThrow(Images.Media.DATA)
                moveToFirst()
                close()
                path = Uri.parse(getString(index))
                pr.transformBitmap(getString(index))
            }

        }
    }

    override fun addPolyline(options: PolylineOptions) {
        options.clickable(true)
        maps.addPolyline(options)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        with(locationManager) {
            requestLocationUpdates(GPS_PROVIDER, 5000, 1f, this@LocationActivity)
            requestLocationUpdates(NETWORK_PROVIDER, 5000, 1f, this@LocationActivity)
            location = getLastKnownLocation(GPS_PROVIDER)
        }
    }

    override fun onLocationChanged(loc: Location) {
        location = loc
    }

    override fun onStatusChanged(p0: Str?, p1: Int, p2: Bundle?) = Unit
    override fun onProviderEnabled(p0: Str?) = Unit
    override fun onProviderDisabled(p0: Str?) = Unit

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Bool {
        menuInflater.inflate(R.menu.location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Bool {
        when (item.itemId) {
            R.id.action_share -> share()
            else -> onBackPressed()
        }
        return true
    }

    private fun share() {
        startActivity(createChooser(
                with(Intent(ACTION_SEND)) {
                    with(location!!) {
                        type = "text/plain"
                        putExtra(EXTRA_SUBJECT, "Я здесь")
                        putExtra(EXTRA_TEXT,
                                "$latitude \n$longitude" +
                                "\n\nhttp://maps.google.com/maps?q=+" +
                                "$latitude,+$longitude")
                    }
                }, "Send via:"))
    }

    public override fun onResume() {
        super.onResume()
        mvMaps.onResume()
    }

    public override fun onPause() = super.onPause()

    public override fun onDestroy() {
        super.onDestroy()
        mvMaps.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mvMaps.onLowMemory()
    }
}