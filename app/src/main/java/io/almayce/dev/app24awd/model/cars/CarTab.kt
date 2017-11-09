package io.almayce.dev.app24awd.model.cars

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 22.09.17.
 */

data class CarTab(var title: Str,
             var icon: Int,
             var params: ArrayList<CarTabParam>) : Serializable