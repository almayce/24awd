package io.almayce.dev.app24awd.view.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.adapter.CarRecyclerViewAdpater
import io.almayce.dev.app24awd.adapter.TabGridViewAdapter
import io.almayce.dev.app24awd.global.Reminder
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.SelectedCar
import io.almayce.dev.app24awd.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

open class MainActivity : MvpAppCompatActivity(), MainView,
        CarRecyclerViewAdpater.ItemClickListener,
        CarRecyclerViewAdpater.ItemLongClickListener {


    @InjectPresenter
    lateinit var pr: MainPresenter
    lateinit var navigation: MainActivityNavigation
    lateinit var control: MainActivityControl
    lateinit var tabControl: MainActivityTabControl
    lateinit var carControl: MainActivityCarControl
    lateinit var starter: Starter
    lateinit var verificator: Verificator
    lateinit var adapter: CarRecyclerViewAdpater

    override fun onResume() {
        super.onResume()
        checkCars()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initActionBar()
        initAdapter()
        initControls()
        initClickListeners()
        if (verificator.verifyStoragePermissions())
            CarList.clear()
        pr.deserialize()
        if (CarList.isNotEmpty())
            carControl.mileageCarDialog(SelectedCar.index)
    }

    override fun updateTitle() {
        supportActionBar?.title = "${pr.getSelectedCarModel(SelectedCar.index)} - ${pr.getSelectedCarMileage(SelectedCar.index)} км."
    }

    private fun initActionBar() {
        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)
        drawer_layout.setDrawerListener(toggle)
        toggle.syncState()
    }

    private fun initAdapter() {
        adapter = CarRecyclerViewAdpater(this, CarList)
        adapter.setClickListener(this)
        adapter.setLongClickListener(this)
    }

    private fun initControls() {
        navigation = MainActivityNavigation(this)
        control = MainActivityControl(this, adapter)
        tabControl = MainActivityTabControl(this)
        carControl = MainActivityCarControl(this)
        starter = Starter(this)
        verificator = Verificator(this)
    }

    private fun initClickListeners() {
        nav_view.setNavigationItemSelectedListener(navigation.navigationListener)
//        fabAdd.setOnClickListener({ tabControl.addTabDialog() })
        gvMain.setOnItemClickListener { adapterView, view, i, l ->
            if (view.contentDescription == "add") tabControl.addTabDialog()
            else starter.startTabActivity(SelectedCar.index, i)
        }
        gvMain.setOnItemLongClickListener { adapterView, view, i, l -> tabControl.showTabPopupMenu(view, i) }
    }

    override fun onDestroy() {
        super.onDestroy()
        val serviceIntent = Intent(this, Reminder::class.java)
        startService(serviceIntent)
    }

    override fun onItemClick(view: View, position: Int) {
        carControl.showCarPopupMenu(view, position)
    }

    override fun onItemLongClick(view: View, position: Int) {
        carControl.showCarPopupMenu(view, position)
    }

    override fun selectCar(position: Int) {
        try {
            checkCars()
            carControl.mileageCarDialog(position)}
        catch (e: IndexOutOfBoundsException) {
            supportActionBar?.title = "24awd"
        }
        control.cancelDialog()
    }

    private fun checkCars() {
        if (CarList.isNotEmpty()) {
            gvMain.adapter = pr.getTabGridViewAdapter(this, SelectedCar.index)
            updateTitle()
            control.cancelDialog()
        } else control.showWelcomeDialog()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        control.showCarsDialog()
        return true
    }

    fun onMainContentClick(view: View) {
        when (view.id) {
            R.id.btCosts -> starter.startCostsActivity()
            R.id.rlCoordinates -> showToast("В стадии разработки.")
            R.id.rlCrash -> showToast("В стадии разработки.")
            R.id.rlBuy -> showToast("В стадии разработки.")
            R.id.tvSite -> starter.goTo("http://24awd.com/")
            else -> showToast("В стадии разработки.")
        }
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
        val adapter = gvMain.adapter as TabGridViewAdapter
        adapter.notifyDataSetChanged()
        try {
            checkCars() }
        catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            control.showCarsDialog()

        }
    }
}
