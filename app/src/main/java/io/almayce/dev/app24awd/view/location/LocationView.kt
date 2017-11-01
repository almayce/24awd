package io.almayce.dev.app24awd.view.location

import android.graphics.Bitmap
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.PolylineOptions

/**
 * Created by almayce on 18.10.17.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface LocationView : MvpView {
    fun addPolyline(options: PolylineOptions)
    fun addMarker(bmp: Bitmap)
    fun addMarker(marker: LocationMarker)
}