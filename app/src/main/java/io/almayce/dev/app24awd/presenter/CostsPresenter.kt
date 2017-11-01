package io.almayce.dev.app24awd.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.adapter.CostsRecyclerViewAdpater
import io.almayce.dev.app24awd.model.cars.*
import io.almayce.dev.app24awd.view.CostsView
import kotlin.collections.ArrayList

/**
 * Created by almayce on 26.09.17.
 */
@InjectViewState
class CostsPresenter : MvpPresenter<CostsView>() {

    fun getParamList(): ArrayList<CostsCarTabParam> {
        val tabs = CarList.get(SelectedCar.index).tabs
        val params = ArrayList<CostsCarTabParam>()
        for (t in tabs)
            for (p in t.params)
                params.add(CostsCarTabParam(tabs.indexOf(t), t.params.indexOf(p), p))
        return params
    }

    fun getParam(title: String): CostsCarTabParam {
        val tabs = CarList.get(SelectedCar.index).tabs
        var param: CostsCarTabParam? = null
        for (t in tabs)
            for (p in t.params)
                if (p.title.equals(title))
                    param = CostsCarTabParam(tabs.indexOf(t), t.params.indexOf(p), p)
        return param!!
    }

    fun getParamStringList() = getParamList().map { it -> it.param.title }
    fun getCostsRecyclerViewAdpater(context: Context) = CostsRecyclerViewAdpater(context, CarList.get(SelectedCar.index).costs)
    fun getFilteredCostsRecyclerViewAdapter(context: Context, date: Long) = CostsRecyclerViewAdpater(context, getFilteredCostsList(date))
    fun getFilteredByMileageCostsRecyclerViewAdapter(context: Context, mileage: Int) = CostsRecyclerViewAdpater(context, getFilteredByMileageCostsList(mileage))
    fun getFilteredCostsList(date: Long) = CarList.get(SelectedCar.index).costs.filter { it -> it.date > date } as ArrayList<CarCost>
    fun getFilteredByMileageCostsList(mileage: Int) = CarList.get(SelectedCar.index).costs.filter { it -> it.mileage > mileage } as ArrayList<CarCost>
    fun getCost(position: Int) = CarList.get(SelectedCar.index).costs.get(position)
    fun getCurrentCostTitle(position: Int) = CarList.get(SelectedCar.index).costs.get(position).title

    fun getSelectedCarModel() =
            try {
                CarList.get(SelectedCar.index).model
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }

    fun getSelectedCarMileage(): Int =
            if (CarList.get(SelectedCar.index).replaceMileage > 0)
                CarList.get(SelectedCar.index).replaceMileage
            else CarList.get(SelectedCar.index).createMileage

    fun updateParam(costParam: CostsCarTabParam) {
        CarList.get(SelectedCar.index)
                .tabs.get(costParam.tabIndex)
                .params.set(costParam.paramIndex, costParam.param)
        serialize()
    }

    fun saveCost(cost: CarCost) {
        CarList.get(SelectedCar.index).costs.add(0, cost)
        serialize()
        viewState.notifyAdapter()
    }

    fun updateCost(position: Int, cost: CarCost) {
        CarList.get(SelectedCar.index).costs.set(position, cost)
        serialize()
        viewState.notifyAdapter()
    }

    fun removeCost(position: Int) {
        CarList.get(SelectedCar.index).costs.removeAt(position)
        serialize()
        viewState.notifyAdapter()
    }

    private fun serialize() {
        Serializer.serialize(Serializer.FileName.CARS)
    }

}