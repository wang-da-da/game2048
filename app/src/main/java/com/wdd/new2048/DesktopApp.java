package com.wdd.new2048;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class DesktopApp extends AppWidgetProvider {
    public AppWidgetManager appWidgetManager;
    public static Context context;
    public RemoteViews views;

    static class MyHandler extends android.os.Handler {
        private WeakReference<DesktopApp> widget;
        public MyHandler(WeakReference<DesktopApp> widget){
            this.widget = widget;
        }

        @Override
        public void handleMessage( Message msg) {
            //加载布局
            RemoteViews views = new RemoteViews(widget.get().context.getPackageName(),R.layout.activity_desktop);
//            views.setImageViewResource(R.id.show, R.drawable.logo);

            //将AppWidgetProvider子类实例包装成ComponentName对象
            ComponentName componentName = new ComponentName(widget.get().context,DesktopApp.class);

            //调用AppWidgetManager将remoteViews添加到ComponentName中
            widget.get().appWidgetManager.updateAppWidget(componentName,views);
            super.handleMessage(msg);
        }
    }

    private Handler handler = new MyHandler(new WeakReference<>(this));

    @Override
    public void onUpdate(Context context,AppWidgetManager appWidgetManager,int[] appWidgetIds){
        this.appWidgetManager = appWidgetManager;
        this.context = context;
        Intent intent = new Intent(context, LoginActivity.class);
        views = new RemoteViews(context.getPackageName(), R.layout.activity_desktop);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);


        views.setOnClickPendingIntent(R.id.show, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
