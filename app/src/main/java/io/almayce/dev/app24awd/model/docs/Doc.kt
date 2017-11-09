package io.almayce.dev.app24awd.model.docs

import io.almayce.dev.app24awd.Str
import java.io.Serializable

/**
 * Created by almayce on 30.10.17.
 */
data class Doc(var title: Str,
               var endDate: Long,
               var photoPath: Str) : Serializable