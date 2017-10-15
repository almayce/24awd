package io.almayce.dev.app24awd.view

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatAutoCompleteTextView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.presenter.CostsPresenter
import io.almayce.dev.app24awd.adapter.CostsRecyclerViewAdpater
import io.almayce.dev.app24awd.model.*
import kotlinx.android.synthetic.main.app_bar_addcar.*
import kotlinx.android.synthetic.main.content_costs.*
import kotlinx.android.synthetic.main.item_costs.*
import java.util.*


/**
 * Created by almayce on 26.09.17.
 */
class CostsActivity : MvpAppCompatActivity(), CostsView, CostsRecyclerViewAdpater.ItemClickListener, CostsRecyclerViewAdpater.ItemLongClickListener {

    @InjectPresenter
    lateinit var pr: CostsPresenter
    private lateinit var adapter: CostsRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_costs)
        setSupportActionBar(toolbar)
        initActionBar()
        initAdapter()
        fabAdd.setOnClickListener({ dialogAddCost() })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cost, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cost -> dialogFilterCost()
            else -> onBackPressed()
        }
        return true
    }

    fun dialogFilterCost() {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_filtercost, null)
        val dpFilterDate = layout.findViewById<DatePicker>(R.id.dpFilterDate)
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setView(layout)

        val alertDialog = builder.create()
        alertDialog.setTitle("Показывать с:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Применить",
                { dialog, which ->
                    adapter = pr.getFilteredCostsRecyclerViewAdapter(this, readDatePicker(dpFilterDate))
                    adapter.notifyDataSetChanged()
                    adapter.setClickListener(this)
                    rvCosts.adapter = adapter
                })
        alertDialog.show()
    }

    fun readDatePicker(datePicker: DatePicker): Long {
        val cal = Calendar.getInstance()
        cal.timeZone = TimeZone.getTimeZone("GMT")
        cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth())
        cal.set(Calendar.MONTH, datePicker.getMonth())
        cal.set(Calendar.YEAR, datePicker.getYear())
        return cal.timeInMillis
    }

    fun initAdapter() {
        rvCosts.layoutManager = LinearLayoutManager(this)
        adapter = pr.getCostsRecyclerViewAdpater(this)
        adapter.setClickListener(this)
        rvCosts.adapter = adapter
    }

    override fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }

    fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "${pr.getSelectedCarModel()} - ${pr.getSelectedCarMileage()} км."
    }

    fun dialogAddCost() {

        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_addcost, null)
        val etComment = layout.findViewById<EditText>(R.id.etComment)
        val etPrice = layout.findViewById<EditText>(R.id.etPrice)
        val dpEventDate = layout.findViewById<DatePicker>(R.id.dpEventDate)

        var costParam: CostsCarTabParam? = null
        var param: CarTabParam? = null

        val actvParam = layout.findViewById<AppCompatAutoCompleteTextView>(R.id.actvParam)
        actvParam.setAdapter(ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pr.getParamStringList()))
        actvParam.setOnItemClickListener { adapterView, view, i, l ->

            var tv = view as TextView
            costParam = pr.getParam(tv.text.toString())
            param = costParam?.param
            etPrice.setText(param!!.replaceCost.toString())

            val cal = Calendar.getInstance()
            cal.timeZone = TimeZone.getTimeZone("GMT")
            cal.timeInMillis = param!!.replaceDate

            dpEventDate.updateDate(cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH))
        }

        val builder = AlertDialog.Builder(this@CostsActivity, R.style.AlertDialogCustom)
        builder.setView(layout)
        val alertDialog = builder.create()

        alertDialog.setTitle("Добавление расхода:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> dialog.cancel() })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить",
                { dialog, which ->

                    //                    val cal = Calendar.getInstance()
//                    cal.timeZone = TimeZone.getTimeZone("UTC")
//                    cal.set(Calendar.DAY_OF_MONTH, dpEventDate.getDayOfMonth())
//                    cal.set(Calendar.MONTH, dpEventDate.getMonth())
//                    cal.set(Calendar.YEAR, dpEventDate.getYear())

                    var limit = 0
                    var cost: CarCost

                    try {
                        limit = param!!.replaceLimit
                        costParam!!.param.replaceCost = etPrice.text.toString().toInt()
                        costParam!!.param.replaceDate = readDatePicker(dpEventDate)
                        costParam!!.param.replaceMileage = CarList.get(SelectedCar.index).replaceMileage
                        pr.updateParam(costParam!!)
                    } catch (e: NullPointerException) {
                        limit = 0
                    }

                    cost = CarCost(actvParam.text.toString(),
                            CarList.get(SelectedCar.index).replaceMileage,
                            readDatePicker(dpEventDate),
                            limit,
                            etPrice.text.toString().toInt(),
                            etComment.text.toString())

                    pr.saveCost(cost)
                    adapter.notifyDataSetChanged()
                })
        alertDialog.show()
    }

    override fun onItemClick(view: View, position: Int) {
        showCostsPopupMenu(view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        showCostsPopupMenu(view, position)
    }

    fun showCostsPopupMenu(v: View, position: Int): Boolean {

        val popupMenu = PopupMenu(this, v)
        popupMenu.inflate(R.menu.popupmenu_costs)
        popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {

            override fun onMenuItemClick(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    R.id.menu_edit -> dialogEditCost(position)
//                    R.id.menu_remind -> {
//                        Notificator().setAlarm(this@CostsActivity,
//                                pr.getParamList().get(position).param.replaceDate - 1,
//                                CarList.get(SelectedCar.index).model,
//                                "Требуется замена ${pr.getParamList().get(position).param.title}")
//                        showToast("Напоминание учтено.")
//                    }
                    R.id.menu_remove -> dialogRemoveCost(position)
                    else -> return false
                }
                return false
            }
        })
        popupMenu.show()
        return true
    }

    fun dialogEditCost(position: Int) {
        val layout = LayoutInflater.from(this).inflate(R.layout.dialog_editcost, null)

        val tvTitle = layout.findViewById<TextView>(R.id.tvTitle)
        val etPrice = layout.findViewById<EditText>(R.id.etPrice)
        val etComment = layout.findViewById<EditText>(R.id.etComment)
//        val dpEventDate = layout.findViewById<DatePicker>(R.id.dpEventDate)

        val currentCost = pr.getCost(position)

        tvTitle.setText(currentCost.title)
        etPrice.setText(currentCost.price.toString())
        etComment.setText(currentCost.comment)

//        val cal = Calendar.getInstance()
//        cal.timeZone = TimeZone.getTimeZone("UTC")
//        cal.timeInMillis = currentCost.date
//
//        dpEventDate.updateDate(cal.get(Calendar.YEAR),
//                cal.get(Calendar.MONTH),
//                cal.get(Calendar.DAY_OF_MONTH))

        val builder = AlertDialog.Builder(this@CostsActivity, R.style.AlertDialogCustom)
        builder.setView(layout)
        val alertDialog = builder.create()

        alertDialog.setTitle("Редактирование расхода:")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> dialog.cancel() })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Сохранить",
                { dialog, which ->

                    //                    val cal = Calendar.getInstance()
//                    cal.timeZone = TimeZone.getTimeZone("UTC")
//                    cal.set(Calendar.DAY_OF_MONTH, dpEventDate.getDayOfMonth())
//                    cal.set(Calendar.MONTH, dpEventDate.getMonth())
//                    cal.set(Calendar.YEAR, dpEventDate.getYear())

                    currentCost.price = etPrice.text.toString().toInt()
                    currentCost.comment = tvComment.text.toString()
//                    currentCost.date = readDatePicker(dpEventDate)

                    pr.updateCost(position, currentCost)
                    adapter.notifyDataSetChanged()
                })
        alertDialog.show()
    }

    fun dialogRemoveCost(position: Int) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Удалить расход ${this.pr.getCurrentCostTitle(position).toLowerCase()}?")
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена",
                { dialog, which -> this.showToast("Удаление отменено.") })
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Удалить",
                { dialog, which -> this.pr.removeCost(position) })
        alertDialog.show()
    }

    fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}