package com.lastweek.sharing.common

import android.content.Context
import com.lastweek.sharing.common.analytics.StreamingAnalytics
import com.lastweek.sharing.common.analytics.StreamingAnalyticsEvent

public class AppStreamingAnalytics(@Suppress("UNUSED_PARAMETER") context: Context) : StreamingAnalytics {
    override fun logEvent(event: StreamingAnalyticsEvent): Unit = Unit
}
