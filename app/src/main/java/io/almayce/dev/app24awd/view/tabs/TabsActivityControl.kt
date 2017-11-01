package io.almayce.dev.app24awd.view.tabs

import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.DatePicker
import android.widget.EditText
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by almayce on 30.10.17.
 */
class TabsActivityControl(val activity: TabsActivity) {

    fun addTabParamDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addparam, null)

        val title = view.findViewById<EditText>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)
        val cost = view.findViewById<EditText>(R.id.etCost)
        val limit = view.findViewById<EditText>(R.id.etLimit)

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
        alertDialog.setTitle("Добавить учет:")
        alertDialog.setContentView(R.layout.dialog_cars)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить",
                { dialog, which ->

                    val cal = Calendar.getInstance()
                    cal.timeZone = TimeZone.getTimeZone("UTC")
                    cal.set(Calendar.DAY_OF_MONTH, dpReplace.getDayOfMonth())
                    cal.set(Calendar.MONTH, dpReplace.getMonth())
                    cal.set(Calendar.YEAR, dpReplace.getYear())

//                    var deltaDays = TimeUnit.DAYS.convert(System.currentTimeMillis() - cal.timeInMillis, TimeUnit.MILLISECONDS).toInt()
//                    if (deltaDays < 0) deltaDays = 0
//                    var pastMileage = CarList.get(SelectedCar.index).replaceMileage - (deltaDays * CarList.get(SelectedCar.index).dayMileage)
//                    if (pastMileage < 0) pastMileage = 0
                    var c = 0
                    try {
                        c = cost.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }

                    var l = 0
                    try {
                        l = limit.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }

                    activity.pr.addTabParam(CarTabParam(
                            title.text.toString(), "",
                            cal.timeInMillis,
                            CarList.get(SelectedCar.index).replaceMileage,
                            c, l))

                })
        alertDialog.show()
    }

    fun editTabParamDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addparam, null)

        val title = view.findViewById<EditText>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)
        val cost = view.findViewById<EditText>(R.id.etCost)
        val limit = view.findViewById<EditText>(R.id.etLimit)

        val currentParam = activity.pr.getCurrentParam(position)
        title.setText(currentParam.title)

        val dpCalendar = Calendar.getInstance()
        dpCalendar.timeZone = TimeZone.getTimeZone("GMT")
        if (currentParam.replaceDate > 0)
            dpCalendar.timeInMillis = currentParam.replaceDate

        dpReplace.init(
                dpCalendar.get(Calendar.YEAR),
                dpCalendar.get(Calendar.MONTH),
                dpCalendar.get(Calendar.DAY_OF_MONTH),
                null)

        cost.setText(currentParam.replaceCost.toString())
        limit.setText(currentParam.replaceLimit.toString())

        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.setTitle("Редактировать учет:")
        alertDialog.setContentView(R.layout.dialog_cars)
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which ->

                    val cal = Calendar.getInstance()
                    cal.timeZone = TimeZone.getTimeZone("GMT")
                    cal.set(Calendar.DAY_OF_MONTH, dpReplace.getDayOfMonth())
                    cal.set(Calendar.MONTH, dpReplace.getMonth())
                    cal.set(Calendar.YEAR, dpReplace.getYear())

                    val car = CarList.get(SelectedCar.index)

                    var replaceMileage = 0
                    if (car.replaceMileage.equals(0))
                        replaceMileage = car.createMileage
                    else replaceMileage = car.replaceMileage

                    var c = 0
                    try {
                        c = cost.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }

                    var l = 0
                    try {
                        l = limit.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                    }


                    activity.pr.editTabParam(position, CarTabParam(
                            title.text.toString(),
                            currentParam.photoPath,
                            cal.timeInMillis,
                            replaceMileage,
                            c, l))
                })
        alertDialog.show()
    }

    fun removeTabParamDialog(position: Int) {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить учет ${activity.pr.getCurrentParam(position).title.toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> activity.showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> activity.pr.removeTabParam(position) })
        alertDialog.show()
    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(replaceDate)
    }
}