package io.almayce.dev.app24awd.view.docs

import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.EditText
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.docs.Doc
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by almayce on 30.10.17.
 */
class DocsActivityControl(val activity: DocsActivity) {

    fun addDocDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_adddoc, null)

        val title = view.findViewById<EditText>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)

        val dpCalendar = Calendar.getInstance()
        dpCalendar.timeZone = TimeZone.getTimeZone("GMT")

        dpReplace.init(
                dpCalendar.get(Calendar.YEAR),
                dpCalendar.get(Calendar.MONTH),
                dpCalendar.get(Calendar.DAY_OF_MONTH),
                null)

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Добавить документ:")
        alertDialog.setContentView(R.layout.dialog_cars)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить",
                { dialog, which ->

                    val cal = Calendar.getInstance()
                    cal.timeZone = TimeZone.getTimeZone("UTC")
                    cal.set(Calendar.DAY_OF_MONTH, dpReplace.getDayOfMonth())
                    cal.set(Calendar.MONTH, dpReplace.getMonth())
                    cal.set(Calendar.YEAR, dpReplace.getYear())

                    activity.pr.addDoc(Doc(
                            title.text.toString(),
                            cal.timeInMillis,
                            ""))

                })
        alertDialog.show()
    }

    fun editDocDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_adddoc, null)

        val title = view.findViewById<EditText>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)

        val currentDoc = activity.pr.getCurrentDoc(position)
        title.setText(currentDoc.title)

        val dpCalendar = Calendar.getInstance()
        dpCalendar.timeZone = TimeZone.getTimeZone("GMT")
            dpCalendar.timeInMillis = currentDoc.endDate

        dpReplace.init(
                dpCalendar.get(Calendar.YEAR),
                dpCalendar.get(Calendar.MONTH),
                dpCalendar.get(Calendar.DAY_OF_MONTH),
                null)

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Редактировать документ:")
        alertDialog.setContentView(R.layout.dialog_cars)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which ->

                    val cal = Calendar.getInstance()
                    cal.timeZone = TimeZone.getTimeZone("GMT")
                    cal.set(Calendar.DAY_OF_MONTH, dpReplace.getDayOfMonth())
                    cal.set(Calendar.MONTH, dpReplace.getMonth())
                    cal.set(Calendar.YEAR, dpReplace.getYear())

                    activity.pr.editDoc(position, Doc(
                            title.text.toString(),
                            cal.timeInMillis,
                            currentDoc.photoPath))
                })
        alertDialog.show()
    }

    fun removeDocDialog(position: Int) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить документ ${activity.pr.getCurrentDoc(position).title.toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.pr.removeDoc(position) })
        alertDialog.show()
    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(replaceDate)
    }
}