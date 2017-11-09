package io.almayce.dev.app24awd.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.Str

/**
 * Created by almayce on 30.10.17.
 */
class MainActivitySOS(val activity: MainActivity) : LocationListener {
    private val locationManager: LocationManager by lazy { activity.getSystemService(MvpAppCompatActivity.LOCATION_SERVICE) as LocationManager }
    var location: Location? = null


    init {
        initLocation()
    }

    @SuppressLint("MissingPermission")
    private fun initLocation() = with(locationManager) {
        requestLocationUpdates(GPS_PROVIDER, 5000, 1f, this@MainActivitySOS)
        requestLocationUpdates(NETWORK_PROVIDER, 5000, 1f, this@MainActivitySOS)
        location = getLastKnownLocation(GPS_PROVIDER)
    }

    fun shareLocation() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        with(sharingIntent) {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Я здесь")
            putExtra(Intent.EXTRA_TEXT, "${location?.latitude} " +
                    "\n${location?.longitude}" +
                    "\n\nhttp://maps.google.com/maps?q=+${location?.latitude},+${location?.longitude}")
        }
        activity.startActivity(Intent.createChooser(sharingIntent, "Send via:"))
    }

    override fun onLocationChanged(loc: Location?) = let { location = loc }
    override fun onStatusChanged(p0: Str?, p1: Int, p2: Bundle?) = Unit
    override fun onProviderEnabled(p0: Str?) = Unit
    override fun onProviderDisabled(p0: Str?) = Unit
}