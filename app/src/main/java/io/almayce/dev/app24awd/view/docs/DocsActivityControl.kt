package io.almayce.dev.app24awd.view.docs

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import android.view.LayoutInflater
import android.widget.DatePicker
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.docs.Doc
import java.util.*
import java.util.Calendar.*

/**
 * Created by almayce on 30.10.17.
 */
class DocsActivityControl(val activity: DocsActivity) {

    @SuppressLint("InflateParams")
    fun addDocDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_adddoc, null)

        val title = view.findViewById<ET>(R.id.etTitle)
        val dpReplace = view.findViewById<DP>(R.id.dpReplace)

        val dpCalendar = getInstance()
        with(dpCalendar) {
            timeZone = TimeZone.getTimeZone("GMT")
            dpReplace.init(
                    get(YEAR),
                    get(MONTH),
                    get(DAY_OF_MONTH),
                    null)
        }

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Добавить документ:")
            setContentView(R.layout.dialog_cars)
            setButton(BUTTON_NEGATIVE, "Отмена", { _, _ -> })
            setButton(BUTTON_POSITIVE, "Добавить", { _, _ ->

                val cal = getInstance()
                with(cal) {
                    timeZone = TimeZone.getTimeZone("GMT")
                    with(dpReplace) {
                        set(DAY_OF_MONTH, dayOfMonth)
                        set(MONTH, month)
                        set(YEAR, year)
                    }
                }

                activity.pr.addDoc(Doc(
                        title.text.toString(),
                        cal.timeInMillis,
                        ""))
            })
            show()
        }
    }

    @SuppressLint("InflateParams")
    fun editDocDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_adddoc, null)

        val title = view.findViewById<ET>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)

        val currentDoc = activity.pr.getCurrentDoc(position)
        title.setText(currentDoc.title)

        val dpCalendar = getInstance()
        with(dpCalendar) {
            timeZone = TimeZone.getTimeZone("GMT")
            timeInMillis = currentDoc.endDate

            dpReplace.init(
                    get(YEAR),
                    get(MONTH),
                    get(DAY_OF_MONTH),
                    null)
        }


        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Редактировать документ:")
            setContentView(R.layout.dialog_cars)
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
            setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which ->

                        val cal = getInstance()
                        with(cal) {
                            timeZone = TimeZone.getTimeZone("GMT")
                            with(dpReplace) {
                                set(DAY_OF_MONTH, dayOfMonth)
                                set(MONTH, month)
                                set(YEAR, year)
                            }
                        }

                        activity.pr.editDoc(position, Doc(
                                title.text.toString(),
                                cal.timeInMillis,
                                currentDoc.photoPath))
                    })
            show()
        }

    }

    fun removeDocDialog(position: Int) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить документ ${activity.pr.getCurrentDoc(position).title.toLowerCase()}?")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Удаление отменено.") })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.pr.removeDoc(position) })
            show()
        }
    }

    fun dateFormat(replaceDate: Long): Str {
        val sdf = SDF("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(replaceDate)
    }
}