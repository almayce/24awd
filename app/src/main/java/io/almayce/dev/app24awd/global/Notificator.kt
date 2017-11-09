package io.almayce.dev.app24awd.global

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.WakefulBroadcastReceiver
import io.almayce.dev.app24awd.R
import io.almayce.dev.app24awd.Str

/**
 * Created by almayce on 25.09.17.
 */

class Notificator : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")

        wl.acquire(20000)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://24awd.com/"))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 333,
                browserIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context)
        with(builder) {
            if (!intent.getBooleanExtra("isTask", false)) {
                setContentTitle("24awd")
                setContentText("Пожалуйста, введите актуальный пробег своего автомобиля.")
            } else {
                setContentTitle(intent.getStringExtra("title"))
                setContentText(intent.getStringExtra("text"))
            }

            color = ContextCompat.getColor(context, R.color.colorPrimary)
            setSmallIcon(R.drawable.ic_warning_black_24dp)
            setDefaults(Notification.DEFAULT_ALL)
            setContentIntent(pendingIntent)
        }

        val notification = builder.build()
        notification.flags = notification.flags or Notification.FLAG_ONLY_ALERT_ONCE
        val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(333, notification)

        wl.release()
    }

    fun startAlarm(context: Context) {
        val i = Intent(context, Notificator::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, i, 0)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi)
    }

    fun setAlarm(context: Context, millis: Long, title: Str, message: Str) {
        val i = Intent(context, Notificator::class.java)
        with(i) {
            putExtra("title", title)
            putExtra("text", message)
            putExtra("isTask", true)
        }
        val pi = PendingIntent.getBroadcast(context, (System.currentTimeMillis()).toInt(), i, 0)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, pi)
    }
}