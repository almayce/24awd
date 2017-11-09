package io.almayce.dev.app24awd.view.main

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.model.cars.CarList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityNavigation(val activity: MainActivity) {

    val navigationListener = NavigationView.OnNavigationItemSelectedListener { item ->
        val id = item.itemId

        with(activity) {
            when (id) {
                R.id.nav_costs -> onNavCostsClick()
                R.id.nav_buy -> onNavBuyClick()
                R.id.nav_location -> {
                    if (verificator.verifyLocationPermissions())
                        starter.startLocationActivity()
                }
                R.id.nav_instruction -> starter.startDtpActivity()
                R.id.nav_sos -> sos.shareLocation()
                R.id.nav_coordinates -> starter.startCoordinatesActivity()
                R.id.nav_doc -> starter.startDocsActivity()
//            R.id.nav_site -> activity.starter.goTo("http://24awd.com/")
                else -> showToast("В стадии разработки.")
            }
        }

        launch(UI) {
            delay(1000)
            activity.drawer_layout.closeDrawer(GravityCompat.START)
        }
        true
    }

    private fun onNavCostsClick() = if (CarList.isNotEmpty()) activity.starter.startCostsActivity()
    else {
        with(activity) {
            showToast("Выберите автомобиль.")
            control.showCarsDialog()
        }
    }

    private fun onNavBuyClick() =
            if (CarList.isNotEmpty()) activity.starter.startOrderActivity()
            else {
                with(activity) {
                    showToast("Выберите автомобиль.")
                    control.showCarsDialog()
                }
            }
}