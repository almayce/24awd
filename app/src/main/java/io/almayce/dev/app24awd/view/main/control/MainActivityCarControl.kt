package io.almayce.dev.app24awd.view.main.control

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import io.almayce.dev.app24awd.ADB
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.ET
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.view.main.MainActivity

/**
 * Created by almayce on 27.09.17.
 */


class MainActivityCarControl(val activity: MainActivity) {

    fun showCarPopupMenu(v: View, position: Int): Bool {
        val popupMenu = PopupMenu(activity, v)
        with(popupMenu) {
            inflate(R.menu.popupmenu_car)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_select -> activity.pr.selectCar(position)
                    R.id.menu_mileage -> mileageCarDialog(position)
                    R.id.menu_edit -> renameCarDialog(position)
                    R.id.menu_remove -> removeCarDialog(position)
                    else -> return@OnMenuItemClickListener false
                }
                false
            })
            show()
        }
        return true
    }

    @SuppressLint("InflateParams")
    fun mileageCarDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_mileage, null)
        val title = view.findViewById<ET>(R.id.etTitle)

        val currentMileage = activity.pr.getSelectedCarMileage(position)
        title.setText(currentMileage.toString())

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Укажите актуальный пробег автомобиля ${CarList[position].model}:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Редактирование отменено.") })
            setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which ->
                        if (currentMileage != title.text.toString().toInt())
                            activity.pr.saveSelectedCarMileage(position, title.text.toString().toInt())
                    })
            show()
        }
    }

    @SuppressLint("InflateParams")
    private fun renameCarDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_editcar, null)
        val title = view.findViewById<ET>(R.id.etTitle)
        title.setText(activity.pr.getSelectedCarModel(position))

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Переименовать:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Редактирование отменено.") })
            setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which -> activity.pr.renameSelectedCar(position, title.text.toString()) })
            show()
        }
    }


    private fun removeCarDialog(position: Int) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить ${activity.pr.getSelectedCarModel(position).toLowerCase()}?")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Удаление отменено.") })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.pr.removeSelectedCar(position) })
            show()
        }
    }
}