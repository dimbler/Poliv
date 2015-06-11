package com.example.dimbler.webview;

/**
 * Created by dimbler on 11.06.2015.
 */
        import android.app.AlarmManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.os.PowerManager;
        import android.os.SystemClock;
        import android.util.Log;
        import android.widget.Toast;

public class Alarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        String access_token = intent.getStringExtra("access_token");
        if (access_token != null) {
            new ExecNasos().execute(access_token, "false");
            Log.d("JSON", "Nasos has been stopped " + intent.getStringExtra("access_token"));
            //MainActivity.nasos_Button.setChecked(false);
            //MainActivity.np.setValue(0);
            Toast.makeText(context, R.string.nasos_stopped, Toast.LENGTH_LONG).show(); // For example
        }else {
            Log.d("JSON", "Not stopped " + intent.getStringExtra("access_token"));
            Toast.makeText(context, R.string.nasos_not_stopped, Toast.LENGTH_LONG).show();
        }
        wl.release();
    }

    public void SetAlarm(Context context, int alarmTime, String access_token)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        i.putExtra("access_token", access_token);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 60 * alarmTime, pi); // Millisec * Second * Minute
    }

    public void CancelAlarm(Context context)
    {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
