package io.almayce.dev.app24awd.presenter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.Log
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.util.DirectionConverter
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import io.almayce.dev.app24awd.global.BitmapManager
import io.almayce.dev.app24awd.global.SchedulersTransformer
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.view.location.LocationMarker
import io.almayce.dev.app24awd.view.location.LocationView
import io.almayce.dev.app24awd.view.location.Markers
import java.io.File
import java.io.FileNotFoundException

/**
 * Created by almayce on 18.10.17.
 */
@InjectViewState
class LocationPresenter : MvpPresenter<LocationView>() {
    private var bitmapManager: BitmapManager

    init {
        bitmapManager = BitmapManager()
        bitmapManager.onTransformedBitmapObservable
                .compose(SchedulersTransformer())
                .subscribe({ it ->
                    viewState.addMarker(it)
                })
    }

    fun getMarkerIconFromDrawable(bmp: Bitmap): BitmapDescriptor =
            BitmapDescriptorFactory.fromBitmap(bmp)

    fun getMarkerIconFromPath(path: String): BitmapDescriptor {
        try {
            return BitmapDescriptorFactory.fromBitmap(bitmapManager.getBitmapFromPath(path))
        } catch (e: FileNotFoundException) {
            return getDefaultIcon()
        }
    }

    fun getDefaultIcon(): BitmapDescriptor =
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)

    fun drawRoute(context: Context, from: Location, to: LatLng) {
        GoogleDirection.withServerKey("AIzaSyAJb1roGuh8yFXtEWINJ4chwXeR_rpH-EQ")
                .from(LatLng(from.latitude, from.longitude))
                .to(LatLng(to.latitude, to.longitude))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .execute(object : DirectionCallback {
                    override fun onDirectionSuccess(direction: Direction, rawBody: String) {
                        if (direction.isOK()) {
                            Log.d("RAWW", rawBody)
                            val route = direction.routeList[0]
                            val leg = route.legList[0]
                            val polylineOptions = DirectionConverter.createPolyline(context, leg.directionPoint, 5, Color.parseColor("#2073c8"))
                            viewState.addPolyline(polylineOptions)
                        } else {
                            Log.d("RAWW", rawBody)
                        }
                    }

                    override fun onDirectionFailure(t: Throwable) {
                        Log.d("RAWW", "!!!")
                        // Do something
                    }
                })
    }

    fun transformBitmap(path: String) {
        bitmapManager.transformBitmap(path)
    }

    fun addMarker(marker: Marker?, photoPath: String) {
        Markers.add(LocationMarker(
                marker!!.id,
                marker.title,
                marker.position.latitude,
                marker.position.longitude,
                photoPath))
        serialize()
    }

    fun removeMarker(marker: Marker?) {
        Markers.forEach { it -> if (it.id == marker?.id) Markers.remove(it) }
        serialize()
    }

    fun addAllMarkers() {
        for (m in Markers)
            viewState.addMarker(m)
    }

    fun serialize() {
        Serializer.serialize(Serializer.FileName.MARKERS)
    }

    fun deserialize() {
        if (Markers.isEmpty())
            Serializer.deserialize(Serializer.FileName.MARKERS)
    }

}