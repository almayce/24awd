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
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.adapter.CarRecyclerViewAdapter
import io.almayce.dev.app24awd.adapter.TabGridViewAdapter
import io.almayce.dev.app24awd.global.Reminder
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.presenter.MainPresenter
import io.almayce.dev.app24awd.view.main.control.MainActivityCarControl
import io.almayce.dev.app24awd.view.main.control.MainActivityControl
import io.almayce.dev.app24awd.view.main.control.MainActivityTabControl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

open class MainActivity : MvpAppCompatActivity(), MainView,
        CarRecyclerViewAdapter.ItemClickListener,
        CarRecyclerViewAdapter.ItemLongClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    @InjectPresenter
    lateinit var pr: MainPresenter

    private lateinit var navigation: MainActivityNavigation
    private lateinit var tabControl: MainActivityTabControl
    private lateinit var carControl: MainActivityCarControl

    lateinit var control: MainActivityControl
    lateinit var sos: MainActivitySOS
    lateinit var starter: Starter
    lateinit var verificator: MainVerificator
    lateinit var adapter: CarRecyclerViewAdapter

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
        initHelpers()
        initClickListeners()
        verificator.verifyStoragePermissions()
        pr.onCreate()
        if (CarList.isNotEmpty())
            carControl.mileageCarDialog(SelectedCar.index)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out Str>, grantResults: IntArray) =
            with(verificator) {
                when (requestCode) {
                    1 -> if (verifyStoragePermissions()) control.showWelcomeDialog()
                    2 -> if (hasLocationPermission()) starter.startLocationActivity()
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
        adapter = CarRecyclerViewAdapter(this, CarList)
        with(adapter) {
            setClickListener(this@MainActivity)
            setLongClickListener(this@MainActivity)
        }
    }

    private fun initControls() {
        navigation = MainActivityNavigation(this)
        tabControl = MainActivityTabControl(this)
        carControl = MainActivityCarControl(this)
        control = MainActivityControl(this, adapter)
    }

    private fun initHelpers(){
        verificator = MainVerificator(this)
        sos = MainActivitySOS(this)
        starter = Starter(this)
    }

    private fun initClickListeners() {
        nav_view.setNavigationItemSelectedListener(navigation.navigationListener)
//        fabAdd.setOnClickListener({ tabControl.addTabDialog() })
        with(gvMain) {
            setOnItemClickListener { adapterView, view, i, l ->
                if (view.contentDescription == "add") tabControl.addTabDialog()
                else starter.startTabActivity(SelectedCar.index, i)
            }
            setOnItemLongClickListener { adapterView, view, i, l -> tabControl.showTabPopupMenu(view, i) }
        }
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

    override fun selectCar(position: Int) = try {
        checkCars()
        carControl.mileageCarDialog(position)
    } catch (e: IndexOutOfBoundsException) {
        supportActionBar?.title = "24awd"
    }

    private fun checkCars() {
        if (CarList.isNotEmpty()) {
            gvMain.adapter = pr.getTabGridViewAdapter(this, SelectedCar.index)
            updateTitle()
        }
    }

    override fun onBackPressed() = if (drawer_layout.isDrawerOpen(GravityCompat.START))
        drawer_layout.closeDrawer(GravityCompat.START)
    else super.onBackPressed()

    override fun onCreateOptionsMenu(menu: Menu): Bool {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Bool {
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
            R.id.rlCoordinates -> starter.startCoordinatesActivity()
            R.id.btCrash -> starter.startDtpActivity()
            R.id.tvSite -> starter.goTo("http://24awd.com/")
            R.id.tvService -> if (CarList.isNotEmpty()) starter.startOrderActivity() else showToast("Добавьте автомобиль.")
            R.id.tvParts -> if (CarList.isNotEmpty()) starter.startOrderActivity() else showToast("Добавьте автомобиль.")
            R.id.rlSOS -> sos.shareLocation()
            R.id.btDocuments -> starter.startDocsActivity()
        }
    }

    fun showToast(text: Str) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

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
