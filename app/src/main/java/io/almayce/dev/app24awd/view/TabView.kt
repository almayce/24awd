package io.almayce.dev.app24awd.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import io.almayce.dev.app24awd.model.CarTabParam

/**
 * Created by almayce on 27.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface TabView : MvpView{
    fun notifyDataSetChanged()
//    fun updateHistory(param: CarTabParam)
}