package io.almayce.dev.app24awd.model.cars

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 22.09.17.
 */

data class CarTabParam(var title: Str,
                       var photoPath: Str,
                       var replaceDate: Long,
                       var replaceMileage:Int,
                       var replaceCost: Int,
                       var replaceLimit: Int
                  ) : Serializable