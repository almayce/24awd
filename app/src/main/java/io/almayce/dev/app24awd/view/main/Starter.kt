package io.almayce.dev.app24awd.view.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import io.almayce.dev.app24awd.view.*
import io.almayce.dev.app24awd.view.docs.DocsActivity
import io.almayce.dev.app24awd.view.location.LocationActivity
import io.almayce.dev.app24awd.view.tabs.TabsActivity

/**
 * Created by almayce on 27.09.17.
 */
class Starter(val activity: Activity) {

    fun startCostsActivity() {
        var intent = Intent(activity, CostsActivity::class.java)
        activity.startActivity(intent)
    }

    fun startAddcarActivity() {
        var intent = Intent(activity, AddcarActivity::class.java)
        activity.startActivity(intent)
    }

    fun startLocationActivity() {
        var intent = Intent(activity, LocationActivity::class.java)
        activity.startActivity(intent)
    }
    fun startDtpActivity() {
        var intent = Intent(activity, DtpActivity::class.java)
        activity.startActivity(intent)
    }
    fun startOrderActivity() {
        var intent = Intent(activity, OrderActivity::class.java)
        activity.startActivity(intent)
    }
    fun startCoordinatesActivity() {
        var intent = Intent(activity, CoordinatesActivity::class.java)
        activity.startActivity(intent)
    }

    fun startDocsActivity() {
        var intent = Intent(activity, DocsActivity::class.java)
        activity.startActivity(intent)
    }

    fun startTabActivity(carIndex: Int, tabIndex: Int) {
        val intent = Intent(activity, TabsActivity::class.java)
        intent.putExtra("carIndex", carIndex)
        intent.putExtra("tabIndex", tabIndex)
        activity.startActivity(intent)
    }

    fun goTo(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)
        activity.startActivity(browserIntent)
    }
}