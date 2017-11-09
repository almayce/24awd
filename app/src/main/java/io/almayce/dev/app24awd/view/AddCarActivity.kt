package io.almayce.dev.app24awd.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.almayce.dev.app24awd.Bool
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.cars.Car
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabPack
import io.almayce.dev.app24awd.model.cars.SelectedCar
import kotlinx.android.synthetic.main.app_bar_addcar.*
import kotlinx.android.synthetic.main.content_addcar.*

/**
 * Created by almayce on 22.09.17.
 */
class AddCarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addcar)
        setSupportActionBar(toolbar)

        if (CarList.isNotEmpty()) {
            with(supportActionBar!!) {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                title = "Добавление автомобиля"
            }
        }


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
                Serializer.serialize(Serializer.FileName.CARS)
                onBackPressed()
                showToast("Автомобиль создан.")
                SelectedCar.index = CarList.size - 1
            } else showToast("Заполните все поля.")
        })
    }

    fun showToast(text: Str) = Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

    private fun checkFields(): Bool =
            etModel.text.toString().isNotEmpty() &&
                    etVin.text.toString().isNotEmpty() &&
                    etCapacity.text.toString().isNotEmpty() &&
                    etPower.text.toString().isNotEmpty() &&
                    etYear.text.toString().isNotEmpty() &&
                    etTotalMileage.text.toString().isNotEmpty()

    override fun onSupportNavigateUp(): Bool {
        onBackPressed()
        return true
    }


}