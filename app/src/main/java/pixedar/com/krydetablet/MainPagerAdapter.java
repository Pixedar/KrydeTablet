package pixedar.com.krydetablet;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    WeatherDataController weatherDataController;
   // IntentController intentController;
   // public MainPagerAdapter(FragmentManager fm, int NumOfTabs, Context context,IntentController intentController) {
//        this.intentController = intentController;
   public MainPagerAdapter(FragmentManager fm, int NumOfTabs, WeatherDataController weatherDataController) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

        this.weatherDataController  = weatherDataController;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 1:
                OutsideTempFragment outsideTempFragment = new OutsideTempFragment();
                outsideTempFragment.setListener(weatherDataController);
                outsideTempFragment.setIndex(0);
                outsideTempFragment.setPrefix(" °C");
                outsideTempFragment.settitle("Temperatura na zewnątrz: ");
               return outsideTempFragment;
/*            WeatherCircle weatherCircle = new WeatherCircle();
            weatherCircle.setListener(intentController);
            weatherCircle.setListener(weatherDataController);
            return  weatherCircle;*/

          //  return new HomeFragment();
            case 2:
                OutsideTempFragment outsideTempFragment2 = new OutsideTempFragment();
                outsideTempFragment2.setListener(weatherDataController);
                outsideTempFragment2.setIndex(4);
                outsideTempFragment2.setPrefix(" Hpa");
                outsideTempFragment2.settitle("Ciśnienie: ");
                return outsideTempFragment2;
            case 3:
                OutsideTempFragment outsideTempFragment3 = new OutsideTempFragment();
                outsideTempFragment3.setListener(weatherDataController);
                outsideTempFragment3.setIndex(6);
                outsideTempFragment3.setFilled(true);
                outsideTempFragment3.setPrefix(" kmh");
                outsideTempFragment3.settitle("Wiatr: ");
                return outsideTempFragment3;
            case 0:
                OutsideTempFragment outsideTempFragment4 = new OutsideTempFragment();
                outsideTempFragment4.setListener(weatherDataController);
                outsideTempFragment4.setIndex(1);
                outsideTempFragment4.setPrefix(" °C");
                outsideTempFragment4.settitle("Temperatura w domu: ");
                return outsideTempFragment4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
