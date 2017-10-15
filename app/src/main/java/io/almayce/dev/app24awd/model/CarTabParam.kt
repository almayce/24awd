package io.almayce.dev.app24awd.model

import java.io.Serializable

/**
 * Created by almayce on 22.09.17.
 */

data class CarTabParam(var title: String,
                       var replaceDate: Long,
                       var replaceMileage:Int,
                       var replaceCost: Int,
                       var replaceLimit: Int
                  ) : Serializable {}