package io.almayce.dev.app24awd.view.location

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 27.10.17.
 */
data class LocationMarker(
        val id: Str,
        val title: Str,
        val lat: Double,
        val lng: Double,
        val photoPath: Str) : Serializable