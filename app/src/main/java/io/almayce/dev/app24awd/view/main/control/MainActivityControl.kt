package io.almayce.dev.app24awd.view.main.control

import android.content.Intent
import android.support.v7.app.AlertDialog.BUTTON_NEGATIVE
import android.support.v7.app.AlertDialog.BUTTON_POSITIVE
import io.almayce.dev.app24awd.ADB
import io.almayce.dev.app24awd.LLM
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.RV
import io.almayce.dev.app24awd.adapter.CarRecyclerViewAdapter
import io.almayce.dev.app24awd.view.AddCarActivity
import io.almayce.dev.app24awd.view.main.MainActivity

/**
 * Created by almayce on 27.09.17.
 */
class MainActivityControl(val activity: MainActivity, private val carsAdapter: CarRecyclerViewAdapter) {

    fun showWelcomeDialog() {
        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Добро пожаловать!")
            setMessage("Чтобы продолжить пользоваться приложением необходимо внести данные о своем автомобиле.")
            setButton(BUTTON_POSITIVE, "Продолжить",
                    { dialog, which -> activity.starter.startAddcarActivity() })
            show()
        }
    }

    fun showCarsDialog() {
        val rvCars = RV(activity)
        with(rvCars) {
            layoutManager = LLM(activity)
            adapter = carsAdapter
        }

        val builder = ADB(activity, R.style.AlertDialogCustom)
        with(builder.create()) {
            setTitle("Ваши автомобили:")
            setView(rvCars)
            setButton(BUTTON_NEGATIVE, "Отмена", { dialogInterface, i -> })
            setButton(BUTTON_POSITIVE, "Добавить",
                    { dialog, which -> activity.startActivity(Intent(activity, AddCarActivity::class.java)) })
            show()
        }

    }
}