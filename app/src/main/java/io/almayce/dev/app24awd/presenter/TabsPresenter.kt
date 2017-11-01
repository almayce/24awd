package io.almayce.dev.app24awd.presenter

import android.content.Context
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.BitmapManager
import io.almayce.dev.app24awd.global.Notificator
import io.almayce.dev.app24awd.global.SchedulersTransformer
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.cars.Car
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.view.tabs.TabsView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 27.09.17.
 */

@InjectViewState
class TabsPresenter : MvpPresenter<TabsView>() {
    var carIndex = 0
    var tabIndex = 0

    var bm = BitmapManager()
    var nt = Notificator()

    init {
        bm.onTransformedBitmapObservable
                .compose(SchedulersTransformer())
                .subscribe({ it ->
                    viewState.notifyDataSetChanged()
                })
    }

    fun getSelectedCarModel() = CarList.get(SelectedCar.index).model
    fun getSelectedCarMileage(): Int =
            if (CarList.get(SelectedCar.index).replaceMileage > 0)
                CarList.get(SelectedCar.index).replaceMileage
            else CarList.get(SelectedCar.index).createMileage

    fun getCurrentParam(position: Int): CarTabParam =
            CarList.get(carIndex).tabs.get(tabIndex).params.get(position)

    fun editTabParam(position: Int, param: CarTabParam) {
        CarList.get(carIndex).tabs
                .get(tabIndex).params
                .set(position, param)

        serialize()

        viewState.notifyDataSetChanged()
    }

    fun addTabParam(param: CarTabParam) {
        CarList.get(carIndex).tabs
                .get(tabIndex).params
                .add(param)

        serialize()

        viewState.notifyDataSetChanged()
    }

    fun saveTabParams() {
        serialize()
    }

    fun setAlarm(context: Context, position: Int): String {
        val param = CarList.get(carIndex).tabs
                .get(tabIndex).params
                .get(position)
        val date = calculateReplaceDate(CarList.get(carIndex), param)
        val content = "В автомобиле ${CarList.get(carIndex).model} произведите замену ${param.title}"
        if (date > 0) {
            nt.setAlarm(context, date, "24awd", content)
            return "Напоминание на ${dateFormat(date)}."
        }
        else return "Для расчета даты напоминания введите актуальный пробег автомобиля."
    }

    fun dateFormat(replaceDate: Long): String {
        val sdf = SimpleDateFormat("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    fun calculateReplaceDate(car: Car, param: CarTabParam): Long {

        val replaceMileage = (param.replaceMileage + param.replaceLimit) - car.replaceMileage
        Log.d("replaceMileage", replaceMileage.toString())

        val deltaDate = car.replaceDate - car.createDate
        Log.d("deltaDate", deltaDate.toString())

        val deltaMileage = car.replaceMileage - car.createMileage
        Log.d("deltaMileage", deltaMileage.toString())

        var deltaDays = TimeUnit.DAYS.convert(deltaDate, TimeUnit.MILLISECONDS)
        Log.d("deltaDays", deltaDays.toString())

        if (deltaDays < 1) deltaDays = 1
        var avgPerDay = deltaMileage / deltaDays
        if (avgPerDay < 1) avgPerDay = 1
        val daysLeft = replaceMileage / avgPerDay
        Log.d("daysLeft", daysLeft.toString())
        if (avgPerDay <= 1)
            return -1
        else {
            val replaceDate = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(daysLeft, TimeUnit.DAYS)
            return replaceDate
        }
    }

    fun transformBitmap(path: String) {
        bm.transformBitmap(path)
    }

    fun removeTabParam(position: Int) {
        CarList.get(carIndex).tabs.get(tabIndex).params.removeAt(position)

        serialize()

        viewState.notifyDataSetChanged()
    }

    private fun serialize() {
        Serializer.serialize(Serializer.FileName.CARS)
    }

}