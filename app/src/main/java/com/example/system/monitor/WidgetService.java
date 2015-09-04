package com.example.system.monitor;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * @author ichiwa
 */
public class WidgetService extends Service {


    private MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
    private long oldReceivedBytes = 0;
    private long oldRequestBytes = 0;

    @Override
    public void onCreate() {

        this.oldReceivedBytes = TrafficStats.getMobileRxBytes();
        this.oldRequestBytes = TrafficStats.getMobileTxBytes();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            //---------------//
            // CPU情報の取得 //
            //---------------//
            CpuInfoWrapper cpuInfoWrap = new CpuInfoWrapper();
            cpuInfoWrap.LoadCpuInfo();
            int cpu = (int) (cpuInfoWrap.getCpuavail() * 100);

            //--------------------//
            // メモリー情報の取得 //
            //--------------------//
            MemoryInfoWrapper memoryInfoWrap = new MemoryInfoWrapper();
            memoryInfoWrap.LoadMemory();

            //--------------------------//
            // ネットワーク転送量の取得 //
            //--------------------------//
            long up = 0;
            long down = 0;
            if (this.oldRequestBytes < TrafficStats.getMobileTxBytes()) {
                up = TrafficStats.getMobileTxBytes() - oldRequestBytes;
                this.oldRequestBytes = TrafficStats.getMobileTxBytes();
            }

            if (this.oldReceivedBytes < TrafficStats.getMobileRxBytes()) {
                down = TrafficStats.getMobileRxBytes() - oldReceivedBytes;
                this.oldReceivedBytes = TrafficStats.getMobileRxBytes();
            }
            // 描画するビューの取得
            RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.main);
            remoteViews.setTextViewText(R.id.textCpuValue, String.valueOf(cpu) + "%");

            remoteViews.setTextViewText(
                R.id.textMemoryValue,
                String.valueOf(memoryInfoWrap.getMemoryAvail() / 1024) + "MB/" + String.valueOf(memoryInfoWrap.getMemoryTotal() / 1024) + "MB");
            remoteViews.setTextViewText(R.id.textNetworkUpValue, makeBytesString(up));
            remoteViews.setTextViewText(R.id.textNetworkDownValue, makeBytesString(down));

            // プログレスバー
            remoteViews.setProgressBar(R.id.progressCpu, 100, cpu, false);
            remoteViews.setProgressBar(R.id.progressMemory, (int) memoryInfoWrap.getMemoryTotal() / 1024, (int) memoryInfoWrap.getMemoryAvail() / 1024, false);

            ComponentName sysMoniWidgetProvider = new ComponentName(this, SysMoniWidgetProvider.class);
            AppWidgetManager appManager = AppWidgetManager.getInstance(this);
            appManager.updateAppWidget(sysMoniWidgetProvider, remoteViews);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String makeBytesString(long bytes) {
        if (bytes > 1024 && bytes < (1024 * 1024)) {
            bytes /= 1024;
            return String.valueOf(bytes) + "KB";
        }
        if (bytes > 1024) {
            bytes /= 1024;
            bytes /= 1024;
            return String.valueOf(bytes) + "MB";
        }
        return String.valueOf(bytes) + "B";
    }

}
