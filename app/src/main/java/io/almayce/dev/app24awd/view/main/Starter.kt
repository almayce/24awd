package io.almayce.dev.app24awd.view.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.view.AddCarActivity
import io.almayce.dev.app24awd.view.CoordinatesActivity
import io.almayce.dev.app24awd.view.DtpActivity
import io.almayce.dev.app24awd.view.OrderActivity
import io.almayce.dev.app24awd.view.costs.CostsActivity
import io.almayce.dev.app24awd.view.docs.DocsActivity
import io.almayce.dev.app24awd.view.location.LocationActivity
import io.almayce.dev.app24awd.view.tabs.TabsActivity

/**
 * Created by almayce on 27.09.17.
 */
class Starter(val activity: Activity) {

    private val costsIntent = Intent(activity, CostsActivity::class.java)
    private val addcarIntent = Intent(activity, AddCarActivity::class.java)
    private val locationIntent =Intent(activity, LocationActivity::class.java)
    private val dtpIntent = Intent(activity, DtpActivity::class.java)
    private val orderIntent = Intent(activity, OrderActivity::class.java)
    private val coordinatesIntent= Intent(activity, CoordinatesActivity::class.java)
    private val docsIntent= Intent(activity, DocsActivity::class.java)

    fun startCostsActivity() = activity.startActivity(costsIntent)
    fun startAddcarActivity() = activity.startActivity(addcarIntent)
    fun startLocationActivity() = activity.startActivity(locationIntent)
    fun startDtpActivity() = activity.startActivity(dtpIntent)
    fun startOrderActivity() = activity.startActivity(orderIntent)
    fun startCoordinatesActivity() = activity.startActivity(coordinatesIntent)
    fun startDocsActivity() = activity.startActivity(docsIntent)

    fun startTabActivity(carIndex: Int, tabIndex: Int) = activity.startActivity(
            with(Intent(activity, TabsActivity::class.java)) {
        putExtra("carIndex", carIndex)
        putExtra("tabIndex", tabIndex)
    })

    fun goTo(link: Str) = activity.startActivity(
            with(Intent(Intent.ACTION_VIEW, Uri.parse(link))){
        addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
    })
}