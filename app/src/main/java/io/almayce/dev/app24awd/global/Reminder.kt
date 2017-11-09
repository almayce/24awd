package io.almayce.dev.app24awd.global

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit

/**
 * Created by almayce on 25.09.17.
 */
class Reminder : Service() {

    private val notificator = Notificator()

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        launch(UI) {
            checkCurrentDay()
        }
    }

    private tailrec suspend fun checkCurrentDay() {
        delay(3, TimeUnit.HOURS)
        val calendar = getInstance(TimeZone.getTimeZone("UTC"))
        with(calendar) {
            if (get(DAY_OF_WEEK) == WEDNESDAY)
                if (get(HOUR_OF_DAY) > 10)
                    notificator.startAlarm(this@Reminder)
        }

        checkCurrentDay()
    }
}