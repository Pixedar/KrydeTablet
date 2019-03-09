package pixedar.com.krydetablet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;

public class RainFragment extends Fragment {
    FragmentBarChart fragmentBarChart = new FragmentBarChart();
    FragmentBarChart bar = new FragmentBarChart();
/*    public  RainFragment(){
        bar =  new FragmentBarChart(){
            @Override
            public void setListener(WeatherDataController weatherDataController){
                weatherDataController.setOnMonthlyDataArrivedListener(new WeatherDataController.OnMonthlyDataArrivedListener() {
                    @Override
                    public void avgDataArrived(ArrayList<Entry>[] result) {

                    }

                    @Override
                    public void dataUpdated(Entry[] result) {

                    }

                    @Override
                    public void sumDataArrived(ArrayList<Entry> result) {

                    }
                });
            }
        };
    }*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_chart_layout, container, false);
        BarChart mChart = rootView.findViewById(R.id.bar_chart);
        TextView textView = rootView.findViewById(R.id.textView2);
        fragmentBarChart.init(textView,mChart);

         BarChart mChart2 = rootView.findViewById(R.id.bar_chart3);
        TextView textView2 = rootView.findViewById(R.id.textView4);
        bar.init(textView2,mChart2);
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

    }
    public FragmentBarChart getFragmentBarChart() {
        return fragmentBarChart;
    }
    public FragmentBarChart getFragmentBarChart2() {
        return bar;
    }
}
