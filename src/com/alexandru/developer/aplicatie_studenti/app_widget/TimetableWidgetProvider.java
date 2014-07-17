package com.alexandru.developer.aplicatie_studenti.app_widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.RemoteViews;
import com.alexandru.developer.aplicatie_studenti.MyActivity;
import com.alexandru.developer.aplicatie_studenti.R;

import java.util.Calendar;

/**
 * Created by Alexandru on 5/26/14.
 * The provider for all the widgets created by the user.
 *
 * This class is some sort of broadcast receiver, receiving an intent when a widget is created
 * Also, it can receive an intent with CALENDAR_UPDATE_DAY action (at midnight) with the
 * explicit action to update all the widgets.The intent firing is scheduled with an alarm.
 *
 * The updating is made with a remote views service that creates an instance of RemoteViewsFactory
 * (an adapter for the list view)
 */
public class TimetableWidgetProvider extends AppWidgetProvider {
    public static String CLASS_NAME = "class_name";
    private final String TAG = this.getClass().getCanonicalName().toUpperCase();
    public static final String CALENDAR__DAILY_UPDATE = "com.alexandru.developer.aplicatie_studenti";

    private final int START_APP_REQUEST_CODE = 1;
    private final int UPDATE_WIDGET_REQUEST_CODE = 2;
    private final long DAY_TO_MILLIS = 24 * 3600 * 1000;

    private boolean tomorrow;

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "Widget enabled");
        super.onEnabled(context);
        scheduleUpdate(context.getApplicationContext());
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(getPendingIntentForUpdate(context));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(CALENDAR__DAILY_UPDATE)){

            final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            final ComponentName appWidget = new ComponentName(context, getClass().getName());

            final int widgetsId[] = appWidgetManager.getAppWidgetIds(appWidget);
            tomorrow = intent.getBooleanExtra("tomorrow", false);

            onUpdate(context, appWidgetManager, widgetsId);
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final Calendar calendar = Calendar.getInstance();
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        final Intent startApplication = new Intent(context, MyActivity.class);
        startApplication.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, START_APP_REQUEST_CODE,
                startApplication, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.calendar_title, pendingIntent );
        remoteViews.setOnClickPendingIntent(R.id.change_day, getChangeDayPendingIntent(context));
        for(int i = 0; i < appWidgetIds.length; i++){
            int mWidgetId = i;

            //Intent - starting the service that provides content to the list view
            //within the widget
            Intent intent = new Intent(context, ListWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
            intent.putExtra(CLASS_NAME, getClass().getName());
            intent.putExtra("tomorrow", tomorrow);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            if(tomorrow){
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                int monthOfYear = calendar.get(Calendar.MONTH);
                if(dayOfWeek != 7)
                    remoteViews.setTextViewText(R.id.day_of_week, "" + dayToString(context, dayOfWeek + 1 ));
                else
                    remoteViews.setTextViewText(R.id.day_of_week, "" + dayToString(context, 1 ));
                if(monthOfYear != 12)
                    remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH))+ " "+
                            (calendar.get(Calendar.DAY_OF_MONTH) + 1));
                else
                    remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH))+ " "+
                            1);
            }else{
                remoteViews.setTextViewText(R.id.day_of_week, "" + dayToString(context, calendar.get(Calendar.DAY_OF_WEEK) ));
                remoteViews.setTextViewText(R.id.day_of_month, monthToString(context, calendar.get(Calendar.MONTH))+ " "+
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
            remoteViews.setRemoteAdapter(mWidgetId, R.id.widget_list, intent);
            remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);

            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);

        }

    }

    private String dayToString(Context context, int day){
        String[] days = context.getResources().getStringArray(R.array.days_of_week);
        return days[day-1];
    }

    private String monthToString(Context context, int month){
        String[] months = context.getResources().getStringArray(R.array.months);
        return months[month];
    }

    private void scheduleUpdate(Context context){
        Log.d(TAG, "Widget created and alarm set!");

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();

        final PendingIntent pendingIntentForUpdate = getPendingIntentForUpdate(context);

        //Calculate midnight time in millis
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 24);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), DAY_TO_MILLIS, pendingIntentForUpdate);
    }

    private PendingIntent getPendingIntentForUpdate(Context context){
        Intent updateIntent = new Intent(context, this.getClass());
        updateIntent.setAction(CALENDAR__DAILY_UPDATE);

        return PendingIntent.getBroadcast(context, UPDATE_WIDGET_REQUEST_CODE,
                updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getChangeDayPendingIntent(Context context){
        Intent tomorrow = new Intent(context, this.getClass());
        tomorrow.setAction(CALENDAR__DAILY_UPDATE);
        if(this.tomorrow)
            //Already displaying tomorrow's courses
            tomorrow.putExtra("tomorrow", false);
        else
            tomorrow.putExtra("tomorrow", true);
        return PendingIntent.getBroadcast(context, UPDATE_WIDGET_REQUEST_CODE,
                tomorrow, PendingIntent.FLAG_UPDATE_CURRENT);
    }

}
