package io.almayce.dev.app24awd.model

import android.content.Context
import io.almayce.dev.app24awd.R
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 25.09.17.
 */
object CarTabPack {

    fun getAllTabs(): ArrayList<CarTab> {
        var tabs = ArrayList<CarTab>()
        var c = System.currentTimeMillis()

        tabs.add(CarTab("Масло", R.drawable.ic_oil,
                arrayListOf(CarTabParam("Моторное масло", c, 0, 0, 10000),
                        CarTabParam("Трансмиссионное масло", c, 0,  0, 50000),
                        CarTabParam("Масло АКПП", c, 0, 0,  60000),
                        CarTabParam("Редукторное масло", c, 0, 0,  60000),
                        CarTabParam("Масло вискомуфты", c, 0,  0, 60000)
                )))

        tabs.add(CarTab("Фильтры", R.drawable.ic_filters,
                arrayListOf(CarTabParam("Фильтр масляный", c, 0,  0, 10000),
                        CarTabParam("Фильтр воздушный", c, 0,  0, 10000),
                        CarTabParam("Фильтр салона", c,  0, 0, 10000)
                )))

        tabs.add(CarTab("Тормоза", R.drawable.ic_breaks,
                arrayListOf(CarTabParam("Колодки передние", c,  0, 0, 25000),
                        CarTabParam("Диски передние", c, 0,  0, 60000),
                        CarTabParam("Колодки задние", c, 0, 0,  25000),
                        CarTabParam("Диски / барабаны задние", c, 0,  0, 60000),
                        CarTabParam("Колодки ручного тормоза", c, 0,  0, 60000)
                )))

        tabs.add(CarTab("Ремни", R.drawable.ic_belts,
                arrayListOf(CarTabParam("Ремень / цепь ГРМ", c, 0, 0, 80000),
                        CarTabParam("Ремень генератора", c, 0,  0, 80000)
                )))

        tabs.add(CarTab("Тех. жидкости", R.drawable.ic_flu,
                arrayListOf(CarTabParam("Антифриз", c, 0, 0, 30000),
                        CarTabParam("Тормозная жидкость", c,  0, 0, 50000),
                        CarTabParam("Жидкость ГУР", c, 0, 0, 50000)
                )))

        tabs.add(CarTab("Запчасти", R.drawable.ic_parts, arrayListOf()
        ))
        return tabs
    }
}