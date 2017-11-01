package io.almayce.dev.app24awd.model.cars

import java.io.Serializable

/**
 * Created by almayce on 22.09.17.
 */

data class CarTab(var title: String,
             var icon: Int,
             var params: ArrayList<CarTabParam>) : Serializable {}