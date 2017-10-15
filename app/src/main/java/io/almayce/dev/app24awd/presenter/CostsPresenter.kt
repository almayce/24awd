package io.almayce.dev.app24awd.presenter

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.almayce.dev.app24awd.global.Serializer
import io.almayce.dev.app24awd.adapter.CostsRecyclerViewAdpater
import io.almayce.dev.app24awd.model.*
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
            for (p in t.params) {
                params.add(CostsCarTabParam(tabs.indexOf(t),
                        t.params.indexOf(p),
                        p))
            }
        return params
    }

    fun getParam(title: String) : CostsCarTabParam {
        val tabs = CarList.get(SelectedCar.index).tabs
        var param: CostsCarTabParam? = null
        for (t in tabs)
            for (p in t.params) {
                if (p.title.equals(title))
                    param = CostsCarTabParam(tabs.indexOf(t),
                            t.params.indexOf(p),
                            p)
            }
        return param!!
    }

    fun getParamStringList(): ArrayList<String> {
        val target = ArrayList<String>()
        for (p in getParamList())
            target.add(p.param.title)
        return target
    }

    fun getCostsRecyclerViewAdpater(context: Context): CostsRecyclerViewAdpater =
            CostsRecyclerViewAdpater(context, CarList.get(SelectedCar.index).costs)
    fun getFilteredCostsRecyclerViewAdapter(context: Context, date: Long): CostsRecyclerViewAdpater =
            CostsRecyclerViewAdpater(context, getFilteredCostsList(date))

    fun getFilteredCostsList(date: Long): ArrayList<CarCost> {
        val list = CarList.get(SelectedCar.index).costs
        val target = ArrayList<CarCost>()
        for (c in list)
            if (c.date > date)
                target.add(c)
        return target
    }
    fun getCost(position: Int): CarCost =
            CarList.get(SelectedCar.index).costs.get(position)
    fun getSelectedCarModel() = CarList.get(SelectedCar.index).model
    fun getSelectedCarMileage() = CarList.get(SelectedCar.index).replaceMileage

    fun updateParam(costParam: CostsCarTabParam) {
        CarList.get(SelectedCar.index)
                .tabs.get(costParam.tabIndex)
                .params.set(costParam.paramIndex, costParam.param)

//        println(costParam.param.title)
//        println(costParam.param.replaceCost)
//        println(costParam.param.replaceDate)

        Serializer.serialize()
    }

    fun saveCost(cost: CarCost) {
        CarList.get(SelectedCar.index).costs.add(0, cost)
        Serializer.serialize()
        viewState.notifyAdapter()
    }

    fun updateCost(position: Int, cost: CarCost) {
        CarList.get(SelectedCar.index).costs.set(position, cost)
        Serializer.serialize()
        viewState.notifyAdapter()
    }

    fun removeCost(position: Int) {
        CarList.get(SelectedCar.index).costs.removeAt(position)
        Serializer.serialize()
        viewState.notifyAdapter()
    }

    fun getCurrentCostTitle(position: Int) = CarList.get(SelectedCar.index).costs.get(position).title
}