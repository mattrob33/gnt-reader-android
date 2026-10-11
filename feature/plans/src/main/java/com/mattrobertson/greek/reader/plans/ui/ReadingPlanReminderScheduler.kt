package com.mattrobertson.greek.reader.plans.ui

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.mattrobertson.greek.reader.plans.ReadingPlans
import java.util.Calendar

object ReadingPlanReminderScheduler {
    private const val ACTION_REMIND = "com.mattrobertson.greek.reader.plans.REMIND"
    private const val CHANNEL_ID = "reading_plan_daily"

    fun schedule(context: Context, planIndex: Int, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
        if (!prefs.getBoolean(reminderEnabledKey(planIndex), false) || prefs.getInt("plan-$planIndex-day", -1) !in ReadingPlans.all[planIndex].days.indices) return
        val intent = Intent(context, ReadingPlanReminderReceiver::class.java).setAction(ACTION_REMIND).putExtra("plan_index", planIndex)
        val pending = PendingIntent.getBroadcast(context, planIndex, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val now = Calendar.getInstance()
        val next = (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            if (!after(now)) add(Calendar.DAY_OF_YEAR, 1)
        }
        val alarms = context.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= 31 && !alarms.canScheduleExactAlarms()) {
            alarms.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.timeInMillis, pending)
        } else if (Build.VERSION.SDK_INT >= 23) {
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.timeInMillis, pending)
        } else {
            alarms.setExact(AlarmManager.RTC_WAKEUP, next.timeInMillis, pending)
        }
    }

    fun cancel(context: Context, planIndex: Int) {
        val intent = Intent(context, ReadingPlanReminderReceiver::class.java).setAction(ACTION_REMIND)
        PendingIntent.getBroadcast(context, planIndex, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)?.let {
            context.getSystemService(AlarmManager::class.java).cancel(it)
            it.cancel()
        }
    }

    internal fun fire(context: Context, planIndex: Int) {
        if (planIndex !in ReadingPlans.all.indices) return
        val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
        val progress = prefs.getInt("plan-$planIndex-day", -1)
        if (!prefs.getBoolean(reminderEnabledKey(planIndex), false) || progress !in ReadingPlans.all[planIndex].days.indices) return
        if (Build.VERSION.SDK_INT >= 26) {
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Reading plan reminders", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
        val openApp = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            putExtra("open_reading_plan_index", planIndex)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = openApp?.let {
            PendingIntent.getActivity(context, 1000 + planIndex, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(context.applicationInfo.icon)
            .setContentTitle("Today's Greek reading is ready")
            .setContentText("${ReadingPlans.all[planIndex].title} · Day ${progress + 1}")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(1000 + planIndex, notification)
        schedule(context, planIndex, prefs.getInt(reminderHourKey(planIndex), 9), prefs.getInt(reminderMinuteKey(planIndex), 0))
    }
}

class ReadingPlanReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "android.intent.action.TIME_SET" || intent.action == "android.intent.action.TIMEZONE_CHANGED") {
            val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
            ReadingPlans.all.indices.forEach { index ->
                if (prefs.getBoolean(reminderEnabledKey(index), false)) {
                    ReadingPlanReminderScheduler.schedule(context, index, prefs.getInt(reminderHourKey(index), 9), prefs.getInt(reminderMinuteKey(index), 0))
                }
            }
        } else {
            ReadingPlanReminderScheduler.fire(context, intent.getIntExtra("plan_index", -1))
        }
    }
}
