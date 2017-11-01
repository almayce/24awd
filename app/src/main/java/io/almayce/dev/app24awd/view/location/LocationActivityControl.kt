package io.almayce.dev.app24awd.view.location

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import io.almayce.dev.app24awd.R

/**
 * Created by almayce on 18.10.17.
 */
class LocationActivityControl(val activity: LocationActivity) {

    fun markerRemoveDialog(marker: Marker) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить маркер?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", {dialogInterface, i ->  })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.removeMarker(marker)})
        alertDialog.show()
    }

    fun polylineRemoveDialog(polyline: Polyline) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить маршрут?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", {dialogInterface, i ->  })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.removePolyline(polyline) })
        alertDialog.show()
    }
}