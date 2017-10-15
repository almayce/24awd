package io.almayce.dev.app24awd.view.main

import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import io.almayce.dev.app24awd.R

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityTabControl(val activity: MainActivity) {

    fun showTabPopupMenu(v: View, position: Int): Boolean {

        if (v.contentDescription == "add") {
            return true
        } else {
            val popupMenu = PopupMenu(activity, v)
            popupMenu.inflate(R.menu.popupmenu)
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.menu_edit -> editTabDialog(position)
                        R.id.menu_remove -> removeTabDialog(position)
                        else -> return false
                    }
                    return false
                }
            })
            popupMenu.show()
        }
        return true
    }

    fun addTabDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addtab, null)
        val title = view.findViewById<EditText>(R.id.etTitle)

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Добавить пункт:")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить",
                { dialog, which -> addTab(title.text.toString(), R.drawable.ic_other) })
        alertDialog.show()
    }

    fun editTabDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addtab, null)
        val title = view.findViewById<EditText>(R.id.etTitle)
        title.setText(activity.pr.getTabTitle(position))

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Редактировать пункт:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Редактирование отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which -> editTab(position, title.text.toString()) })
        alertDialog.show()
    }


    fun removeTabDialog(position: Int) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить пункт ${activity.pr.getTabTitle(position).toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.pr.removeTab(position) })
        alertDialog.show()
    }

    private fun addTab(title: String, icon: Int) {
        activity.pr.addTab(title, icon)
    }

    private fun editTab(position: Int, title: String) {
        activity.pr.editTab(position, title)
    }
}