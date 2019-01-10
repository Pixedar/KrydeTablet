package pixedar.com.krydetablet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private WeatherDataController weatherDataController;

    // IntentController intentController;
    // public MainPagerAdapter(FragmentManager fm, int NumOfTabs, Context context,IntentController intentController) {
//        this.intentController = intentController;
    MainPagerAdapter(FragmentManager fm, int NumOfTabs, WeatherDataController weatherDataController) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.weatherDataController = weatherDataController;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                FragmentChart FragmentChart = new FragmentChart();
                FragmentChart.setListener(weatherDataController);
                FragmentChart.setIndex(0);
                FragmentChart.setPrefix(" °C");
                FragmentChart.settitle("Temperatura na zewnątrz: ");
                return FragmentChart;
            case 2:
                FragmentChart FragmentChart2 = new FragmentChart();
                FragmentChart2.setListener(weatherDataController);
                FragmentChart2.setIndex(4);
                FragmentChart2.setPrefix(" Hpa");
                FragmentChart2.settitle("Ciśnienie: ");
                return FragmentChart2;
            case 3:
                FragmentChart FragmentChart3 = new FragmentChart();
                FragmentChart3.setListener(weatherDataController);
                FragmentChart3.setIndex(6);
                FragmentChart3.setFilled(true);
                FragmentChart3.setPrefix(" kmh");
                FragmentChart3.settitle("Wiatr: ");
                return FragmentChart3;
            case 0:
                FragmentChart FragmentChart4 = new FragmentChart();
                FragmentChart4.setListener(weatherDataController);
                FragmentChart4.setIndex(1);
                FragmentChart4.setPrefix(" °C");
                FragmentChart4.settitle("Temperatura w domu: ");
                return FragmentChart4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
