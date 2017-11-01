package io.almayce.dev.app24awd.view.tabs

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Created by almayce on 27.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface TabsView : MvpView{
    fun notifyDataSetChanged()
}