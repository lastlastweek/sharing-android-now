package com.lastweek.sharing.temporary

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import com.elvishew.xlog.XLog
import com.lastweek.sharing.common.notification.NotificationHelper
import com.lastweek.sharing.R
import com.lastweek.sharing.common.getLog
import com.lastweek.sharing.common.isPermissionGranted
import kotlin.also
import kotlin.apply
import kotlin.jvm.java
import kotlin.let

internal class NotificationHelperImpl(context: Context) : NotificationHelper {
    private companion object {
        private const val CHANNEL_STREAMING = "com.lastweek.sharing.NOTIFICATION_CHANNEL_STREAMING"
        private const val CHANNEL_ERROR = "com.lastweek.sharing.NOTIFICATION_CHANNEL_ERROR"
    }

    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val packageName = context.packageName
    private val largeIcon by lazy(LazyThreadSafetyMode.NONE) {
        AppCompatResources.getDrawable(
            context,
            R.drawable.logo
        )?.toBitmap()
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.deleteNotificationChannel("com.lastweek.sharing.service.NOTIFICATION_CHANNEL_01")
            notificationManager.deleteNotificationChannel("com.lastweek.sharing.NOTIFICATION_CHANNEL_START_STOP")

            val streamingName = context.getString(R.string.app_notification_channel_streaming_T)
            NotificationChannel(CHANNEL_STREAMING, streamingName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }.let { notificationManager.createNotificationChannel(it) }

            val errorName = context.getString(R.string.app_notification_channel_error_T)
            NotificationChannel(CHANNEL_ERROR, errorName, NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null)
                enableLights(false)
                enableVibration(false)
                setShowBadge(false)
            }.let { notificationManager.createNotificationChannel(it) }
        }
    }

    override fun notificationPermissionGranted(context: Context): Boolean =
        (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || context.isPermissionGranted(Manifest.permission.POST_NOTIFICATIONS))
            .also { XLog.d(getLog("notificationPermissionGranted", "$it")) }

    override fun foregroundNotificationsEnabled(): Boolean = (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || (
            notificationManager.areNotificationsEnabled() &&
                    (notificationManager.getNotificationChannel(CHANNEL_STREAMING)?.importance ?: 0) > NotificationManager.IMPORTANCE_NONE
            )).also { XLog.d(getLog("foregroundNotificationsEnabled", "$it")) }

    override fun errorNotificationsEnabled(): Boolean = (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q || (
            notificationManager.areNotificationsEnabled() &&
                    (notificationManager.getNotificationChannel(CHANNEL_ERROR)?.importance ?: 0) > NotificationManager.IMPORTANCE_NONE
            )).also { XLog.d(getLog("errorNotificationsEnabled", "$it")) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getNotificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getStreamNotificationSettingsIntent(): Intent =
        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, notificationManager.getNotificationChannel(CHANNEL_STREAMING).id)

    override fun createForegroundNotification(context: Context, stopIntent: Intent): Notification {
        XLog.d(getLog("createForegroundNotification", "context: ${context::class.java.simpleName}#${context.hashCode()}"))

        return NotificationCompat.Builder(context, CHANNEL_STREAMING)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setLargeIcon(largeIcon)
            .setOngoing(true)
            .setContentTitle(context.getString(R.string.app_notification_streaming_title_T))
            .setContentText(context.getString(R.string.app_notification_streaming_content_T))
            .setSmallIcon(R.drawable.ic_notification_small_anim_24dp)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setContentIntent(PendingIntent.getActivity(context, 0, SingleActivity.getIntent(context), PendingIntent.FLAG_IMMUTABLE))
            .addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.app_notification_stop_T),
                    PendingIntent.getService(context, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                )
            )
            .also { builder ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.getNotificationChannel(CHANNEL_STREAMING)?.let { notificationChannel ->
                        builder.setSound(notificationChannel.sound)
                            .setPriority(notificationChannel.importance)
                            .setVibrate(notificationChannel.vibrationPattern)
                    }
                }
            }.build()
    }

    override fun getErrorNotification(context: Context, message: String, recoverIntent: Intent): Notification {
        XLog.d(getLog("getErrorNotification", "context: ${context::class.java.simpleName}#${context.hashCode()}, message: $message"))

        return NotificationCompat.Builder(context, CHANNEL_ERROR)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(Notification.CATEGORY_ERROR)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLargeIcon(largeIcon)
            .setSmallIcon(R.drawable.ic_notification_small_24dp)
            .setContentTitle(context.getString(R.string.app_error_title_T))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(PendingIntent.getActivity(context, 0, SingleActivity.getIntent(context), PendingIntent.FLAG_IMMUTABLE))
            .addAction(
                NotificationCompat.Action(
                    null,
                    context.getString(R.string.app_error_recover_T),
                    PendingIntent.getService(context, 5, recoverIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                )
            ).also { builder ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationManager.getNotificationChannel(CHANNEL_ERROR)?.let { notificationChannel ->
                        builder
                            .setSound(notificationChannel.sound)
                            .setPriority(notificationChannel.importance)
                            .setVibrate(notificationChannel.vibrationPattern)
                    }
                }
            }
            .build()
    }

    override fun showNotification(notificationId: Int, notification: Notification) {
        XLog.d(getLog("showNotification", "$notificationId"))
        notificationManager.notify(notificationId, notification)
    }

    override fun cancelNotification(notificationId: Int) {
        XLog.d(getLog("cancelNotification", "$notificationId"))
        notificationManager.cancel(notificationId)
    }
}