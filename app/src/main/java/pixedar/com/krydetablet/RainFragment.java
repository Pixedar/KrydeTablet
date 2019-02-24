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
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bar_chart_layout, container, false);
        BarChart mChart = rootView.findViewById(R.id.bar_chart);
        TextView textView = rootView.findViewById(R.id.textView2);
        fragmentBarChart.init(textView,mChart);
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

    }
    public FragmentBarChart getFragmentBarChart() {
        return fragmentBarChart;
    }
}
