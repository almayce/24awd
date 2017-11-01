package io.almayce.dev.app24awd.view.main

import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity

/**
 * Created by almayce on 30.10.17.
 */
class MainActivitySOS(val activity: MainActivity) : LocationListener {
    var locationManager: LocationManager? = null
    var location: Location? = null

    init {
        locationManager = activity.getSystemService(MvpAppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1f, this)
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1f, this)
        location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    fun shareLocation() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Я здесь")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "${location?.latitude} " +
                "\n${location?.longitude}" +
                "\n\nhttp://maps.google.com/maps?q=+${location?.latitude},+${location?.longitude}")
        activity.startActivity(Intent.createChooser(sharingIntent, "Send via:"))
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