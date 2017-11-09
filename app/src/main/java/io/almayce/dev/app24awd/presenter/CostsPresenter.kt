package io.almayce.dev.app24awd.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.Str
import io.almayce.dev.app24awd.adapter.CostsRecyclerViewAdpater
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.model.cars.CarCost
import io.almayce.dev.app24awd.model.cars.CarList
import io.almayce.dev.app24awd.model.cars.CostsCarTabParam
import io.almayce.dev.app24awd.model.cars.SelectedCar
import io.almayce.dev.app24awd.view.costs.CostsView

/**
 * Created by almayce on 26.09.17.
 */
@InjectViewState
class CostsPresenter : MvpPresenter<CostsView>() {

    private fun getParamList(): ArrayList<CostsCarTabParam> {
        val tabs = CarList[SelectedCar.index].tabs
        val params = ArrayList<CostsCarTabParam>()
        for (t in tabs)
            t.params.mapTo(params) { CostsCarTabParam(tabs.indexOf(t), t.params.indexOf(it), it) }
        return params
    }

    fun getParam(title: Str): CostsCarTabParam {
        val tabs = CarList[SelectedCar.index].tabs
        var param: CostsCarTabParam? = null
        for (t in tabs)
            t.params
                    .filter { it.title == title }
                    .forEach { param = CostsCarTabParam(tabs.indexOf(t), t.params.indexOf(it), it) }
        return param!!
    }

    fun getParamStringList()
            = getParamList().map { it.param.title }
    fun getCostsRecyclerViewAdpater(context: Context)
            = CostsRecyclerViewAdpater(context, CarList[SelectedCar.index].costs)
    fun getFilteredCostsRecyclerViewAdapter(context: Context, date: Long)
            = CostsRecyclerViewAdpater(context, getFilteredCostsList(date))
    fun getFilteredByMileageCostsRecyclerViewAdapter(context: Context, mileage: Int)
            = CostsRecyclerViewAdpater(context, getFilteredByMileageCostsList(mileage))
    private fun getFilteredCostsList(date: Long)
            = CarList[SelectedCar.index].costs
            .filter { it.date > date } as ArrayList<CarCost>
    private fun getFilteredByMileageCostsList(mileage: Int)
            = CarList[SelectedCar.index].costs
            .filter { it.mileage > mileage } as ArrayList<CarCost>
    fun getCost(position: Int)
            = CarList[SelectedCar.index].costs[position]
    fun getCurrentCostTitle(position: Int)
            = CarList[SelectedCar.index].costs[position].title

    fun getSelectedCarModel() =
            try {
                CarList[SelectedCar.index].model
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }

    fun getSelectedCarMileage(): Int =
            with(CarList[SelectedCar.index]) {
                if (replaceMileage > 0)
                    replaceMileage
                else createMileage
            }


    fun updateParam(costParam: CostsCarTabParam) {
        CarList[SelectedCar.index]
                .tabs[costParam.tabIndex]
                .params[costParam.paramIndex] = costParam.param
        serialize()
    }

    fun saveCost(cost: CarCost) {
        CarList[SelectedCar.index].costs.add(0, cost)
        serialize()
        viewState.notifyAdapter()
    }

    fun updateCost(position: Int, cost: CarCost) {
        CarList[SelectedCar.index].costs[position] = cost
        serialize()
        viewState.notifyAdapter()
    }

    fun removeCost(position: Int) {
        CarList[SelectedCar.index].costs.removeAt(position)
        serialize()
        viewState.notifyAdapter()
    }

    private fun serialize() = Serializer.serialize(Serializer.FileName.CARS)

}