package io.almayce.dev.app24awd.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.adapter.TabGridViewAdapter
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CarTab
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.model.docs.DocList
import io.almayce.dev.app24awd.model.docs.DocsPack
import io.almayce.dev.app24awd.view.main.MainView
import java.io.EOFException
import java.io.FileNotFoundException
import java.io.InvalidClassException

/**
 * Created by almayce on 26.09.17.
 */
@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    fun onCreate() {
        CarList.clear()
        DocList.clear()
        deserialize()
        checkDocs()
    }

    private fun deserialize() = try {
        Serializer.deserialize(Serializer.FileName.CARS)
        Serializer.deserialize(Serializer.FileName.DOCS)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: InvalidClassException) {
        e.printStackTrace()
    } catch (e: EOFException) {
        e.printStackTrace()
    }

    private fun checkDocs() {
        if (DocList.isEmpty()) DocList.addAll(DocsPack.getAllDocs())
    }

    fun selectCar(position: Int) {
        SelectedCar.index = position
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
    }

    fun getSelectedCarMileage(position: Int): Int =
            if (CarList[position].replaceMileage > 0)
                CarList[position].replaceMileage
            else CarList[position].createMileage


    fun saveSelectedCarMileage(position: Int, mileage: Int) {
        val car = CarList[position]
        if (mileage < car.createMileage)
            car.createMileage = mileage
        else {
            car.replaceMileage = mileage
            car.replaceDate = System.currentTimeMillis()
        }
        serialize()
        viewState.updateTitle()
    }

    fun renameSelectedCar(position: Int, title: Str) {
        val car = CarList[position]
        car.model = title

        CarList[position] = car
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
        serialize()
    }

    fun removeSelectedCar(position: Int) {
        CarList.removeAt(position)
        viewState.selectCar(position)
        viewState.notifyDataSetChanged()
        serialize()
    }

    fun addTab(title: Str, icon: Int) {
        val tab = CarTab(title, icon, arrayListOf())
        CarList[SelectedCar.index].tabs.add(tab)
        viewState.notifyDataSetChanged()
        serialize()
    }

    fun editTab(position: Int, title: Str) {
        val tab = CarList[SelectedCar.index].tabs[position]
        tab.title = title

        CarList[SelectedCar.index].tabs[position] = tab
        viewState.notifyDataSetChanged()
        serialize()
    }

    fun getTabTitle(position: Int): Str = CarList[SelectedCar.index].tabs[position].title
    fun removeTab(position: Int) {
        CarList[SelectedCar.index].tabs.removeAt(position)
        viewState.notifyDataSetChanged()
        serialize()
    }

    fun getTabGridViewAdapter(context: Context, index: Int): TabGridViewAdapter {
        val list = ArrayList<CarTab>()
        list.addAll(CarList[index].tabs)
        list.add(list[list.size - 1])
        return TabGridViewAdapter(context, list)
    }

    fun getSelectedCarModel(index: Int) = CarList[index].model

    private fun serialize() = Serializer.serialize(Serializer.FileName.CARS)
}