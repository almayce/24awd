package io.almayce.dev.app24awd.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.CarList
import io.almayce.dev.app24awd.model.CarTabParam
import io.almayce.dev.app24awd.model.SelectedCar
import io.almayce.dev.app24awd.view.TabView

/**
 * Created by almayce on 27.09.17.
 */

@InjectViewState
class TabPresenter: MvpPresenter<TabView>() {
    var carIndex = 0
    var tabIndex = 0

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

        Serializer.serialize()

        viewState.notifyDataSetChanged()
    }
    fun addTabParam(param: CarTabParam) {
        CarList.get(carIndex).tabs
                .get(tabIndex).params
                .add(param)

        Serializer.serialize()

        viewState.notifyDataSetChanged()
    }

//    fun replaceTabParam(position: Int, cost: Int, replace: Long) {
//        val target = getCurrentParam(position)
//        target.replaceCost = cost
//        target.replaceDate = replace
//        CarList.get(carIndex).tabs
//                .get(tabIndex).params
//                .set(position, target)
//
//        Serializer.serialize()
//
//        viewState.notifyDataSetChanged()
////        viewState.updateHistory(target)
//    }
    fun removeTabParam(position: Int) {
        CarList.get(carIndex).tabs.get(tabIndex).params.removeAt(position)

        Serializer.serialize()

        viewState.notifyDataSetChanged()
    }

}