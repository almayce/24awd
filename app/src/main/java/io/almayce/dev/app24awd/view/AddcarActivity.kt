package io.almayce.dev.app24awd.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.Car
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.CarTabPack
import io.almayce.dev.app24awd.model.SelectedCar
import io.almayce.dev.app24awd.view.main.MainActivity
import kotlinx.android.synthetic.main.app_bar_addcar.*
import kotlinx.android.synthetic.main.content_addcar.*

/**
 * Created by almayce on 22.09.17.
 */
class AddcarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcar)
        setSupportActionBar(toolbar)

        if (CarList.isNotEmpty()) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }
        supportActionBar?.title = "Добавление автомобиля"

        fabConfirm.setOnClickListener({
            if (checkFields()) {
                CarList.add(Car(etModel.text.toString(),
                        etVin.text.toString(),
                        etCapacity.text.toString().toFloat(),
                        etPower.text.toString().toInt(),
                        etYear.text.toString().toInt(),
                        System.currentTimeMillis(),
                        0,
                        etTotalMileage.text.toString().toInt(),
                        0,
                        CarTabPack.getAllTabs(),
                        arrayListOf()
                ))
                Serializer.serialize()
                onBackPressed()
                showToast("Автомобиль создан.")
                SelectedCar.index = CarList.size - 1
            } else showToast("Заполните все поля.")
        })
    }

    fun showToast(text: String) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    fun checkFields(): Boolean =
            etModel.text.toString().isNotEmpty() &&
                    etVin.text.toString().isNotEmpty() &&
                    etCapacity.text.toString().isNotEmpty() &&
                    etPower.text.toString().isNotEmpty() &&
                    etYear.text.toString().isNotEmpty() &&
                    etTotalMileage.text.toString().isNotEmpty()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}