package pixedar.com.krydetablet;

import android.graphics.Color;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

class ChartsSettings {
    static LineDataSet getLineDataSet(ArrayList<Entry> val, int color, boolean filled){
        LineDataSet lineDataSet = new LineDataSet(val, "set");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextColor(Color.BLACK);
        lineDataSet.setDrawValues(false);
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setColor(Color.WHITE);
       // lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
       // lineDataSet.setCubicIntensity(0f);

        if(filled){
            lineDataSet.setDrawFilled(true);
            lineDataSet.setLineWidth(0.0f);
            lineDataSet.setFillAlpha(255);
            lineDataSet.setFillColor(color);
            lineDataSet.setColor(Color.BLACK);
        }else{
            lineDataSet.setLineWidth(1.5f);
        }
        return lineDataSet;
    }
    static BarDataSet getBarDataSet(ArrayList<BarEntry> val, int color){
        BarDataSet barDataSet = new BarDataSet(val, "set");
      //  barDataSet.setColor(Color.WHITE);

/*        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setDrawValues(false);
        barDataSet.setHighlightEnabled(false);
        barDataSet.setColor(Color.WHITE);
        barDataSet.setColor(color);*/
        return barDataSet;
    }
    static void setColorfulLine(LineDataSet lineDataSet){
        lineDataSet.getEntryCount();
        int colors[] = new int[lineDataSet.getEntryCount()];
        for(int k =0; k< colors.length;k++ ){
            try {
                colors[k] = Color.HSVToColor(new float[]{map((int) (lineDataSet.getEntryForIndex(k).getY() * 100), (int) lineDataSet.getYMin() * 100, (int) lineDataSet.getYMax() * 100, 280, 0), 0.7f, 1});
            }catch(java.lang.ArithmeticException e){
                e.printStackTrace();
            }
        }

        lineDataSet.setColors(colors);
        lineDataSet.setLineWidth(1.3f);
        //lineDataSet.setFillAlpha(220);
    }

    static void setXaxis(XAxis x){
        //  x.setLabelCount(10, false);
        //x.setAvoidFirstLastClipping(true);
        x.setTextSize(18);
        x.setTextColor(Color.WHITE);
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        x.setAxisLineColor(Color.BLACK);
        x.setDrawGridLines(false);
        x.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long)value));
            }
        });
    }
    static void setBarXaxis(XAxis x){
          x.setLabelCount(12);
        //x.setAvoidFirstLastClipping(true);
        x.setTextSize(21);
        x.setTextColor(Color.rgb(158, 72, 18));
        x.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
       // x.setAxisLineColor(Color.rgb(158, 72, 18));
        x.setDrawGridLines(false);


       // x.setTextColor(Color.GRAY);
/*        x.setValueFormatter(new IAxisValueFormatter() {
            private SimpleDateFormat mFormat = new SimpleDateFormat("HH");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date(System.currentTimeMillis()-(long)value*180*1000));
            }
        });*/
    }
    static void setYaxis(YAxis y){
   //     y.setLabelCount(8, false);
        y.setTextSize(18);
        //y.setSpaceTop(2);
       // y.setSpaceBottom(20);
        y.setDrawZeroLine(false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        y.setEnabled(true);
    }
    static void setChart(LineChart mChart){
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setMaxVisibleValueCount(Integer.MAX_VALUE);
        mChart.setBackgroundColor(Color.BLACK);
        mChart.setAutoScaleMinMaxEnabled(true);

    }

    public static void setChart(BarChart mChart){
        mChart.setViewPortOffsets(0, 0, 0, 0);
        mChart.getDescription().setEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setTouchEnabled(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setMaxVisibleValueCount(Integer.MAX_VALUE);
        mChart.setBackgroundColor(Color.BLACK);
   //     mChart.setAutoScaleMinMaxEnabled(true);

        mChart.setDrawValueAboveBar(true);
    }
/*public static final float[] maxY = {-100};
public static final int[] maxIndex= {0};
    public static final int[] minIndex= {0};*/
    static LineDataSet getMaxima(final ArrayList<Entry> val){
        float maxX = 0;
        int index = 0;
        final float[] maxY = {-100};
        final int[] minIndex= {0};
        final int[] maxIndex= {0};
        for(Entry e: val){
            if(e.getY() > maxY[0] ){
                maxX = e.getX();
                maxY[0] = e.getY();
                maxIndex[0] =index;
            }
            index++;
        }
        float minX = 0;
        float minY = 10000;
        index = 0;
        for(Entry e: val){
            if(e.getY() < minY ){
                minX = e.getX();
                minY = e.getY();
                minIndex[0] = index;
            }
            index++;
        }
        LineDataSet lineDataSet2 =  new LineDataSet(Arrays.asList(new Entry(maxX, maxY[0]), new Entry(minX, minY)), "set2");
        lineDataSet2.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    if (value == maxY[0]) {
                        if (maxIndex[0] < 5) {
                            return String.format("       %.1f", value);
                        } else if (maxIndex[0] > val.size() - 5) {
                            return String.format("%.1f       ", value);
                        } else {
                            return String.format("%.1f", value);
                        }
                    } else {
                        if (minIndex[0] < 5) {
                            return String.format("       %.1f", value);
                        } else if (minIndex[0] > val.size() - 5) {
                            return String.format("%.1f       ", value);
                        } else {
                            return String.format("%.1f", value);
                        }
                    }
                    //  return String.format("%.1f", value);
            }
        });
        lineDataSet2.setCircleRadius(1.9f);
      //  lineDataSet2.setCircleColorHole(Color.BLACK);
        lineDataSet2.setValueTextColor(Color.WHITE);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setColor(Color.RED,0);
        lineDataSet2.setCircleColor(Color.WHITE);
        lineDataSet2.setValueTextSize(22); // prawidlowe
     //   lineDataSet2.setValueTextSize(19); /// tylko na moj tablet
        return lineDataSet2;
    }

    static LineDataSet getMaxima2(final ArrayList<Entry> val){
        LineDataSet lineDataSet2 =  new LineDataSet(Arrays.asList(new Entry(val.get(val.size()-1).getX(), val.get(val.size()-1).getY())), "set3");
        lineDataSet2.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return "             SKOK";

            }
        });
        lineDataSet2.setCircleRadius(2.7f);
        ///lineDataSet2.setCircleColorHole(Color.BLACK);

        lineDataSet2.setValueTextColor(Color.WHITE);
        lineDataSet2.setDrawValues(true);
        lineDataSet2.setColor(Color.RED,0);
        lineDataSet2.setCircleColor(Color.RED);
    //    lineDataSet2.setValueTextSize(22); // prawidlowe
        lineDataSet2.setValueTextSize(19); /// tylko na moj tablet
        lineDataSet2.setDrawValues(false);
        lineDataSet2.setDrawCircles(false);
        return lineDataSet2;
    }

    private static int map(int x, int in_min, int in_max, int out_min, int out_max)
    {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
