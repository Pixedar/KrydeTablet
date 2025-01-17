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
                pixedar.com.krydetablet.Fragment fragment =  new pixedar.com.krydetablet.Fragment();
                fragment.getFragmentLineChart().setListener(weatherDataController);
                fragment.getFragmentLineChart().setIndex(0);
                fragment.getFragmentLineChart().setPrefix(" °C");
                fragment.getFragmentLineChart().setTempChart(true);
                fragment.getFragmentLineChart().settitle("Temperatura na zewnątrz: ");

                fragment.getFragmentBarChart().setMonthlyDataListener(weatherDataController,1);
                fragment.getFragmentBarChart().setIndex(0);
                fragment.getFragmentBarChart().setPrefix(" °C");
                fragment.getFragmentBarChart().settitle("Temperatura na zewnątrz: ");
                return fragment;
            case 2:
                pixedar.com.krydetablet.Fragment  fragmentLineChart2 = new pixedar.com.krydetablet.Fragment();
                fragmentLineChart2.getFragmentLineChart().setListener(weatherDataController);
                fragmentLineChart2.getFragmentLineChart().setIndex(4);
                fragmentLineChart2.getFragmentLineChart().setPrefix(" Hpa");
                fragmentLineChart2.getFragmentLineChart().settitle("Ciśnienie: ");
                fragmentLineChart2.getFragmentLineChart().setRapidChangeDetection(0.108f,1.95f,12.7f);

                fragmentLineChart2.getFragmentBarChart().setMonthlyDataListener(weatherDataController,1);
                fragmentLineChart2.getFragmentBarChart().setIndex(4);
                fragmentLineChart2.getFragmentBarChart().setPrefix(" Hpa");
                fragmentLineChart2.getFragmentBarChart().settitle("Ciśnienie: ");
                return fragmentLineChart2;
            case 4:
                RainFragment fragmentLineChart3 = new RainFragment();
                fragmentLineChart3.getFragmentBarChart().setListener(weatherDataController);
                fragmentLineChart3.getFragmentBarChart().setIndex(7);
                fragmentLineChart3.getFragmentBarChart().setFactor(0.329f);
            //    fragmentLineChart3.setFilled(true);
                fragmentLineChart3.getFragmentBarChart().setPrefix(" mm/h");
                fragmentLineChart3.getFragmentBarChart().setOneHourSumEnabled(true);
                fragmentLineChart3.getFragmentBarChart().settitle("Deszcz: ");

                fragmentLineChart3.getFragmentBarChart2().setMonthlyDataListener(weatherDataController,2);

                return fragmentLineChart3;
            case 3:
/*                pixedar.com.krydetablet.Fragment fragmentLineChart5 = new pixedar.com.krydetablet.Fragment();
                fragmentLineChart5.getFragmentLineChart().setListener(weatherDataController);
                fragmentLineChart5.getFragmentLineChart().setIndex(5);
                fragmentLineChart5.getFragmentLineChart().setPrefix(" kmh");
                fragmentLineChart5.getFragmentLineChart().settitle("Wiatr: ");
                fragmentLineChart5.getFragmentLineChart().setFilled(true);

                fragmentLineChart5.getFragmentBarChart().setMonthlyDataListener(weatherDataController,1);
                fragmentLineChart5.getFragmentBarChart().setIndex(5);
                fragmentLineChart5.getFragmentBarChart().setPrefix(" kmh");
                fragmentLineChart5.getFragmentBarChart().settitle("Wiatr: ");*/

                RainFragment fragmentLineChart5 = new RainFragment();
                fragmentLineChart5.getFragmentBarChart().setListener(weatherDataController);
                fragmentLineChart5.getFragmentBarChart().setIndex(6);
                fragmentLineChart5.getFragmentBarChart().setPrefix(" kmh");
                fragmentLineChart5.getFragmentBarChart().settitle("Wiatr: ");
                fragmentLineChart5.getFragmentBarChart().setWindChart(true);
                fragmentLineChart5.getFragmentBarChart().setFactor(3.44808949784f);

                fragmentLineChart5.getFragmentBarChart2().setMonthlyDataListener(weatherDataController,1);
                fragmentLineChart5.getFragmentBarChart2().setIndex(6);
                fragmentLineChart5.getFragmentBarChart2().setPrefix(" kmh");
                fragmentLineChart5.getFragmentBarChart2().settitle("Wiatr: ");
                fragmentLineChart5.getFragmentBarChart2().setFactor(3.44808949784f);
                return fragmentLineChart5;
            case 0:
                pixedar.com.krydetablet.Fragment fragmentLineChart4 = new pixedar.com.krydetablet.Fragment();
                fragmentLineChart4.getFragmentLineChart().setListener(weatherDataController);
                fragmentLineChart4.getFragmentLineChart().setIndex(1);
                fragmentLineChart4.getFragmentLineChart().setPrefix(" °C");
                fragmentLineChart4.getFragmentLineChart().settitle("Temperatura w domu: ");

                fragmentLineChart4.getFragmentBarChart().setMonthlyDataListener(weatherDataController,1);
                fragmentLineChart4.getFragmentBarChart().setIndex(1);
                fragmentLineChart4.getFragmentBarChart().setPrefix(" °C");
                fragmentLineChart4.getFragmentBarChart().settitle("Temperatura w domu: ");
                return fragmentLineChart4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
