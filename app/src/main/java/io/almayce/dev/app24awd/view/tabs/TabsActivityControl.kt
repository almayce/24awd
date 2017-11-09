package io.almayce.dev.app24awd.view.tabs

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.PopupMenu
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import java.util.*
import java.util.Calendar.*

/**
 * Created by almayce on 30.10.17.
 */
class TabsActivityControl(val activity: TabsActivity) {

    @SuppressLint("InflateParams")
    fun addTabParamDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addparam, null)

        val title = view.findViewById<ET>(R.id.etTitle)
        val dpReplace = view.findViewById<DP>(R.id.dpReplace)
        val cost = view.findViewById<ET>(R.id.etCost)
        val limit = view.findViewById<ET>(R.id.etLimit)

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
            setTitle("Добавить учет:")
            setContentView(R.layout.dialog_cars)
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
            setButton(BUTTON_POSITIVE, "Добавить",
                    { dialog, which ->

                        val cal = getInstance()
                        with(cal) {
                            with(dpReplace) {
                                timeZone = TimeZone.getTimeZone("UTC")
                                set(DAY_OF_MONTH, dayOfMonth)
                                set(MONTH, month)
                                set(YEAR, year)
                            }
                        }


                        val c = try {
                            cost.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }

                        val l = try {
                            limit.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }

                        activity.pr.addTabParam(CarTabParam(
                                title.text.toString(), "",
                                cal.timeInMillis,
                                CarList[SelectedCar.index].replaceMileage,
                                c, l))

                    })
            show()
        }
    }

    @SuppressLint("InflateParams")
    private fun editTabParamDialog(position: Int) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_addparam, null)

        val title = view.findViewById<ET>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)
        val cost = view.findViewById<ET>(R.id.etCost)
        val limit = view.findViewById<ET>(R.id.etLimit)

        val currentParam = activity.pr.getCurrentParam(position)
        title.setText(currentParam.title)

        val dpCalendar = getInstance()

        with(dpCalendar) {
            timeZone = TimeZone.getTimeZone("GMT")
            if (currentParam.replaceDate > 0)
                timeInMillis = currentParam.replaceDate
            dpReplace.init(
                    get(YEAR),
                    get(MONTH),
                    get(DAY_OF_MONTH),
                    null)
        }


        cost.setText(currentParam.replaceCost.toString())
        limit.setText(currentParam.replaceLimit.toString())

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(view)
        with(builder.create()) {
            setTitle("Редактировать учет:")
            setContentView(R.layout.dialog_cars)
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
            setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which ->
                        val cal = getInstance()
                        with(cal) {
                            with(dpReplace) {
                                timeZone = TimeZone.getTimeZone("GMT")
                                set(DAY_OF_MONTH, dayOfMonth)
                                set(MONTH, month)
                                set(YEAR, year)
                            }
                        }

                        val car = CarList[SelectedCar.index]

                        val replaceMileage = if (car.replaceMileage == 0)
                            car.createMileage
                        else car.replaceMileage

                        val c = try {
                            cost.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }

                        val l = try {
                            limit.text.toString().toInt()
                        } catch (e: NumberFormatException) {
                            0
                        }

                        activity.pr.editTabParam(position, CarTabParam(
                                title.text.toString(),
                                currentParam.photoPath,
                                cal.timeInMillis,
                                replaceMileage,
                                c, l))
                    })
            show()
        }
    }

    private fun removeTabParamDialog(position: Int) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить учет ${activity.pr.getCurrentParam(position).title.toLowerCase()}?")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Удаление отменено.") })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.pr.removeTabParam(position) })
            show()
        }
    }

    fun showPopupMenu(context: Context, v: View, position: Int): Bool {

        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popupmenu_param)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            with(activity) {
                when (item.itemId) {
                    R.id.menu_edit -> editTabParamDialog(position)
                    R.id.menu_photo -> getPhoto(position)
                    R.id.menu_remind -> showToast(pr.setAlarm(context, position))
                    R.id.menu_remove -> removeTabParamDialog(position)
                    else -> return@OnMenuItemClickListener false
                }
            }
            false
        })
        popupMenu.show()
        return true
    }


    fun dateFormat(replaceDate: Long): Str {
        val sdf = SDF("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(replaceDate)
    }
}