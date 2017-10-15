package io.almayce.dev.app24awd.view.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import io.almayce.dev.app24awd.view.AddcarActivity
import io.almayce.dev.app24awd.view.CostsActivity
import io.almayce.dev.app24awd.view.HistoryActivity
import io.almayce.dev.app24awd.view.TabActivity

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

    fun startHistoryActivity() {
        var intent = Intent(activity, HistoryActivity::class.java)
        activity.startActivity(intent)
    }

    fun startTabActivity(carIndex: Int, tabIndex: Int) {
        val intent = Intent(activity, TabActivity::class.java)
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