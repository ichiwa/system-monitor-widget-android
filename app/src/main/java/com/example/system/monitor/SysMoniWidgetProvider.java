/**
 *
 */
package com.example.system.monitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * @author ichiwa
 *
 */
public class SysMoniWidgetProvider extends AppWidgetProvider {

	private static final String ACTION_START_ORIGINAL = SysMoniWidgetProvider.class.getSimpleName();

	/**
	 * ウィジェット初期化処理
	 */
	@Override
	public void onEnabled(Context context)
	{
		// デバッガ有効
		android.os.Debug.waitForDebugger();
		super.onEnabled(context);
	}
	/**
	 * AlermManagerの初期化処理(updatePeriodMillis=0)
 	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		startService(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		super.onReceive(context, intent);

		if (intent.getAction().equals(ACTION_START_ORIGINAL))
		{
			Intent serviceIntent = new Intent(context, WidgetService.class);
			context.startService(serviceIntent);
			startService(context);
		}
	}

	/**
	 * サービススタート
	 * @param context
	 */
	private void startService(Context context)
	{
		Intent intent = new Intent(context, SysMoniWidgetProvider.class);
		intent.setAction(ACTION_START_ORIGINAL);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		long now = System.currentTimeMillis();

		// 一秒後に設定する
		long next = now + 1000;
		alarmManager.set(AlarmManager.RTC, next, pendingIntent);
	}
}
