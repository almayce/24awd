package io.almayce.dev.app24awd.view.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

open class MainActivity : MvpAppCompatActivity(), MainView,
        CarRecyclerViewAdpater.ItemClickListener,
        CarRecyclerViewAdpater.ItemLongClickListener,
ActivityCompat.OnRequestPermissionsResultCallback
        {


    @InjectPresenter
    lateinit var pr: MainPresenter
    lateinit var navigation: MainActivityNavigation
    lateinit var control: MainActivityControl
    lateinit var tabControl: MainActivityTabControl
    lateinit var carControl: MainActivityCarControl
    lateinit var sos: MainActivitySOS
    lateinit var starter: Starter
    lateinit var verificator: MainVerificator
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
        verificator.verifyStoragePermissions()
        pr.onCreate()
        if (CarList.isNotEmpty())
            carControl.mileageCarDialog(SelectedCar.index)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> if (verificator.verifyStoragePermissions())control.showWelcomeDialog()
            2 -> if (verificator.hasLocationPermission()) starter.startLocationActivity()
        }
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
        verificator = MainVerificator(this)
        sos = MainActivitySOS(this)
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
            carControl.mileageCarDialog(position)
        } catch (e: IndexOutOfBoundsException) {
            supportActionBar?.title = "24awd"
        }
    }

    private fun checkCars() {
        if (CarList.isNotEmpty()) {
            gvMain.adapter = pr.getTabGridViewAdapter(this, SelectedCar.index)
            updateTitle()
        }
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
            R.id.btCosts -> if (CarList.isNotEmpty()) starter.startCostsActivity() else showToast("Добавьте автомобиль.")
            R.id.rlLocation -> {
                if (verificator.verifyLocationPermissions())
                    starter.startLocationActivity()
            }
            R.id.rlCoordinates ->  starter.startCoordinatesActivity()
            R.id.btCrash -> starter.startDtpActivity()
            R.id.tvSite -> starter.goTo("http://24awd.com/")
            R.id.tvService -> if (CarList.isNotEmpty()) starter.startOrderActivity() else showToast("Добавьте автомобиль.")
            R.id.tvParts -> if (CarList.isNotEmpty()) starter.startOrderActivity() else showToast("Добавьте автомобиль.")
            R.id.rlSOS -> sos.shareLocation()
            R.id.btDocuments -> starter.startDocsActivity()
        }
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    override fun notifyDataSetChanged() {
        adapter.notifyDataSetChanged()
        val adapter = gvMain.adapter as TabGridViewAdapter
        adapter.notifyDataSetChanged()
        try {
            checkCars()
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
            control.showCarsDialog()

        }
    }
}
