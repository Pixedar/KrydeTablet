package pixedar.com.krydetablet;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

public class ScreenController {
    private Calendar calendar;
    private int enableHour = 6;
    private int enableMinute = 5;
    private int disableHour = 22;
    private int disableMinute = 30;
    private WeatherDataController weatherDataController;
    private PowerManager.WakeLock wakeLock;

    ScreenController( PowerManager.WakeLock wakeLock, WeatherDataController weatherDataController) {
        calendar = Calendar.getInstance();
        this.weatherDataController = weatherDataController;
        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d("GGG", String.valueOf(calendar.getTime().toString()));
        calendar.set(Calendar.HOUR_OF_DAY, disableHour);
        calendar.set(Calendar.MINUTE, disableMinute);
        this.wakeLock = wakeLock;
    }

    void setEnableTime(int hour, int minute) {
        enableHour = hour;
        enableMinute = minute;
    }

    void setDisableTime(int hour, int minute) {
        disableHour = hour;
        disableMinute = minute;
    }

    public void start(final long updateTime) {
        wakeLock.acquire();
        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                calendar.setTimeInMillis(System.currentTimeMillis());
                if (disableHour == calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE) >= disableMinute) {
                    //  wakeLock.release();
                    weatherDataController.loadData("days", 1);
                }
                if (enableHour == calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.MINUTE) >= enableMinute) {
                    //     wakeLock.acquire();
                    weatherDataController.loadData("days", 1);
                }
                handler.postDelayed(this, updateTime);
            }
        };
        handler.postDelayed(runnable, updateTime);
    }

}
