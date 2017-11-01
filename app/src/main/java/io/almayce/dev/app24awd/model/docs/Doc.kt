package io.almayce.dev.app24awd.model.docs

import java.io.Serializable

/**
 * Created by almayce on 30.10.17.
 */
data class Doc(var title: String,
               var endDate: Long,
               var photoPath: String) : Serializable {
}