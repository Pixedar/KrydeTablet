package pixedar.com.krydetablet;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import java.util.Calendar;

public class ScreenController  {
    private boolean[] flag = {true};
    private Calendar calendar;
    int enableHour = 6;
    int enableMinute = 5;
    int disableHour = 22;
    int disableMinute = 30;
    WeatherDataController weatherDataController;
    PowerManager.WakeLock wakeLock;
    public ScreenController(Context context, PowerManager.WakeLock wakeLock,WeatherDataController weatherDataController){
        calendar = Calendar.getInstance();

        this.weatherDataController = weatherDataController;


        calendar.setTimeInMillis(System.currentTimeMillis());
        Log.d("GGG",String.valueOf(calendar.getTime().toString()));
        calendar.set(Calendar.HOUR_OF_DAY, disableHour);
        calendar.set(Calendar.MINUTE, disableMinute);
        this.wakeLock = wakeLock;
    }
    public void setEnableTime(int hour, int minute){
        enableHour = hour;
        enableMinute = minute;
    }
    public void setDisableTime(int hour, int minute){
        disableHour = hour;
        disableMinute = minute;
    }
    public void start(final long updateTime){
        wakeLock.acquire();
        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                calendar.setTimeInMillis(System.currentTimeMillis());
                if(disableHour ==calendar.get(Calendar.HOUR_OF_DAY)&&calendar.get(Calendar.MINUTE)>=disableMinute){
                  //  wakeLock.release();
                    weatherDataController.loadData("days",1);
                }
                if(enableHour ==calendar.get(Calendar.HOUR_OF_DAY)&&calendar.get(Calendar.MINUTE) >= enableMinute){
               //     wakeLock.acquire();
                    weatherDataController.loadData("days",1);
                }
                handler.postDelayed(this,updateTime);
            }
        };
        handler.postDelayed(runnable,updateTime);
    }

}
