package io.almayce.dev.app24awd.view.location

import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import io.almayce.dev.app24awd.ADB
import io.almayce.dev.app24awd.R

/**
 * Created by almayce on 18.10.17.
 */
class LocationActivityControl(val activity: LocationActivity) {

    fun markerRemoveDialog(marker: Marker) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить маркер?")
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i ->  })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.removeMarker(marker)})
            show()
        }
    }

    fun polylineRemoveDialog(polyline: Polyline) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить маршрут?")
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i ->  })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.removePolyline(polyline) })
            show()
        }
    }
}