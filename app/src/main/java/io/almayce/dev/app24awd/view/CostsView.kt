package io.almayce.dev.app24awd.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Created by almayce on 26.09.17.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface CostsView: MvpView {
    fun notifyAdapter()
//    fun updateHistory(costsParam: CostsCarTabParam)
}