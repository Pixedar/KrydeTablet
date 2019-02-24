package pixedar.com.krydetablet;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wiktor on 2017-09-02.
 */

public class FragmentLineChart {
    private LineChart mChart;
    private float lastVal = -100;
    private int index = 0;
    private TextView textView;
    private String prefix = "";
    private String title = "";
    private boolean filled = false;
    private float scale = 1;
    float instThreshold = 10000;

    public void setChnagingSpeedControl(boolean chnagingSpeedControl, float instThreshold) {
        this.chnagingSpeedControl = chnagingSpeedControl;
        this.instThreshold = instThreshold;
    }

    private boolean chnagingSpeedControl = false;

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void settitle(String title) {
        this.title = title;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    private Context context;

    View init(View rootView, Context context) {
        mChart = rootView.findViewById(R.id.temp_chart2);
        ChartsSettings.setChart(mChart);
        textView = rootView.findViewById(R.id.textView2);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        this.context = context;
        return rootView;
    }


    void setFiled(boolean filed) {
        this.filled = filed;
    }


    public void setListener(final WeatherDataController weatherDataController) {
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                ArrayList<Entry> data = new ArrayList<>();
                for (int k = (int) ((1.0f - scale) * (result[index].size() - 1)); k < result[index].size() - 1; k++) {
                    data.add(result[index].get(k));
                }
                if (mChart.getData() == null) {
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(new ArrayList(result[index]), ContextCompat.getColor(context, R.color.outsideColor), filled);
                    LineData lineData;
                    if (chnagingSpeedControl) {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]), ChartsSettings.getMaxima2(result[index]));
                    } else {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]));
                    }
                    if (!filled) {
                        ChartsSettings.setColorfulLine(lineDataSet);
                    }
                    mChart.setData(lineData);
                } else {
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(new ArrayList(result[index]), ContextCompat.getColor(context, R.color.outsideColor), filled);
                    LineData lineData;
                    if (chnagingSpeedControl) {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]), ChartsSettings.getMaxima2(result[index]));
                    } else {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]));
                    }
                    if (!filled) {
                        ChartsSettings.setColorfulLine(lineDataSet);
                    }
                    mChart.getLineData().removeDataSet(0);
                    mChart.getLineData().removeDataSet(1);
                    mChart.setData(lineData);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setValues(result[index]);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(1)).setValues(ChartsSettings.getMaxima(result[0]).getValues());
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                }
                lastVal = result[index].get(result[index].size() - 1).getY();
                mChart.invalidate();
                textView.setText(title + String.valueOf(lastVal) + prefix);
            }

            @Override
            public void dataUpdated(Entry[] result) {
                boolean enableNotif = false;
                if (chnagingSpeedControl) {
                    float hj = result[index].getY() - mChart.getData().getDataSetByIndex(0).getEntryForIndex(0).getY();
                    if (Math.abs(hj) > 12.1f) {
                        enableNotif = true;
                        textView.setTextSize(23);
                        if (hj > 0) {
                            textView.setText(title + String.valueOf(result[index].getY()) + prefix + "  (szybki wzrost w ciagu ostatnich 24h wynoszący " + String.format("%.2f", Math.abs(hj)) + " Hpa)");
                        } else {
                            textView.setText(title + String.valueOf(result[index].getY()) + prefix + "  (szybki spadek w ciagu ostatnich 24h wynoszący " + String.format("%.2f", Math.abs(hj)) + " Hpa)");
                        }
                    } else {
                        textView.setTextSize(31);
                        textView.setText(title + String.valueOf(result[index].getY()) + prefix);
                    }
                } else {
                    textView.setText(title + String.valueOf(result[index].getY()) + prefix);
                }
                (mChart.getData().getDataSetByIndex(0)).removeFirst();
                (mChart.getData().getDataSetByIndex(0)).addEntryOrdered(result[index]);
                mChart.moveViewToX(mChart.getData().getEntryCount());

                float minC = mChart.getData().getDataSetByIndex(0).getYMin();
                float maxC = mChart.getData().getDataSetByIndex(0).getYMax();
                mChart.getXAxis().setAxisMinimum((mChart.getData().getDataSetByIndex(0)).getEntryForIndex(0).getX());

                int colors[] = new int[mChart.getData().getDataSetByIndex(0).getEntryCount()];
                for (int k = 0; k < colors.length; k++) {
                    colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) minC * 100, (int) maxC * 100, 280, 0), 0.7f, 1});
                }
                ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setColors(colors);

                float max = -1000;
                float maxX = 0;
                float min = 1000;
                float minX = 0;
                float inst = 0;
                float instX = 0;
                float instY = 0;
                List<Entry> vals = ((LineDataSet) mChart.getData().getDataSetByIndex(0)).getValues();
                for (int k = 0; k < vals.size() - 1; k++) {
                    if (vals.get(k).getY() > max) {
                        max = vals.get(k).getY();
                        maxX = vals.get(k).getX();
                    }
                    if (vals.get(k).getY() < min) {
                        min = vals.get(k).getY();
                        minX = vals.get(k).getX();
                    }
                    if (chnagingSpeedControl && k > 3 && k < vals.size() - 8 &&
                        vals.get(k - 1).getX() - vals.get(k - 2).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k).getX() - vals.get(k - 1).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k + 1).getX() - vals.get(k).getX() < weatherDataController.getInterval()) {
                            float a = Math.abs(vals.get(k - 2).getY() - vals.get(k - 1).getY());
                            float b = Math.abs(vals.get(k - 1).getY() - vals.get(k).getY());
                            float c = Math.abs(vals.get(k).getY() - vals.get(k + 1).getY());
                            float avg = ((a * 0.5f) + b + (c * 0.5f)) / 3.0f;
                            if (avg > inst) {
                                inst = avg;
                                instX = vals.get(k).getX();
                                instY = vals.get(k).getY();
                            }
                    }
                }
                int k = vals.size() - 2;
                if (chnagingSpeedControl && k > 3 && k < vals.size() - 8 &&
                        vals.get(k - 1).getX() - vals.get(k - 2).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k).getX() - vals.get(k - 1).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k + 1).getX() - vals.get(k).getX() < weatherDataController.getInterval()) {
                    float a = Math.abs(vals.get(k - 2).getY() - vals.get(k - 1).getY());
                    float b = Math.abs(vals.get(k - 1).getY() - vals.get(k).getY());
                    float c = Math.abs(vals.get(k).getY() - vals.get(k + 1).getY());
                    float avg = ((a * 0.5f) + b + (c * 0.5f)) / 3.0f;
                    if (avg > inst) {
                        textView.setText(title + String.valueOf(result[index].getY()) + prefix +" w ciągu ostanich 20 min zajerestrowano duży skok ciśnienia");
                        if(avg > instThreshold*1.28f){
                            enableNotif = true;
                        }
                    }
                }

                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setX(maxX);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setY(max);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setX(minX);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setY(min);
                if (chnagingSpeedControl) {
                    try {
                        if (inst > instThreshold) {
                            (mChart.getData().getDataSetByIndex(2)).setDrawValues(true);
                            ((LineDataSet) (mChart.getData().getDataSetByIndex(2))).setDrawCircles(true);
                            (mChart.getData().getDataSetByIndex(2)).getEntryForIndex(0).setY(instY);
                            (mChart.getData().getDataSetByIndex(2)).getEntryForIndex(0).setX(instX);
                            if(inst > instThreshold*1.28f){
                                enableNotif = true;
                            }
                        } else {
                            (mChart.getData().getDataSetByIndex(2)).setDrawValues(false);
                            ((LineDataSet) (mChart.getData().getDataSetByIndex(2))).setDrawCircles(false);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
                textView.invalidate();
                if(enableNotif){
                    weatherDataController.enableWarining();
                }else {
                    weatherDataController.disableWarining();
                }
            }

        });
    }

    private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
        if ((in_max - in_min) != 0) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        } else {
            return 0;
        }
    }
}



