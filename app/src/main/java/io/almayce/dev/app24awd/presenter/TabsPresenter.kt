package io.almayce.dev.app24awd.presenter

import android.content.Context
import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.SDF
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.global.BitmapManager
import io.almayce.dev.app24awd.global.Notificator
import io.almayce.dev.app24awd.global.SchedulersTransformer
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.cars.Car
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.view.tabs.TabsView
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 27.09.17.
 */

@InjectViewState
class TabsPresenter : MvpPresenter<TabsView>() {
    var carIndex = 0
    var tabIndex = 0

    private var bm = BitmapManager()
    private var nt = Notificator()

    init {
        bm.onTransformedBitmapObservable
                ?.compose(SchedulersTransformer())
                ?.subscribe({ it ->
                    viewState.notifyDataSetChanged()
                })
    }

    fun getSelectedCarModel() = CarList[SelectedCar.index].model
    fun getSelectedCarMileage(): Int =
            with(CarList[SelectedCar.index]) {
                if (replaceMileage > 0)
                    replaceMileage
                else createMileage
            }


    fun getCurrentParam(position: Int): CarTabParam =
            CarList[carIndex].tabs[tabIndex].params[position]

    fun editTabParam(position: Int, param: CarTabParam) {
        CarList[carIndex].tabs[tabIndex].params[position] = param

        serialize()

        viewState.notifyDataSetChanged()
    }

    fun addTabParam(param: CarTabParam) {
        CarList[carIndex].tabs[tabIndex].params
                .add(param)

        serialize()

        viewState.notifyDataSetChanged()
    }

    fun saveTabParams() = serialize()

    fun setAlarm(context: Context, position: Int): Str {
        val param = CarList[carIndex].tabs[tabIndex].params[position]
        val date = calculateReplaceDate(CarList[carIndex], param)
        val content = "В автомобиле ${CarList[carIndex].model} произведите замену ${param.title}"
        return if (date > 0) {
            nt.setAlarm(context, date, "24awd", content)
            "Напоминание на ${dateFormat(date)}."
        }
        else "Для расчета даты напоминания введите актуальный пробег автомобиля."
    }

    private fun dateFormat(replaceDate: Long): Str {
        val sdf = SDF("dd / MM / yy", Locale.ROOT)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(replaceDate)
    }

    private fun calculateReplaceDate(car: Car, param: CarTabParam): Long {

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

        return if (avgPerDay <= 1)
            -1
        else
            System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(daysLeft, TimeUnit.DAYS)
    }

    fun transformBitmap(path: Str) = bm.transformBitmap(path)

    fun removeTabParam(position: Int) {
        CarList[carIndex].tabs[tabIndex].params.removeAt(position)

        serialize()

        viewState.notifyDataSetChanged()
    }

    private fun serialize() = Serializer.serialize(Serializer.FileName.CARS)

}