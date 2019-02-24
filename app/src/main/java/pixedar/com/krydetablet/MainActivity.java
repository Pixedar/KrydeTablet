package pixedar.com.krydetablet;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

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

        wakeLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "krydeTablet:DEBUG");


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        viewPager = findViewById(R.id.pager1);

        final TabLayout tabLayout = findViewById(R.id.tab_layout1);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        final Switch s = findViewById(R.id.switch2);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        final RelativeLayout constraintLayout = findViewById(R.id.testLayout);
        final ImageView sw =  findViewById(R.id.imageView);
        final TableLayout tableLayout = findViewById(R.id.tableLayoutW);

        final RelativeLayout layout = findViewById(R.id.main_layout);
        final WeatherDataController weatherDataController = new WeatherDataController(getApplicationContext(), progressBar,sw);
        wakeLock.acquire();
 /*       ScreenController screenController = new ScreenController(wakeLock, weatherDataController);
        screenController.setDisableTime(22, 30);
        screenController.setEnableTime(5, 45);
        screenController.start(30000);
*/
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
                if (!flag2[0]) {
                    mGLView.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    sw.setVisibility(View.VISIBLE);
                    if(!flag[0]) {
                        tableLayout.setVisibility(View.VISIBLE);
                    }
                }
            }
        };


        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (s.isChecked()) {
                    flag2[0] = true;
                } else {
                    flag2[0] = false;
                    handler.postDelayed(runnable, 16000);

                }
            }
        });



        weatherDataController.setKeepUpdating(true);
        weatherDataController.loadData("days", 1);
        weatherDataController.loadMonthlyWeatherDataFromServer(24*60*60*1000);
        weatherDataController.getAutoRange();
        //  weatherDataController.loadData("results",150);

        final MainPagerAdapter adapter = new MainPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), weatherDataController);
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
        viewPager.setOffscreenPageLimit(5);



        mGLView = new WeatherCircle(this);
        mGLView.setListener(weatherDataController);
        mGLView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
               mGLView.setVisibility(View.GONE);
               constraintLayout.setVisibility(View.GONE);
               sw.setVisibility(View.GONE);
               tableLayout.setVisibility(View.GONE);
                handler.postDelayed(runnable, 20000);
                return true;
            }
        });
     //   layout.addView(mGLView);  ////////////
        initWeatherPatterns(weatherDataController,mGLView,constraintLayout,sw,tableLayout);
    }

private void initWeatherPatterns(WeatherDataController weatherDataController, final WeatherCircle weatherCircle, final RelativeLayout constraintLayout,   final ImageView sw,   final TableLayout  tableLayout){
  //  setContentView(R.layout.weather_patterns_menu);

    tableLayout.setVisibility(View.GONE);

    constraintLayout.addView(weatherCircle);
    final boolean fg[] = {true};
    sw.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!fg[0]) {
                tableLayout.setVisibility(View.GONE);
                weatherCircle.setRenderMode(true);
                flag[0] = true;
                fg[0] = true;
            } else {
                tableLayout.setVisibility(View.VISIBLE);
                weatherCircle.setRenderMode(false);
                flag[0] = false;
                fg[0] = false;
            }
        }
    });

    final TextView texts[] =  new TextView[6];

    for(int k=0; k <texts.length;k++){
        int resID = getResources().getIdentifier("text"+(char)(65+k), "id", getPackageName());
        texts[k] = findViewById(resID);
    }

    weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
        @Override
        public void dataArrived(ArrayList<Entry>[] result) {
            texts[0] .setText( " Temperatura na zewnątrz "+String.format("%.1f",result[GetFieldName.OUTSIDE_TEMP.getIndex()].get(result[GetFieldName.OUTSIDE_TEMP.getIndex()].size() - 1).getY())+"°C");
            texts[1] .setText( " Ciśniene atmosferyczne "+String.format("%.1f",result[GetFieldName.PRESSURE.getIndex()].get(result[GetFieldName.PRESSURE.getIndex()].size() - 1).getY())+" Hpa");
            texts[2] .setText( "           Wiatr " +String.format("%.1f",result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 1).getY())+" km/h");
            texts[3] .setText( "      Temperaura w domu "+String.format("%.1f",result[GetFieldName.INSIDE_TEMP.getIndex()].get(result[GetFieldName.INSIDE_TEMP.getIndex()].size() - 1).getY())+"°C");
            texts[4] .setText( "        Wilgotność w domu "+String.format("%.1f",result[GetFieldName.INSIDE_HUM.getIndex()].get(result[GetFieldName.INSIDE_HUM.getIndex()].size() - 1).getY())+"%");
            texts[5] .setText( "Wilgotność na zwenatrz "+String.format("%.0f",result[GetFieldName.OUTSIDE_HUM.getIndex()].get(result[GetFieldName.OUTSIDE_HUM.getIndex()].size() - 1).getY())+"%      ");
        }

        @Override
        public void dataUpdated(Entry[] result) {
            texts[0] .setText( " Temperatura na zewnątrz "+String.format("%.1f",result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY())+"°C");
            if(result[GetFieldName.PRESSURE.getIndex()].getY() >=1000.0f){
                texts[1] .setText( "  Ciśniene atmosferyczne "+String.format("%.0f",result[GetFieldName.PRESSURE.getIndex()].getY())+" Hpa");
            }else{
                texts[1] .setText( "  Ciśniene atmosferyczne "+String.format("%.1f",result[GetFieldName.PRESSURE.getIndex()].getY())+" Hpa");
            }
            texts[2] .setText( "           Wiatr " +String.format("%.1f",result[GetFieldName.AVERAGE_WIND.getIndex()].getY())+" km/h");
            texts[3] .setText( "      Temperaura w domu "+String.format("%.1f",result[GetFieldName.INSIDE_TEMP.getIndex()].getY())+"°C");
            texts[4] .setText( "        Wilgotność w domu "+String.format("%.1f",result[GetFieldName.INSIDE_HUM.getIndex()].getY())+"%");
            texts[5] .setText( "Wilgotność na zwenatrz "+String.format("%.0f",result[GetFieldName.OUTSIDE_HUM.getIndex()].getY())+"%      ");
        }

    });
/*    for(TextView textView:texts){
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("DEBUG","T");
                return false;
            }
        });
    }*/

}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }


}
