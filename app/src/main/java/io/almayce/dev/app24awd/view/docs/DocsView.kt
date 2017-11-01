package io.almayce.dev.app24awd.view.docs

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Created by almayce on 30.10.17.
 */
@StateStrategyType(AddToEndSingleStrategy::class)
interface DocsView : MvpView {
    fun notifyDataSetChanged()
}