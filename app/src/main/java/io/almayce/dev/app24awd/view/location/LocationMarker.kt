package io.almayce.dev.app24awd.view.location

import java.io.Serializable

/**
 * Created by almayce on 27.10.17.
 */
data class LocationMarker(
        val id: String,
        val title: String,
        val lat: Double,
        val lng: Double,
        val photoPath: String) : Serializable {
}