package pixedar.com.krydetablet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WeatherDataController {
    private ArrayList<OnDataArrivedListener> listeners = new ArrayList<>();
    private ArrayList<OnAutoRangeChanged> autoRangeListeners = new ArrayList<>();
    private ArrayList<OnMonthlyDataArrivedListener> monthlyDataListeners = new ArrayList<>();
    private RequestQueue requestQueue;
    private final static String MAIN_CHANNREL_ID = "333150";
    private final static String DAILY_MAXIMA_ID = "544874";
    private final static String TALKBACK_ID = "28333";
    private final static String AUTO_RANGE_ID = "639828";
    private final static String DAILY_MAXIMA_APIKEY = "15CNBVEG8QAQS7QM";
    private final static String TALKBACK_API_KEY = "QBRE5TVUGHQCNM88";
    private final static String AUTO_RANGE_API_KEY = "HILU2V0VU2BNNC6C";

    private String fileName = "0000";
    private Context context;
    private JSONObject channelInfo;
    private String lastDate = "";

    private final long[] interval = {2000};
    private final long[] lastEntryTime = {0};

    private JSONArray dailyFeed = new JSONArray();
    private SharedPreferences sharedPreferences;
    private boolean keepUpdating;
    private ProgressBar progressBar;
    private boolean flag = true;
    private ImageView imageView;

    WeatherDataController(Context context, ProgressBar progressBar, ImageView imageView) {
        this.progressBar = progressBar;
        this.context = context;
        this.imageView = imageView;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    void setKeepUpdating(boolean keepUpdating) {
        this.keepUpdating = keepUpdating;
    }

    public long getInterval() {
        return interval[0];
    }

    void setOnDataArrivedListener(OnDataArrivedListener listener) {
        this.listeners.add(listener);
    }

    void setOnAutoRangeChangedListener(OnAutoRangeChanged listener) {
        this.autoRangeListeners.add(listener);
    }

    void setOnMonthlyDataArrivedListener(OnMonthlyDataArrivedListener listener) {
        this.monthlyDataListeners.add(listener);
    }

    void loadData(String type, int amount) {
        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        updateFileName();
        loadWeatherDataFromServer("https://api.thingspeak.com/channels/" + MAIN_CHANNREL_ID + "/feeds.json?" + type + "=" + String.valueOf(amount));

    }

    private void updateFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd", Locale.GERMAN);
        fileName = sdf.format(Calendar.getInstance().getTime());
        lastDate = fileName;
    }

    private void keepUpdating(JSONArray feed) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        final long margin = 2000;
        long secondEntryTime = 0;
        try {
            lastEntryTime[0] = sdf.parse(feed.getJSONObject(feed.length() - 1).getString("created_at")).getTime();
            secondEntryTime = sdf.parse(feed.getJSONObject(feed.length() - 2).getString("created_at")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final long initialDelay[] = {(Calendar.getInstance().getTimeInMillis() - 3600000 - lastEntryTime[0])};
        interval[0] = lastEntryTime[0] - secondEntryTime;
        //    Log.d("DEBUG", "initialDelay=" + String.valueOf(initialDelay / 60000.0f) + " interval=" + String.valueOf((interval[0]) / 60000.0f));
        if (initialDelay[0] >= 0 && interval[0] > 0) {
            final Handler handler = new Handler();
            final java.lang.Runnable runnable = new java.lang.Runnable() {
                @Override
                public void run() {
                    loadLastEntryFromServer("https://thingspeak.com/channels/" + MAIN_CHANNREL_ID + "/feeds/last.json");
                    //             Log.d("GGG", String.valueOf(interval[0] + margin));
                    handler.postDelayed(this, interval[0] + margin);
                }
            };
            handler.postDelayed(runnable, initialDelay[0] + margin);

        } else {
            Log.d("DEBUG", "initialDelay=" + String.valueOf(initialDelay[0]) + " interval=" + String.valueOf(interval[0]));
            if (initialDelay[0] < 0 && interval[0] + initialDelay[0] > 0 && interval[0] + initialDelay[0] < 110000) {
                initialDelay[0] = interval[0] + initialDelay[0];
            } else {
                initialDelay[0] = 90000;
            }
            interval[0] = 180000;
            final Handler handler = new Handler();
            final java.lang.Runnable runnable = new java.lang.Runnable() {
                @Override
                public void run() {
                    loadLastEntryFromServer("https://thingspeak.com/channels/" + MAIN_CHANNREL_ID + "/feeds/last.json");
                    //             Log.d("GGG", String.valueOf(interval[0] + margin));
                    handler.postDelayed(this, interval[0] + margin);
                }
            };
            handler.postDelayed(runnable, initialDelay[0] + margin);
        }
        //   initProgressBar(initialDelay);
    }

    private void initProgressBar(long initialDelay) {
        final int updateInterval = 5;
        final float[] progress = {initialDelay};
        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                if (progress[0] < interval[0]) {
                    progress[0] += updateInterval;
                } else {
                    progress[0] = 0;
                }
                progressBar.setProgress((int) ((progress[0] / interval[0]) * 1000));
                handler.postDelayed(this, updateInterval);
            }
        };
        handler.postDelayed(runnable, updateInterval);
    }

    private void checkTalkBackCommand(String url) {
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                JSONObject obj = getResponse();
                try {
                    executeTalkBackCommand(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void enableWarining(boolean importatnt) {
        imageView.setAlpha(1.0f);
        if (importatnt) {
            imageView.setImageResource(R.drawable.ic_red_warning);
        } else {
            imageView.setImageResource(R.drawable.ic_yellow_warning);
        }
    }

    public void disableWarining() {
        imageView.setAlpha(0.3f);
        imageView.setImageResource(R.mipmap.ic_cr);
    }

    private void executeTalkBackCommand(JSONObject command) throws JSONException {
        switch (command.getString("command_string")) {
            case "INTERVAL_CHANGED":
                interval[0] = command.getInt("position");
                break;
        }
    }

    public void loadMonthlyRainData(final long updateInterval) {
        String url = "https://api.thingspeak.com/channels/333150/fields/8.json?days=31&sum=daily";
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry> result = new ArrayList<>();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
                    long time;
                    for (int k = 0; k < feeds.length(); k++) {
                        time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
                        try {
                            result.add(new Entry(time, (float) feeds.getJSONObject(k).getDouble("field" + String.valueOf(8))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for (OnMonthlyDataArrivedListener l : monthlyDataListeners) {
                        l.sumDataArrived(result);
                    }
                    final Handler handler = new Handler();
                    final java.lang.Runnable runnable = new java.lang.Runnable() {
                        @Override
                        public void run() {
                            loadMonthlyRainData(updateInterval);
                        }
                    };
                    handler.postDelayed(runnable, updateInterval);

                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadLastEntryFromServer(String url) {
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = getResponse();
                    Entry[] entry = new Entry[8];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
                    String str = obj.getString("created_at");
                    long time = sdf.parse(str).getTime();
                    for (int k = 0; k < 8; k++) {

                            entry[k] = new Entry(time, (float) obj.getDouble("field" + String.valueOf(k + 1)));

                    }
                    String date = str.substring(0, 10);
                    if (!date.equals(lastDate)) {
                        fileName = date;
                        saveWeatherData(fileName, dailyFeed.toString(), false);
                    }
                    lastDate = date;
                    dailyFeed.put(obj);

                    int margin = 2000;
                    if (time - lastEntryTime[0] > interval[0] + margin || time - lastEntryTime[0] < interval[0] - margin) {
                        //   Log.d("DEBUG", "poszlo");
                        //    checkTalkBackCommand("https://api.thingspeak.com/talkbacks/"+TALKBACK_ID +"/commands/execute.json?api_key="+TALKBACK_API_KEY);
                    }
                    lastEntryTime[0] = time;

                    for (OnDataArrivedListener l : listeners) {
                        l.dataUpdated(entry);
                    }

                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }

    private void loadWeatherDataFromServer(String url) {
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                    channelInfo = getResponse().getJSONObject("channel");
                    for (OnDataArrivedListener l : listeners) {
                        l.dataArrived(feed);
                    }
                    if (flag) {
                        keepUpdating(feeds);
                        flag = false;
                    }
                    //   saveWeatherData(fileName,feeds.toString(), false);

                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

            }
        });
    }


    public void loadMonthlyWeatherDataFromServer(final long updateInterval) {
        String url = "https://api.thingspeak.com/channels/333150/feeds.json?days=31&average=daily";
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                    channelInfo = getResponse().getJSONObject("channel");
                    for (OnMonthlyDataArrivedListener l : monthlyDataListeners) {
                        l.avgDataArrived(feed);
                    }

                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }

                final Handler handler = new Handler();
                final java.lang.Runnable runnable = new java.lang.Runnable() {
                    @Override
                    public void run() {
                        loadMonthlyWeatherDataFromServer(updateInterval);
                    }
                };
                handler.postDelayed(runnable, updateInterval);
            }
        });
    }


    public void getAutoRange() {
        String url = "https://api.thingspeak.com/channels/" + AUTO_RANGE_ID + "/feeds.json?api_key=" + AUTO_RANGE_API_KEY + "&results=1";
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    for (OnAutoRangeChanged l : autoRangeListeners) {
                        l.autoRangeChanged(feeds);
                    }
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }


    private void executeJsonObjectReqest(String url, final Runnable runnable) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        runnable.setResponse(response);
                        runnable.run();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        msg(error.toString());
                        error.printStackTrace();
                    }
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void saveWeatherData(String fileName, String array, boolean clear) {
        if (clear) {
            sharedPreferences
                    .edit()
                    .clear()
                    .putString(fileName, array)
                    .apply();
        } else {
            sharedPreferences
                    .edit()
                    .putString(fileName, array)
                    .apply();
        }
    }

    private ArrayList<Entry>[] readJsonArray(JSONArray feeds) throws Exception {
        ArrayList<Entry>[] result = new ArrayList[8];
        for (int k = 0; k < result.length; k++) {
            result[k] = new ArrayList<>();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        long time;
        for (int k = 0; k < feeds.length(); k++) {
            time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
            for (int j = 0; j < result.length; j++) {
                try {
                    result[j].add(new Entry(time, (float) feeds.getJSONObject(k).getDouble("field" + String.valueOf(j + 1))));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void msg(final String s) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            }
        };
        mainHandler.post(myRunnable);

    }

    public interface OnDataArrivedListener {
        void dataArrived(ArrayList<Entry>[] result);

        void dataUpdated(Entry[] result);
    }

    public interface OnAutoRangeChanged {
        void autoRangeChanged(JSONArray feed);
    }

    public interface OnMonthlyDataArrivedListener {
        void avgDataArrived(ArrayList<Entry>[] result);

        void dataUpdated(Entry[] result);

        void sumDataArrived(ArrayList<Entry> result);
    }

    private class Runnable implements java.lang.Runnable {
        private JSONObject response;

        void setResponse(JSONObject response) {
            this.response = response;
        }

        JSONObject getResponse() {
            return response;
        }

        @Override
        public void run() {
        }
    }
}
