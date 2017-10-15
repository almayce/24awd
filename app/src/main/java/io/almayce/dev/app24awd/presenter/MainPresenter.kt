package io.almayce.dev.app24awd.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.CarTab
import io.almayce.dev.app24awd.model.CarTabParam
import io.almayce.dev.app24awd.adapter.TabGridViewAdapter
import io.almayce.dev.app24awd.model.SelectedCar
import io.almayce.dev.app24awd.view.main.MainView
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.InvalidClassException

/**
 * Created by almayce on 26.09.17.
 */
@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    fun deserialize() {
        try {
            Serializer.deserialize()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: InvalidClassException) {
            e.printStackTrace()
        } catch (e: EOFException) {
            e.printStackTrace()
        }
    }

    fun selectCar(position: Int) {
        SelectedCar.index = position
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
    }

    fun getSelectedCarMileage(position: Int): Int =
            if (CarList.get(position).replaceMileage > 0)
                CarList.get(position).replaceMileage
            else CarList.get(position).createMileage


    fun saveSelectedCarMileage(position: Int, mileage: Int) {
        val car = CarList.get(position)

        if (mileage < car.createMileage)
            car.createMileage = mileage
        else {
            car.replaceMileage = mileage
            car.replaceDate = System.currentTimeMillis()
        }

//        val dayMileage = car.dayMileage
//        val replaceMileage = car.replaceMileage
//
//        val mileageUpdate = car.mileageUpdate
//        val currentMileageUpdate = System.currentTimeMillis()
//
//        val deltaMileage = mileage - replaceMileage
//        val deltaUpdate = Math.abs(currentMileageUpdate - mileageUpdate)
//        val deltaUpdateDays = TimeUnit.DAYS.convert(deltaUpdate, TimeUnit.MILLISECONDS).toInt()
//
//        try {
//            car.dayMileage = (deltaMileage / deltaUpdateDays + dayMileage) / 2
//        } catch (e: ArithmeticException) {
//            car.dayMileage = (deltaMileage + dayMileage) / 2
//
//            e.printStackTrace()
//        }
//        println(car.dayMileage)
        Serializer.serialize()
        viewState.updateTitle()
    }

    fun renameSelectedCar(position: Int, title: String) {
        var car = CarList.get(position)
        car.model = title

        CarList.set(position, car)
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
        Serializer.serialize()
    }

    fun removeSelectedCar(position: Int) {
        CarList.removeAt(position)
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
        Serializer.serialize()
    }

    fun addTab(title: String, icon: Int) {
        var tab = CarTab(title, icon, arrayListOf<CarTabParam>())
        CarList.get(SelectedCar.index).tabs.add(tab)
        viewState.notifyDataSetChanged()
        Serializer.serialize()
    }

    fun editTab(position: Int, title: String) {
        var tab = CarList.get(SelectedCar.index).tabs.get(position)
        tab.title = title

        CarList.get(SelectedCar.index).tabs.set(position, tab)
        viewState.notifyDataSetChanged()
        Serializer.serialize()
    }

    fun getTabTitle(position: Int): String = CarList.get(SelectedCar.index).tabs.get(position).title
    fun removeTab(position: Int) {
        CarList.get(SelectedCar.index).tabs.removeAt(position)
        viewState.notifyDataSetChanged()
        Serializer.serialize()
    }

    fun getTabGridViewAdapter(context: Context, index: Int): TabGridViewAdapter {
        val list = ArrayList<CarTab>()
        list.addAll(CarList.get(index).tabs)
        list.add(list.get(list.size - 1))
        return TabGridViewAdapter(context, list)

    }

    fun getSelectedCarModel(index: Int) = CarList.get(index).model
}