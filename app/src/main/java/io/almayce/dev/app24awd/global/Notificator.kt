package io.almayce.dev.app24awd.global

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.WakefulBroadcastReceiver
import io.almayce.dev.app24awd.R

/**
 * Created by almayce on 25.09.17.
 */

class Notificator : WakefulBroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "")

        wl.acquire()

        //        Intent resultIntent = new Intent(context, MainActivity.class);

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(context, 333,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context)
        if (intent.getBooleanExtra("isTask", false) == false) {
            builder.setContentTitle("24awd")
            builder.setContentText("Пожалуйста, введите актуальный пробег своего автомобиля.")
        } else {
            builder.setContentTitle(intent.getStringExtra("title"))
            builder.setContentText(intent.getStringExtra("text"))
        }

        builder.color = ContextCompat.getColor(context, R.color.colorPrimary);
        builder.setSmallIcon(R.drawable.ic_warning_black_24dp)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setContentIntent(pendingIntent)

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

    fun setAlarm(context: Context, millis: Long, title: String, message: String) {
        val i = Intent(context, Notificator::class.java)
        i.putExtra("title", title)
        i.putExtra("text", message)
        i.putExtra("isTask", true)
        val pi = PendingIntent.getBroadcast(context, (System.currentTimeMillis()).toInt(), i, 0)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + millis, pi)
    }
}