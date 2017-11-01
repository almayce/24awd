package io.almayce.dev.app24awd.view.main

import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.adapter.CarRecyclerViewAdpater
import io.almayce.dev.app24awd.view.AddcarActivity

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityControl(val activity: MainActivity, val adapter: CarRecyclerViewAdpater) {

    var carsAlertDialog: AlertDialog
    init {
        carsAlertDialog = AlertDialog.Builder(activity, R.style.AlertDialogCustom).create()
    }

    fun showWelcomeDialog() {
        val builder = AlertDialog.Builder(activity, R.style.AlertDialogCustom)
        val alertDialog = builder.create()
        alertDialog.setTitle("Добро пожаловать!")
        alertDialog.setMessage("Чтобы продолжить пользоваться приложением необходимо внести данные о своем автомобиле.")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Продолжить",
                { dialog, which -> activity.starter.startAddcarActivity() })
        alertDialog.show()
    }

    fun showCarsDialog() {
        val rvCars = RecyclerView(activity)
        rvCars.layoutManager = LinearLayoutManager(activity)
        rvCars.adapter = adapter

        carsAlertDialog.setTitle("Ваши автомобили:")
        carsAlertDialog.setView(rvCars)
        carsAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Отмена", {dialogInterface, i ->  })
        carsAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Добавить",
                { dialog, which -> activity.startActivity(Intent(activity, AddcarActivity::class.java)) })
        carsAlertDialog.show()
    }
}