package io.almayce.dev.app24awd.view.costs

import android.annotation.SuppressLint
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.PopupMenu
import io.almayce.dev.app24awd.*
import io.almayce.dev.app24awd.model.cars.*
import kotlinx.android.synthetic.main.item_costs.*
import java.util.*
import java.util.Calendar.*

/**
 * Created by almayce on 08.11.17.
 */
class CostsActivityControl(val activity: CostsActivity) {

    @SuppressLint("InflateParams")
    fun dialogFilterCostByMileage() {
        val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_filtercostsbymileage, null)
        val etMileage = layout.findViewById<ET>(R.id.etMileage)
        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(layout)

        with(builder.create()) {
            setTitle("Показывать с:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> })
            setButton(BUTTON_POSITIVE, "Применить",
                    { dialog, which -> activity.getFilteredByMileageCostsRecyclerViewAdapter(etMileage.text.toString().toInt())
                    })
            show()
        }
    }

    @SuppressLint("InflateParams")
    fun dialogFilterCost() {
        val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_filtercosts, null)
        val dpFilterDate = layout.findViewById<DatePicker>(R.id.dpFilterDate)
        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(layout)

        with(builder.create()) {
            setTitle("Показывать с:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> })
            setButton(BUTTON_POSITIVE, "Применить",
                    { dialog, which -> activity.getFilteredCostsRecyclerViewAdapter(readDatePicker(dpFilterDate))

                    })
            show()
        }
    }

    @SuppressLint("InflateParams")
    fun dialogAddCost() {

        val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_addcost, null)
        val etComment = layout.findViewById<ET>(R.id.etComment)
        val etPrice = layout.findViewById<ET>(R.id.etPrice)
        val dpEventDate = layout.findViewById<DatePicker>(R.id.dpEventDate)

        var costParam: CostsCarTabParam? = null
        var param: CarTabParam? = null

        val actvParam = layout.findViewById<AppCompatAutoCompleteTextView>(R.id.actvParam)
        with(actvParam) {
            setAdapter(ArrayAdapter<Str>(activity, android.R.layout.simple_list_item_1, activity.pr.getParamStringList()))
            setOnItemClickListener { adapterView, view, i, l ->

                val tv = view as TV
                costParam = activity.pr.getParam(tv.text.toString())
                param = costParam?.param
                etPrice.setText(param!!.replaceCost.toString())

                with(getInstance()) {
                    timeZone = TimeZone.getTimeZone("GMT")
                    timeInMillis = param!!.replaceDate

                    dpEventDate.updateDate(
                            get(YEAR),
                            get(MONTH),
                            get(DAY_OF_MONTH))
                }

            }
        }


        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(layout)
        with(builder.create()) {

            setTitle("Добавление расхода:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> dialog.cancel() })
            setButton(BUTTON_POSITIVE, "Добавить",
                    { dialog, which ->

                        var limit = 0
                        val cost: CarCost

                        try {
                            limit = param!!.replaceLimit
                            with (costParam!!.param) {
                                replaceCost = etPrice.text.toString().toInt()
                                replaceDate = readDatePicker(dpEventDate)
                                replaceMileage = CarList[SelectedCar.index].replaceMileage
                            }
                            activity.pr.updateParam(costParam!!)
                        } catch (e: NullPointerException) {
                            limit = 0
                        }

                        var mileage = CarList[SelectedCar.index].replaceMileage
                        if (mileage <= 0)
                            mileage = CarList[SelectedCar.index].createMileage

                        cost = CarCost(actvParam.text.toString(),
                                mileage,
                                readDatePicker(dpEventDate),
                                limit,
                                etPrice.text.toString().toInt(),
                                etComment.text.toString())

                        activity.pr.saveCost(cost)
                        activity.notifyAdapter()
                    })
            show()
        }
    }

    @SuppressLint("InflateParams")
    private fun dialogEditCost(position: Int) {
        val layout = LayoutInflater.from(activity).inflate(R.layout.dialog_editcost, null)

        val tvTitle = layout.findViewById<TV>(R.id.tvTitle)
        val etPrice = layout.findViewById<ET>(R.id.etPrice)
        val etComment = layout.findViewById<ET>(R.id.etComment)

        val currentCost =activity.pr.getCost(position)
        val (title, mileage, date, limit, price, comment) = currentCost

        tvTitle.text = title
        etPrice.setText(price.toString())
        etComment.setText(comment)

        val builder = ADB(activity, R.style.AlertDialogCustom)
        builder.setView(layout)
        with(builder.create()) {
            setTitle("Редактирование расхода:")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> dialog.cancel() })
            setButton(BUTTON_POSITIVE, "Сохранить",
                    { dialog, which ->

                        currentCost.price = etPrice.text.toString().toInt()
                        currentCost.comment = tvComment.text.toString()

                        activity.pr.updateCost(position, currentCost)
                        activity.notifyAdapter()
                    })
            show()
        }


    }

    private fun dialogRemoveCost(position: Int) {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Удалить расход ${activity.pr.getCurrentCostTitle(position).toLowerCase()}?")
            setButton(BUTTON_NEGATIVE, "Отмена",
                    { dialog, which -> activity.showToast("Удаление отменено.") })
            setButton(BUTTON_POSITIVE, "Удалить",
                    { dialog, which -> activity.pr.removeCost(position) })
            show()
        }
    }

    fun showCostsPopupMenu(v: View, position: Int): Bool {

        val popupMenu = PopupMenu(activity, v)
        with(popupMenu) {
            inflate(R.menu.popupmenu_costs)
            setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_edit -> dialogEditCost(position)
                    R.id.menu_remove -> dialogRemoveCost(position)
                    else -> return@OnMenuItemClickListener false
                }
                false
            })
            show()
        }
        return true
    }

    private fun readDatePicker(datePicker: DP): Long {
        val cal = getInstance()
        with(cal) {
            timeZone = TimeZone.getTimeZone("GMT")
            with(datePicker) {
                set(DAY_OF_MONTH, dayOfMonth)
                set(MONTH, month)
                set(YEAR, year)
            }

        }
        return cal.timeInMillis
    }
}