package pixedar.com.krydetablet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fragment  extends android.support.v4.app.Fragment {
    FragmentBarChart fragmentBarChart = new FragmentBarChart();
    FragmentLineChart fragmentLineChart = new FragmentLineChart();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.temp_chart2, container, false);
        fragmentBarChart.init(rootView);
        fragmentLineChart.init(rootView,getContext());
        return rootView;
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);

    }
    public FragmentLineChart getFragmentLineChart() {
        return fragmentLineChart;
    }
    public FragmentBarChart getFragmentBarChart() {
        return fragmentBarChart;
    }
}
