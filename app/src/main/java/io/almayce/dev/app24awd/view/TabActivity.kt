package io.almayce.dev.app24awd.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.CarTabParam
import io.almayce.dev.app24awd.adapter.ParamRecyclerViewAdpater
import io.almayce.dev.app24awd.model.CarTab
import io.almayce.dev.app24awd.model.SelectedCar
import io.almayce.dev.app24awd.presenter.TabPresenter
import kotlinx.android.synthetic.main.app_bar_tab.*
import kotlinx.android.synthetic.main.content_tab.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by almayce on 22.09.17.
 */
class TabActivity : MvpAppCompatActivity(), TabView, ParamRecyclerViewAdpater.ItemClickListener, ParamRecyclerViewAdpater.ItemLongClickListener {

    @InjectPresenter
    lateinit var pr: TabPresenter

    private lateinit var adapter: ParamRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tab)
        setSupportActionBar(toolbar)
        pr.carIndex = intent.getIntExtra("carIndex", 0)
        pr.tabIndex = intent.getIntExtra("tabIndex", 0)
        val currentTab = CarList.get(pr.carIndex).tabs.get(pr.tabIndex)
        initActionBar(currentTab)
        initAdapter(currentTab)
        fabAdd.setOnClickListener({ addTabParamDialog() })
    }

    fun initActionBar(currentTab: CarTab) {
        supportActionBar?.title = "${pr.getSelectedCarModel()} - ${pr.getSelectedCarMileage()} км."
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    fun initAdapter(currentTab: CarTab) {
        rvParams.layoutManager = LinearLayoutManager(this)
        adapter = ParamRecyclerViewAdpater(this, currentTab.params)
        adapter.setClickListener(this)
        adapter.setLongClickListener(this)
        rvParams.adapter = adapter
    }

    fun showPopupMenu(context: Context, v: View, position: Int): Boolean {

        val popupMenu = PopupMenu(context, v)
        popupMenu.inflate(R.menu.popupmenu_replace)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
//                    R.id.menu_replace -> replaceTabParamDialog(position)
                    R.id.menu_edit -> editTabParamDialog(position)
                    R.id.menu_remove -> removeTabParamDialog(position)
                    else -> return false
                }
                return false
            }
        })
        popupMenu.show()
        return true
    }

    fun addTabParamDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_addparam, null)

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

        val builder = AlertDialog.Builder(this@TabActivity, R.style.AlertDialogCustom)
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

                    pr.addTabParam(CarTabParam(
                            title.text.toString(),
                            cal.timeInMillis,
                            CarList.get(SelectedCar.index).replaceMileage,
                            cost.text.toString().toInt(),
                            limit.text.toString().toInt()))

                })
        alertDialog.show()
    }

    fun editTabParamDialog(position: Int) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_addparam, null)

        val title = view.findViewById<EditText>(R.id.etTitle)
        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)
        val cost = view.findViewById<EditText>(R.id.etCost)
        val limit = view.findViewById<EditText>(R.id.etLimit)

        val currentParam = pr.getCurrentParam(position)
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

        val builder = AlertDialog.Builder(this@TabActivity, R.style.AlertDialogCustom)
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

                    val car =  CarList.get(SelectedCar.index)

                    var replaceMileage = 0
                    if (car.replaceMileage.equals(0))
                        replaceMileage = car.createMileage
                    else replaceMileage = car.replaceMileage

                    pr.editTabParam(position, CarTabParam(
                            title.text.toString(),
                            cal.timeInMillis,
                           replaceMileage,
                            cost.text.toString().toInt(),
                            limit.text.toString().toInt()))
                })
        alertDialog.show()
    }

    //    fun replaceTabParamDialog(position: Int) {
//        val view = LayoutInflater.from(this).inflate(R.layout.dialog_replaceparam, null)
//        val etCost = view.findViewById<EditText>(R.id.etCost)
//        val dpReplace = view.findViewById<DatePicker>(R.id.dpReplace)
//        val builder = AlertDialog.Builder(this@TabActivity, R.style.AlertDialogCustom)
//        builder.setView(view)
//        val alertDialog = builder.create()
//
//        val currentParam = CarList.get(pr.carIndex).tabs.get(pr.tabIndex).params.get(position)
//        etCost.setText(currentParam.replaceCost.toString())
//
//
//        alertDialog.setTitle("Замена ${currentParam.title.toLowerCase()}")
//        alertDialog.setMessage("Укажите дату и стоимость замены:")
//        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
//                { dialog, which -> dialog.cancel() })
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
//                { dialog, which ->
//
//                    val cal = Calendar.getInstance()
//                    cal.timeZone = TimeZone.getTimeZone("GMT")
//                    cal.set(Calendar.DAY_OF_MONTH, dpReplace.getDayOfMonth())
//                    cal.set(Calendar.MONTH, dpReplace.getMonth())
//                    cal.set(Calendar.YEAR, dpReplace.getYear())
//
//                    pr.replaceTabParam(position,
//                            etCost.text.toString().toInt(),
//                            cal.timeInMillis)
//                })
//        alertDialog.show()
//    }
//
    private fun removeTabParamDialog(position: Int) {
        val builder = AlertDialog.Builder(this@TabActivity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить учет ${pr.getCurrentParam(position).title.toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> pr.removeTabParam(position) })
        alertDialog.show()
    }

//    override fun updateHistory(param: CarTabParam) {
//        val pref = getSharedPreferences("history", Context.MODE_PRIVATE)
//        val ed = pref.edit()
//
//        val target = "${CarList.get(pr.carIndex).model} - замена:\n" +
//                "${param.title}\n" +
//                "Стоимость: ${param.replaceCost} р.\n" +
//                "Дата: ${dateFormat(param.replaceDate)}"
//
//        var savedHistory = pref.getString("history", "")
//
//        println(target)
//
//        if (savedHistory == "")
//            ed.putString("history", target)
//        else ed.putString("history", "$target split $savedHistory ")
//        ed.apply()
//
//    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        return sdf.format(replaceDate)
    }

    private fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        showPopupMenu(this, view, position)
    }

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
    }
}