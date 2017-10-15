package io.almayce.dev.app24awd

import io.almayce.dev.app24awd.model.Car
import org.junit.Test

import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun saveSelectedCarMileage() {
        val car = Car("test", "test", 1.5F, 100,
                Car.PowerType.POWER_TYPE_HP, 2015, 400000, 500,
                (System.currentTimeMillis() - 6660006688), arrayListOf())

        val dayMileage = car.dayMileage
        val totalMileage = car.replaceMileage

        val mileageUpdate = car.mileageUpdate
        val currentMileageUpdate = System.currentTimeMillis()

        val deltaMileage = 600000 - totalMileage
        val deltaUpdate = currentMileageUpdate - mileageUpdate
        val deltaUpdateDays = TimeUnit.DAYS.convert(deltaUpdate, TimeUnit.MILLISECONDS).toInt()
        println(deltaUpdateDays)

        car.replaceMileage = 600000
        car.dayMileage = (deltaMileage / deltaUpdateDays + dayMileage) / 2
        println(car.dayMileage)
    }
}