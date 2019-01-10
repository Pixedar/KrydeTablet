package pixedar.com.krydetablet;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

/**
 * Created by Wiktor on 2017-09-02.
 */

public class FragmentChart extends Fragment{
    private LineChart mChart;
    private BarChart barChart;
    private final float[]maxima ={0,1};
    // private List<Entry> val;
    private float lastVal = -100;
    private int index = 0;
    private TextView textView;
    private String prefix = "";
    private String title ="";
    private boolean filled =false;
    private int size = 10;
    public void setIndex(int index){
        this.index = index;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public void settitle(String title){
        this.title = title;
    }
    public void setFilled(boolean filled){
        this.filled = filled;
    }
    public void setBarChart(){
        barChart.setVisibility(View.VISIBLE);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.temp_chart2, container, false);

        mChart = rootView.findViewById(R.id.temp_chart2);
        barChart = rootView.findViewById(R.id.bar_chart);
        ChartsSettings.setChart(mChart);
        textView = rootView.findViewById(R.id.textView2);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());

        return rootView;
    }
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
/*        if (visible&& textView!= null) {
            textView.setText(String.valueOf(lastVal)+prefix);
        }*/
    }
    void setFiled(boolean filed){
        this.filled = filed;
    }

    public void setListener(WeatherDataController weatherDataController){
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                size = result.length;
                if (mChart.getData() == null){  ////////////////
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[index], ContextCompat.getColor(getContext(), R.color.outsideColor),filled);

                    LineData lineData = new LineData(lineDataSet,ChartsSettings.getMaxima(result[index]));
                    if(!filled) {
                        ChartsSettings.setColorfulLine(lineDataSet);
                    }
                 /*   maxima[0] = lineData.getXMin();
                    maxima[1] = lineData.getXMax();*/
                    mChart.setData(lineData);
                }else{
                    //        Log.d("GGG","exec");
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(result[index], ContextCompat.getColor(getContext(), R.color.outsideColor),filled);
                    LineData lineData = new LineData(lineDataSet,ChartsSettings.getMaxima(result[index]));
                    if(!filled) {
                        ChartsSettings.setColorfulLine(lineDataSet);
                    }

                    mChart.getLineData().removeDataSet(0);
                    mChart.getLineData().removeDataSet(1);
                    mChart.setData(lineData);


       /*             ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setValues(result[index]);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(1)).setValues(ChartsSettings.getMaxima(result[0]).getValues());
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();*/
                }
                lastVal = result[index].get(result[index].size()-1).getY();
                mChart.invalidate();
                textView.setText(title+String.valueOf(lastVal)+prefix);
            }

            @Override
            public void dataUpdated(Entry[] result) {

                //   if (result[index].getY() != lastVal) {
                //       lastVal = result[index].getY();
                textView.setText(title + String.valueOf(result[index].getY()) + prefix);

                (mChart.getData().getDataSetByIndex(0)).removeEntry(0);
                (mChart.getData().getDataSetByIndex(0)).addEntry(result[index]);
                mChart.moveViewToX(mChart.getData().getEntryCount());

                int colors[] = new int[mChart.getData().getDataSetByIndex(0).getEntryCount()];
                for (int k = 0; k < colors.length; k++) {
                    colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) mChart.getData().getDataSetByIndex(0).getYMin() * 100, (int) mChart.getData().getDataSetByIndex(0).getYMax() * 100, 280, 0), 0.7f, 1});
                }

                ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setColors(colors);

                if (lastVal > (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).getY()) {
                    (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setX(result[index].getX());
                    (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setY(lastVal);
                    //    ChartsSettings.maxY[0] = lastVal;
                    //    ChartsSettings.maxIndex[0] = size;
                }
                if (lastVal < (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).getY()) {
                    (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setX(result[index].getX());
                    (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setY(lastVal);
                    //   ChartsSettings.minIndex[0] = size;
                }

                mChart.getData().notifyDataChanged();
                //   mChart.notifyDataSetChanged();
                mChart.invalidate();
                textView.invalidate();

                // }

            }

            @Override
            public void dailyMaximaArrived(ArrayList<Entry[][]> result) {

            }

            @Override
            public void dataRangeChanged(int entries) {

            }
        });
    }
    private static int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}



