package io.almayce.dev.app24awd.model.cars

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 05.10.17.
 */
data class CarCost(var title: Str,
                   var mileage: Int,
                   var date: Long,
                   var limit: Int,
                   var price: Int,
                   var comment: Str) : Serializable