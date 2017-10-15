package io.almayce.dev.app24awd.view.main

import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.CarList

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityCarControl(val activity: MainActivity) {

    fun showCarPopupMenu(v: View, position: Int): Boolean {

        val popupMenu = PopupMenu(activity, v)
        popupMenu.inflate(R.menu.popupmenu_car)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.menu_select -> activity.pr.selectCar(position)
                    R.id.menu_mileage -> mileageCarDialog(position)
                    R.id.menu_edit -> renameCarDialog(position)
                    R.id.menu_remove -> removeCarDialog(position)
                    else -> return false
                }
                return false
            }
        })
        popupMenu.show()
        return true
    }

    fun mileageCarDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_mileage, null)
        val title = view.findViewById<EditText>(R.id.etTitle)

        val currentMileage = activity.pr.getSelectedCarMileage(position)
        title.setText(currentMileage.toString())

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Укажите актуальный пробег автомобиля ${CarList.get(position).model}:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Редактирование отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which ->
                    if (currentMileage != title.text.toString().toInt())
                        activity.pr.saveSelectedCarMileage(position, title.text.toString().toInt())
                })
        alertDialog.show()
    }

    fun renameCarDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_editcar, null)
        val title = view.findViewById<EditText>(R.id.etTitle)
        title.setText(activity.pr.getSelectedCarModel(position))

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Переименовать:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Редактирование отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which -> activity.pr.renameSelectedCar(position, title.text.toString()) })
        alertDialog.show()
    }


    fun removeCarDialog(position: Int) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить ${activity.pr.getSelectedCarModel(position).toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.pr.removeSelectedCar(position) })
        alertDialog.show()
    }
}