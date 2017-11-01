package io.almayce.dev.app24awd.model.cars

import java.io.Serializable

/**
 * Created by almayce on 05.10.17.
 */
data class CarCost(var title: String,
                   var mileage: Int,
                   var date: Long,
                   var limit: Int,
                   var price: Int,
                   var comment: String) : Serializable