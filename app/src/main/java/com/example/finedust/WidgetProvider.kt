package com.example.finedust

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.finedust.presentation.main.MainActivity

class WidgetProvider: AppWidgetProvider() {
    private val MY_ACTION = "android.action.MY_ACTION"

    private fun setMyAction(context: Context?): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

    private fun addViews(context: Context?): RemoteViews {
        val views = RemoteViews(context?.packageName, R.layout.widget)
        views.setOnClickPendingIntent(R.id.widget, setMyAction(context))
        return views
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach { appWidgetIds ->
            val views: RemoteViews = addViews(context)
            appWidgetManager?.updateAppWidget(appWidgetIds, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

    }

    //    위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    //    위젯의 마지막 인스턴스가 제거될때 호출
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    //    위젯이 사용자에 의해 제거될때 호출
    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
    }
}