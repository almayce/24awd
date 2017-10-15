package io.almayce.dev.app24awd.view

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.MvpAppCompatActivity
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.adapter.HistoryRecyclerViewAdpater
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.SelectedCar
import kotlinx.android.synthetic.main.app_bar_addcar.*
import kotlinx.android.synthetic.main.content_history.*

/**
 * Created by almayce on 26.09.17.
 */

class HistoryActivity : MvpAppCompatActivity() {

    private lateinit var adapter: HistoryRecyclerViewAdpater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        setSupportActionBar(toolbar)
            initActionBar()
        initAdapter()
    }

    fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "${getSelectedCarModel()} - ${getSelectedCarMileage()} км."
    }

    fun getSelectedCarModel() = CarList.get(SelectedCar.index).model
    fun getSelectedCarMileage() = CarList.get(SelectedCar.index).replaceMileage

    fun initAdapter() {
        val pref = getSharedPreferences("history", Context.MODE_PRIVATE)
        rvHistory.layoutManager = LinearLayoutManager(this)
        adapter = HistoryRecyclerViewAdpater(this, pref.getString("history", "").split("split").toTypedArray())
        rvHistory.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
