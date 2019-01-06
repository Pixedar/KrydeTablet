package pixedar.com.krydetablet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    final boolean[] flag = {true};
    final boolean[] flag2 = {false};
    ViewPager viewPager;

    PowerManager.WakeLock wakeLock;


    WeatherCircle mGLView;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        setContentView(R.layout.activity_main);


        wakeLock = ((PowerManager)getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        viewPager = findViewById(R.id.pager1);


        final TabLayout tabLayout = findViewById(R.id.tab_layout1);
        tabLayout.addTab(tabLayout.newTab().setText("Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Led"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("History2"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final Switch s = findViewById(R.id.switch2);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        final RelativeLayout layout = findViewById(R.id.main_layout);

        final WeatherDataController weatherDataController = new WeatherDataController(getApplicationContext(),progressBar);

   //     viewPager.setAlpha(0);
     //   s.setAlpha(0);


        ScreenController screenController = new ScreenController(this,wakeLock,weatherDataController);
        screenController.setDisableTime(22,30);
        screenController.setEnableTime(5,45);
        screenController.start(30000);

        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
/*                if(!flag2[0]) {
                    flag[0] = true;
                    viewPager.setAlpha(0);
                    s.setAlpha(0);
                }*/
            //    setContentView(mGLView);
                if(!flag2[0]) {
                    mGLView.setVisibility(View.VISIBLE);
                }
            }
        };


        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(s.isChecked()){
                    flag2[0] = true;
                }else{
                    flag2[0] = false;
                    handler.postDelayed(runnable, 16000);

                }
            }
        });


        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
/*                if(!flag2[0]) {
                    if (flag[0]) {
                        flag[0] = false;
                        viewPager.setAlpha(1);
                        s.setAlpha(1);
                        handler.postDelayed(runnable, 150000);
                    }
                }*/
              //  setContentView(mGLView);
//                layout.addView(mGLView);
    //            handler.postDelayed(runnable, 5000);

                return false;
            }
        });


        weatherDataController.setKeepUpdating(true);
        weatherDataController.loadData("days",1);
      //  weatherDataController.loadData("results",150);
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {

            }

            @Override
            public void dataUpdated(Entry[] result) {
                Log.d("GGG","updated");
/*
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
*/


              //  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
             ///   getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


/*                PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,"abc");
                wakeLock.acquire(TimeUnit.SECONDS.toMillis(5));*/

            }

            @Override
            public void dailyMaximaArrived(ArrayList<Entry[][]> result) {

            }

            @Override
            public void dataRangeChanged(int entries) {

            }
        });

        final MainPagerAdapter adapter = new MainPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(),weatherDataController);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(1);
        viewPager.setOffscreenPageLimit(4);

     //   WeatherCircle mGLView = findViewById(R.id.visualizer);

       mGLView = new WeatherCircle(this);
        mGLView.setListener(weatherDataController);
        mGLView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGLView.setVisibility(View.GONE);
                handler.postDelayed(runnable, 16000);
                return true;
            }
        });
        layout.addView(mGLView);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }


}
