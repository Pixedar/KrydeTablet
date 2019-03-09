package pixedar.com.krydetablet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;

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
    private float peakThreshold;
    private float dailyThreshold;
    private float oneHourThreshold;
    private ApparentTemperature apparentTemperature = new ApparentTemperature();
    public void setTempChart(boolean tempChart) {
        this.tempChart = tempChart;
    }

    private boolean tempChart = false;
    private Range range = new Range();
    public void setRapidChangeDetection(float peakThreshold,float oneHourThreshold, float dailyThreshold) {
        this.rapidChangeDetection = true;
        this.peakThreshold = peakThreshold;
        this.dailyThreshold = dailyThreshold;
        this.oneHourThreshold = oneHourThreshold;
    }

    private boolean rapidChangeDetection = false;

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

    @SuppressLint("ResourceAsColor")
    View init(View rootView, Context context) {
        mChart = rootView.findViewById(R.id.temp_chart2);
        ChartsSettings.setChart(mChart);
        textView = rootView.findViewById(R.id.textView2);
        ChartsSettings.setXaxis(mChart.getXAxis());
        ChartsSettings.setYaxis(mChart.getAxisLeft());
        mChart.setBackgroundColor(R.color.black);

        this.context = context;
        return rootView;
    }


    void setFiled(boolean filed) {
        this.filled = filed;
    }


    public void setListener(final WeatherDataController weatherDataController) {
        weatherDataController.setOnAutoRangeChangedListener(new WeatherDataController.OnAutoRangeChanged() {
            @Override
            public void autoRangeChanged(JSONArray feed) {
                range.setAutoRange(feed);
            }
        });
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                ArrayList<Entry> data = new ArrayList<>();
                    for (int k = (int) ((1.0f - scale) * (result[index].size() - 1)); k < result[index].size() - 1; k++) {
                      data.add(result[index].get(k));
                    }
                if (mChart.getData() == null) {
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(new ArrayList(result[index]), ContextCompat.getColor(context, R.color.outsideColor), filled);

                    if(filled){
                        lineDataSet.setDrawFilled(true);
                        lineDataSet.setFillColor(Color.rgb(30,30,30));
                    }
                    LineData lineData;
                    if (rapidChangeDetection) {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]), ChartsSettings.getMaxima2(result[index]));
                    } else {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]));
                    }
                    mChart.setData(lineData);

                        setColorfulLine();

                } else {
                    LineDataSet lineDataSet = ChartsSettings.getLineDataSet(new ArrayList(result[index]), ContextCompat.getColor(context, R.color.outsideColor), filled);
                    LineData lineData;
                    if (rapidChangeDetection) {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]), ChartsSettings.getMaxima2(result[index]));
                    } else {
                        lineData = new LineData(lineDataSet, ChartsSettings.getMaxima(result[index]));
                    }
                    mChart.getLineData().removeDataSet(0);
                    mChart.getLineData().removeDataSet(1);
                    mChart.setData(lineData);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setValues(result[index]);
                    ((LineDataSet) mChart.getData().getDataSetByIndex(1)).setValues(ChartsSettings.getMaxima(result[0]).getValues());
                        setColorfulLine();

                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                }
                lastVal = result[index].get(result[index].size() - 1).getY();
                mChart.invalidate();
                textView.setText(title + String.valueOf(lastVal) + prefix);

            }

            @Override
            public void dataUpdated(Entry[] result) {
                range.update(result);
                boolean enableRedWarining = false;
                boolean enableYellowWarining = false;
                if (rapidChangeDetection) {
                    float hj = result[index].getY() - mChart.getData().getDataSetByIndex(0).getEntryForIndex(0).getY();
                    if (Math.abs(hj) > dailyThreshold) { //12.1
                        if(Math.abs(hj) > 18.0f){
                            enableRedWarining = true;
                        }else {
                            enableYellowWarining = true;
                        }
                        textView.setTextSize(23);
                        if (hj > 0) {
                            textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix + "  (szybki wzrost w ciagu ostatnich 24h wynoszący " + String.format("%.2f", Math.abs(hj)) + " Hpa)");
                        } else {
                            textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix + "  (szybki spadek w ciagu ostatnich 24h wynoszący " + String.format("%.2f", Math.abs(hj)) + " Hpa)");
                        }
                    } else {
                        textView.setTextSize(32);
                        textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix);
                    }
                } else {
                    if(tempChart){
                        float val;
                        float hue;
                   //     for(int u =0; u < 50; u++) {
/*                            float t = (float) (Math.random() * 35);
                            float rh = (float) (Math.random() * 100);
                            float w = (float) Math.random() * 8;
                            apparentTemperature.update(t, rh, w, range);*/

                             apparentTemperature.update(result[index].getY() ,result[GetFieldName.OUTSIDE_HUM.getIndex()].getY(),result[GetFieldName.AVERAGE_WIND.getIndex()].getY(),range);
                           val = apparentTemperature.getApparentTemperature();
                       hue = apparentTemperature.getColorHue();
/*                            Log.d("DEBUG", "T!!!!!!!!!!!");
                            Log.d("DEBUG", String.valueOf(t));
                            Log.d("DEBUG", "RH!!!!!!!!!!!");
                            Log.d("DEBUG", String.valueOf(rh));
                            Log.d("DEBUG", "WIND!!!!!!!!!!!");
                            Log.d("DEBUG", String.valueOf(w));
                            Log.d("DEBUG", "VAL!!!!!!!!!!!");
                            Log.d("DEBUG", String.valueOf(val));
                            Log.d("DEBUG", "HUE!!!!!!!!!!!!");
                            Log.d("DEBUG", String.valueOf(hue));
                            Log.d("DEBUG", "END!!!!!!!!!!!!");*/
                   //     }
                        String c ="";
                        if(hue != -1){
                            c=" ⭕";
                        }
                        if(val != -100){
                             c+="("+String.valueOf(Math.round(val))+prefix+")";
                        }else {
                            c+=" ";
                        }

                        String d = title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix+c;

                        SpannableString ss1=  new SpannableString(d);
                        if(hue != -1){
                            ss1.setSpan(new ForegroundColorSpan(Color.HSVToColor(new float[]{hue, 100, 100})), d.length()- c.length(), d.length()- c.length()+2, 0);// set color
                            ss1.setSpan(new RelativeSizeSpan(1.2f), d.length()- c.length(), d.length()- c.length()+2, 0);
                        }
                        if(hue != -1) {
                            ss1.setSpan(new RelativeSizeSpan(0.52f), d.length() - c.length() + 2, d.length(), 0);
                        }else {
                            ss1.setSpan(new RelativeSizeSpan(0.52f), d.length() - c.length(), d.length(), 0);
                        }
                        textView.setText(ss1);

                /*        if(v != result[index].getY()){
                            String c ="  ("+String.valueOf(Math.round(v))+prefix+")";
                            String d = title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix+c;
                            SpannableString ss1=  new SpannableString(d);
                            ss1.setSpan(new RelativeSizeSpan(0.45f), d.length() - c.length(),d.length(), 0);
                            textView.setText(ss1);
                        }else {
                            textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix);
                            //"\uD83D\uDCA7"
                        }
                        textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix);*/
                    }else {
                        textView.setText(title + String.valueOf(Math.round(result[index].getY()*10.0f)/10.0f) + prefix);
                    }
                }
                (mChart.getData().getDataSetByIndex(0)).removeFirst();
                (mChart.getData().getDataSetByIndex(0)).addEntryOrdered(result[index]);
                mChart.moveViewToX(mChart.getData().getEntryCount());

                   setColorfulLine();
                float max = -1000;
                float maxX = 0;
                float min = 1000;
                float minX = 0;
                float peak = 0;
                float peakX = 0;
                float peakY = 0;
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
                    if (rapidChangeDetection && k > 5 && k < vals.size() - 2 &&
                        vals.get(k - 1).getX() - vals.get(k - 2).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k).getX() - vals.get(k - 1).getX() < weatherDataController.getInterval() * 2 &&
                        vals.get(k + 1).getX() - vals.get(k).getX() < weatherDataController.getInterval()) {
                            float a = Math.abs(vals.get(k - 2).getY() - vals.get(k - 1).getY());
                            float b = Math.abs(vals.get(k - 1).getY() - vals.get(k).getY());
                            float c = Math.abs(vals.get(k).getY() - vals.get(k + 1).getY());
                            float avg = ((a * 0.5f) + b + (c * 0.5f)) / 3.0f;
                            if (avg > peak) {
                                peak = avg;
                                peakX = vals.get(k).getX();
                                peakY = vals.get(k).getY();
                            }
                    }
                }
                if(rapidChangeDetection&& vals.size() > 20){
                    if(vals.get(vals.size()-1).getX() - vals.get(vals.size()-21).getX() < weatherDataController.getInterval() * 21){
                        float v = vals.get(vals.size()-1).getY() - vals.get(vals.size()-21).getY();
                        if(v > oneHourThreshold){
                            textView.setTextSize(23);
                            if(v >0){
                                textView.setText(title + String.valueOf(result[index].getY()) + prefix +"bardzo duży wzrost w ciągu ostatniej godziny");
                            }else {
                                textView.setText(title + String.valueOf(result[index].getY()) + prefix +"bardzo duży spadek w ciągu ostatniej godziny");
                            }
                            enableRedWarining = true;
                        }
                    }
                }
                boolean currentDetection = false;
                int k = vals.size() - 2;
                if (rapidChangeDetection && k > 5) {
                    if (vals.get(k - 1).getX() - vals.get(k - 2).getX() < weatherDataController.getInterval() * 2 &&
                            vals.get(k).getX() - vals.get(k - 1).getX() < weatherDataController.getInterval() * 2 &&
                            vals.get(k + 1).getX() - vals.get(k).getX() < weatherDataController.getInterval()) {
                        float a = Math.abs(vals.get(k - 2).getY() - vals.get(k - 1).getY());
                        float b = Math.abs(vals.get(k - 1).getY() - vals.get(k).getY());
                        float c = Math.abs(vals.get(k).getY() - vals.get(k + 1).getY());
                        float avg = ((a * 0.5f) + b + (c * 0.5f)) / 3.0f;
                        if (avg > peak) {
                            textView.setTextSize(23);
                            if (avg > peakThreshold * 1.122f) {
                                enableRedWarining = true;
                                textView.setText(title + String.valueOf(result[index].getY()) + prefix + " w ciągu ostanich 12 min zajerestrowano duży skok ciśnienia");
                            } else {
                                enableYellowWarining = true;
                                textView.setText(title + String.valueOf(result[index].getY()) + prefix + " w ciągu ostanich 12 min zajerestrowano słaby skok ciśnienia");
                            }
                            currentDetection = true;
                        }
                    }
                }
                Log.d("DEBUG",String.valueOf(peak));
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setX(maxX);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(0).setY(max);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setX(minX);
                (mChart.getData().getDataSetByIndex(1)).getEntryForIndex(1).setY(min);
                if (rapidChangeDetection) {
                    try {
                        if (peak > peakThreshold) {
                            (mChart.getData().getDataSetByIndex(2)).setDrawValues(true);
                            ((LineDataSet) (mChart.getData().getDataSetByIndex(2))).setDrawCircles(true);
                            (mChart.getData().getDataSetByIndex(2)).getEntryForIndex(0).setY(peakY);
                            (mChart.getData().getDataSetByIndex(2)).getEntryForIndex(0).setX(peakX);
                                if (peak > peakThreshold * 1.3f&&vals.get(vals.size()-1).getX() - peakX < 4*60*60*1000) {
                                    if(!currentDetection) {
                                        textView.setTextSize(23);
                                        textView.setText(title + String.valueOf(result[index].getY()) + prefix + "zajerestrowano bardzo duży skok ciśnienia");
                                    }
                                    enableRedWarining = true;
                                } else if(vals.get(vals.size()-1).getX() - peakX < 2*60*60*1000){
                                    if(!currentDetection) {
                                        textView.setTextSize(23);
                                        textView.setText(title + String.valueOf(result[index].getY()) + prefix + "zajerestrowano słaby skok ciśnienia");
                                    }
                                    enableYellowWarining = true;
                                }
                        } else {
                            (mChart.getData().getDataSetByIndex(2)).setDrawValues(false);
                            ((LineDataSet) (mChart.getData().getDataSetByIndex(2))).setDrawCircles(false);
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                mChart.getXAxis().setAxisMinimum((mChart.getData().getDataSetByIndex(0)).getEntryForIndex(0).getX());
                mChart.getData().notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
                textView.invalidate();
                if(enableRedWarining){
                    weatherDataController.enableWarining(true);
                }else if(enableYellowWarining) {
                    weatherDataController.enableWarining(false);
                }else{
                    weatherDataController.disableWarining();
                }
            }

        });
    }
    private  void setColorfulLine(){
        float minC = mChart.getData().getDataSetByIndex(0).getYMin();
         float maxC = mChart.getData().getDataSetByIndex(0).getYMax();
        int colors[] = new int[mChart.getData().getDataSetByIndex(0).getEntryCount()];

        for (int k = 0; k < colors.length; k++) {
           colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) ((range.getRangeByIndex(index)[1]*0.6f+minC*0.4f))* 100, (int) ((range.getRangeByIndex(index)[0]*0.6f+maxC*0.4f))* 100, 300, 0), 1f, 1});
         //   colors[k] = Color.HSVToColor(new float[]{map((int) (mChart.getData().getDataSetByIndex(0).getEntryForIndex(k).getY() * 100), (int) minC* 100, (int) maxC* 100, 290, 0), 0.8f, 1});
        }
        ((LineDataSet) mChart.getData().getDataSetByIndex(0)).setColors(colors);
    }
    private static int map(int x, int in_min, int in_max, int out_min, int out_max) {
        if ((in_max - in_min) != 0) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        } else {
            return 0;
        }
    }
}



