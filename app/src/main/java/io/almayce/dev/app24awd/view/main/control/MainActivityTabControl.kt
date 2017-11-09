package io.almayce.dev.app24awd.view.main.control

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.view.main.MainActivity

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityTabControl(val activity: MainActivity) {

    fun showTabPopupMenu(v: View, position: Int): Bool {

        if (v.contentDescription == "add") {
            return true
        } else {
            val popupMenu = PopupMenu(activity, v)
            with(popupMenu) {
                inflate(R.menu.popupmenu)
                setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_edit -> editTabDialog(position)
                        R.id.menu_remove -> removeTabDialog(position)
                        else -> return@OnMenuItemClickListener false
                    }
                    false
                })
                show()
            }
        }
        return true
    }

    @SuppressLint("InflateParams")
    fun addTabDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addtab, null)
        val title = view.findViewById<ET>(R.id.etTitle)
        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Добавить пункт:")
            setButton(BUTTON_POSITIVE, "Добавить",
                    { dialog, which -> addTab(title.text.toString(), R.drawable.ic_other) })
            show()
        }

    }

    @SuppressLint("InflateParams")
    private fun editTabDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addtab, null)
        val title = view.findViewById<ET>(R.id.etTitle)
        title.setText(activity.pr.getTabTitle(position))

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Редактировать пункт:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Редактирование отменено.") })
           setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which -> editTab(position, title.text.toString()) })
            show()
        }
    }

    private fun removeTabDialog(position: Int) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить пункт ${activity.pr.getTabTitle(position).toLowerCase()}?")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Удаление отменено.") })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.pr.removeTab(position) })
            show()
        }

    }

    private fun addTab(title: Str, icon: Int) = activity.pr.addTab(title, icon)
    private fun editTab(position: Int, title: Str) = activity.pr.editTab(position, title)
}