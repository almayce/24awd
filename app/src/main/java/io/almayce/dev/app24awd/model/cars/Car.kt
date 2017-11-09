package io.almayce.dev.app24awd.model.cars

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 22.09.17.
 */
data class Car(var model: Str,
               var vin: Str,
               var engineCapacity: Float,
               var enginePower: Int,
               var year: Int,
               var createDate: Long,
               var replaceDate: Long,
               var createMileage: Int,
               var replaceMileage: Int,
               var tabs: ArrayList<CarTab>,
               var costs: ArrayList<CarCost>) : Serializable