package com.firstapp.ttangfinedust;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import android.widget.RemoteViews;


/**
 * Implementation of App Widget functionality.
 */
public class FinedustWidget extends AppWidgetProvider { //AppWidgetProvider 를 통해 위젯의 상태 변화에 따라 호출되는 다양한 콜백함수 작동방법을 적을 수 있다.

    private static final int WIDGET_UPDATE_INTERVAL =340000; //15min
    private static PendingIntent mSender;
    private static AlarmManager mManager;
    private static final String ACTION_BTN = "ButtonClick";
    private static RemoteViews views;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        String action = intent.getAction();
        // 위젯 업데이트 인텐트를 수신했을 때

        if(action.equals("android.appwidget.action.APPWIDGET_UPDATE"))
        {
            removePreviousAlarm();

            long firstTime = System.currentTimeMillis() + WIDGET_UPDATE_INTERVAL;
            mSender = PendingIntent.getBroadcast(context, 0, intent, 0);
            mManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            mManager.set(AlarmManager.RTC, firstTime, mSender);
        }
        // 위젯 제거 인텐트를 수신했을 때
        else if(action.equals("android.appwidget.action.APPWIDGET_DISABLED"))
        {

            removePreviousAlarm();
        }
    }
    public void removePreviousAlarm()
    {
        if(mManager != null && mSender != null)
        {
            mSender.cancel();
            mManager.cancel(mSender);
        }
    }
    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, //위젯의 크기 및 옵션이 변경될 때마다 호출되는 함수
                         int appWidgetId) {

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.finedust_widget); //RemoteViews 위젯 업데이트를 의뢰할 때 의뢰내용을 저장하는 클래스
        new GetDatawidget(context,appWidgetManager,appWidgetId,views);

        Intent intent = new Intent(context,loadingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.main_background_widget,pendingIntent);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) { //위젯이 설치될 때마다 호출되는 함수
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) { //앱 위젯이 최초로 설치되는 순간 호출되는 함수
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) { //위젯이 제거되는 순간 호출되는 함수
        // Enter relevant functionality for when the last widget is disabled
    }
}

