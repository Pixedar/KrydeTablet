package pixedar.com.krydetablet;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Wiktor on 2017-09-02.
 */

public class FragmentBarChart {
    private BarChart mChart;
    private int index = 0;
    private TextView textView;
    private String prefix = "";
    private String title = "";
    private boolean oneHourSumEnabled = false;
    Range range = new Range();

    public void setWindChart(boolean windChart) {
        this.windChart = windChart;
    }

    private boolean windChart = false;
    private float factor = 1;
    private float sum = 0;
    private Calendar time;
    private int h = 0;
    private int lastH = 0;

    public void setFactor(float factor) {
        this.factor = factor;
    }

    public void setOneHourSumEnabled(boolean oneHourSumEnabled) {
        this.oneHourSumEnabled = oneHourSumEnabled;
    }


    public void setIndex(int index) {
        this.index = index;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void settitle(String title) {
        this.title = title;
    }

    @SuppressLint("ResourceAsColor")
    public View init(View rootView) {
        mChart = rootView.findViewById(R.id.bar_chart);
        ChartsSettings.setChart(mChart);
        textView = rootView.findViewById(R.id.bar_chart_text);
        ChartsSettings.setBarXaxis(mChart.getXAxis());
        if (oneHourSumEnabled) {
            mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("HH:00");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 2500000));
                }
            });
            mChart.getAxisLeft().setAxisMinimum(-0.05f);
        } else if (windChart) {
            mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 2500000));
                }
            });
        } else {
            mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 70000000));
                }
            });
        }
        mChart.setBackgroundColor(R.color.black);
        mChart.setFitBars(true);
        mChart.invalidate();
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        return rootView;
    }

    @SuppressLint("ResourceAsColor")
    public void init(TextView textView, BarChart mChart) {
        this.mChart = mChart;
        this.textView = textView;
        //    mChart = rootView.findViewById(R.id.bar_chart);
        ChartsSettings.setChart(this.mChart);
        //   textView = rootView.findViewById(R.id.bar_chart_text);
        ChartsSettings.setBarXaxis(this.mChart.getXAxis());
        if (oneHourSumEnabled) {
            this.mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("HH:00");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 2500000));
                }
            });
            this.mChart.getAxisLeft().setAxisMinimum(-0.05f);


        } else if (windChart) {
            mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 240000));
                }
            });
            mChart.getAxisLeft().setAxisMinimum(-0.18f);
        } else {
            this.mChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("MM-dd");

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return mFormat.format(new Date((long) value * 70000000));
                }
            });
        }
        this.mChart.setFitBars(true);
        mChart.setBackgroundColor(R.color.black);
        this.mChart.invalidate();
        ChartsSettings.setYaxis(this.mChart.getAxisLeft());

    }


    public void setListener(WeatherDataController weatherDataController) {
        weatherDataController.setOnAutoRangeChangedListener(new WeatherDataController.OnAutoRangeChanged() {
            @Override
            public void autoRangeChanged(JSONArray feed) {
                range.setAutoRange(feed);
            }
        });
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                ArrayList<BarEntry> barResult = new ArrayList<>();
                if (oneHourSumEnabled) {
                    calcHourSum(barResult, result);
                } else {
                    for (Entry e : result[index]) {
                        barResult.add(new BarEntry(e.getX() / 240000, e.getY()*factor));
                    }
                }
                BarDataSet BarDataSet = ChartsSettings.getBarDataSet(barResult, Color.WHITE);
                if (oneHourSumEnabled) {
                    BarDataSet.setValueFormatter(new IValueFormatter() {
                        @Override
                        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                            if (value == 0) {
                                return "";
                            } else {
                                return String.format("%.1f", value) + "mm";
                            }
                        }
                    });

                }

                BarData barData = new BarData(BarDataSet);

                barData.setValueTextColor(Color.WHITE);
                barData.setValueTextSize(18);

                mChart.setData(barData);
                if (windChart) {
                    setColorfullBars();
                    barData.setDrawValues(false);
                    mChart.getAxisLeft().setAxisMinimum(-0.18f);
                }


                mChart.invalidate();


                if (oneHourSumEnabled) {
                    textView.setText(title + String.format("%.1f", sum * factor) + prefix);
                    mChart.getXAxis().setAxisMinimum((mChart.getData().getDataSetByIndex(0)).getEntryForIndex(0).getX());
                    mChart.moveViewToX(mChart.getData().getEntryCount());
                    (mChart.getData().getDataSetByIndex(0)).getEntryForIndex((mChart.getData().getDataSetByIndex(0)).getEntryCount() - 1).setY(sum * factor);
                } else {
                    textView.setText(title + String.format("%.1f", barResult.get(barResult.size() - 1).getY()) + prefix);
                }
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
                textView.invalidate();
            }

            @Override
            public void dataUpdated(Entry[] result) {
                if (oneHourSumEnabled) {
                    setHourSum(result);
                }else {
                    textView.setText(title + String.format("%.1f", result[index].getY()*factor) + prefix);
                    (mChart.getData().getDataSetByIndex(0)).removeFirst();
                    (mChart.getData().getDataSetByIndex(0)).addEntryOrdered(new BarEntry(result[index].getX() / 240000, result[index].getY()*factor));
                    mChart.moveViewToX(mChart.getData().getEntryCount());
                    mChart.getAxisLeft().setAxisMinimum(-0.18f);
                    setColorfullBars();
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                    mChart.invalidate();
                }
            }

        });
    }

    public void setMonthlyDataListener(WeatherDataController weatherDataController, final int type) {
        weatherDataController.setOnMonthlyDataArrivedListener(new WeatherDataController.OnMonthlyDataArrivedListener() {
            @Override
            public void avgDataArrived(ArrayList<Entry>[] result) {
                if (type == 1) {
                    ArrayList<BarEntry> barResult = new ArrayList<>();
                    for (Entry e : result[index]) {
                        barResult.add(new BarEntry(e.getX() / 70000000, e.getY()*factor));
                    }
                    updateMonthlyChart(barResult);
                }
            }

            @Override
            public void dataUpdated(Entry[] result) {

            }

            @Override
            public void sumDataArrived(ArrayList<Entry> result) {
                if (type == 2) {
                    ArrayList<BarEntry> barResult = new ArrayList<>();
                    for (Entry e : result) {
                        barResult.add(new BarEntry(e.getX() / 70000000, e.getY()*factor));
                    }
                    updateMonthlyChart(barResult);
                    mChart.getAxisLeft().setAxisMinimum(-0.9f);
                }
            }
        });
    }

    public void updateMonthlyChart(ArrayList<BarEntry> barResult) {
        BarDataSet BarDataSet = ChartsSettings.getBarDataSet(barResult, Color.WHITE);
        BarData barData = new BarData(BarDataSet);
        barData.setValueTextColor(Color.WHITE);
        barData.setValueTextSize(18);
        mChart.setData(barData);
        BarDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                if (value == 0) {
                    return "";
                } else {
                    return String.format("%.1f", value);
                }
            }
        });
        mChart.invalidate();
        float lastVal = barResult.get(barResult.size() - 1).getY();
        textView.setText(title + String.format("%.1f", lastVal * factor) + prefix);
    }

    private void calcHourSum(ArrayList<BarEntry> barResult, ArrayList<Entry>[] result) {
        time = Calendar.getInstance();
        for (Entry e : result[index]) {
            sum += e.getY();
            time.setTimeInMillis((long) e.getX());
            h = time.get(Calendar.HOUR_OF_DAY);
            if (h != lastH) {
                barResult.add(new BarEntry(e.getX() / 2500000, sum * factor));
                sum = 0;
            }
            lastH = h;
        }
    }

    private void setHourSum(Entry[] result) {
        textView.setText(title + String.format("%.1f", sum * factor) + prefix);
        sum += result[index].getY();
        time.setTimeInMillis((long) result[index].getX());
        h = time.get(Calendar.HOUR_OF_DAY);
        if (h != lastH) {
            (mChart.getData().getDataSetByIndex(0)).removeFirst();
            (mChart.getData().getDataSetByIndex(0)).addEntryOrdered(new BarEntry(result[index].getX() / 2500000, sum * factor));
            mChart.moveViewToX(mChart.getData().getEntryCount());
            //      float minC = mChart.getData().getDataSetByIndex(0).getYMin();
            //   float maxC = mChart.getData().getDataSetByIndex(0).getYMax();
            mChart.getXAxis().setAxisMinimum((mChart.getData().getDataSetByIndex(0)).getEntryForIndex(0).getX());

/*            int colors[] = new int[mChart.getData().getDataSetByIndex(0).getEntryCount()];
            for (int k = 0; k < colors.length; k++) {
                colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) minC * 100, (int) maxC * 100, 280, 0), 0.7f, 1});
            }
            ((BarDataSet) mChart.getData().getDataSetByIndex(0)).setColors(colors);*/


            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            textView.invalidate();
            sum = 0;
        } else {
            (mChart.getData().getDataSetByIndex(0)).getEntryForIndex((mChart.getData().getDataSetByIndex(0)).getEntryCount() - 1).setY(sum * factor);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            textView.invalidate();
        }
        lastH = h;
    }

    private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
        if ((in_max - in_min) != 0) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        } else {
            return 0;
        }
    }

    private void setColorfullBars() {
        float minC = mChart.getData().getDataSetByIndex(0).getYMin();
        float maxC = mChart.getData().getDataSetByIndex(0).getYMax();
        int colors[] = new int[mChart.getData().getDataSetByIndex(0).getEntryCount()];

        for (int k = 0; k < colors.length; k++) {
            colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) ((range.getRangeByIndex(index)[1]*factor + minC) / 2.0f) * 100, (int) ((range.getRangeByIndex(index)[0]*factor + maxC) / 2.0f) * 100, 300, 0), 0.7f, 0.8f});
            //   colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) minC* 100, (int) maxC* 100, 290, 0), 0.8f, 1});
        }
        ((BarDataSet) mChart.getData().getDataSetByIndex(0)).setColors(colors);
    }
}



