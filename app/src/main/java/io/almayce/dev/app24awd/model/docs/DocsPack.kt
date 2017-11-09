package io.almayce.dev.app24awd.model.docs

import java.util.*

/**
 * Created by almayce on 25.09.17.
 */
object DocsPack {

    fun getAllDocs(): ArrayList<Doc> {
        val docs = ArrayList<Doc>()
        val c = System.currentTimeMillis()

        docs.add(Doc("Водительское удостоверение", c, ""))
        docs.add(Doc("Страховка", c, ""))
        docs.add(Doc("Тех. паспорт", c, ""))

        return docs
    }
}