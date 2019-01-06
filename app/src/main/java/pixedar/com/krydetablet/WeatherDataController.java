package pixedar.com.krydetablet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
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
    private RequestQueue requestQueue;
    private String channelID = "333150";
    private String dailyMaximaChannelId = "544874";
    private String dailyMaximaApiKey = "15CNBVEG8QAQS7QM";
    private String fileName = "0000";
    private Context context;
    private JSONObject channelInfo;

    private  final long[] interval = {2000};
    final long[] lastEntryTime ={0};

    private JSONArray dailyFeed =new JSONArray();
    private SharedPreferences sharedPreferences;
    private boolean keepUpdating;
    ProgressBar progressBar;
    public WeatherDataController(Context context, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setDailyMaximaApiKey(String dailyMaximaApiKey){
        this.dailyMaximaApiKey = dailyMaximaApiKey;
    }
    public void setKeepUpdating(boolean keepUpdating){
        this.keepUpdating = keepUpdating;
    }

    public void setDailyMaximaChannelId(String dailyMaximaChannelId) {
        this.dailyMaximaChannelId = dailyMaximaChannelId;
    }

    public void setOnDataArrivedListener(OnDataArrivedListener listener) {
        this.listeners.add(listener);
    }

    public void loadData(String type, int amount) {

        sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        updateFileName();
        loadWeatherDataFromServer("https://api.thingspeak.com/channels/" + channelID + "/feeds.json?" + type + "=" + String.valueOf(amount));

    }

    private void updateFileName(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd", Locale.GERMAN);
        fileName = sdf.format(Calendar.getInstance().getTime());
        lastDate = fileName;
    }
    private void keepUpdating(JSONArray feed){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        final long margin = 2000;
        long secondEntryTime =0;
        try {
            lastEntryTime[0] = sdf.parse(feed.getJSONObject(feed.length()-1).getString("created_at")).getTime();
            secondEntryTime = sdf.parse(feed.getJSONObject(feed.length()-2).getString("created_at")).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final long initialDelay = (Calendar.getInstance().getTimeInMillis()-3600000 - lastEntryTime[0]);
         interval[0] = lastEntryTime[0] - secondEntryTime;

      // Log.d("DEBUG","sdf2=" +sdf.format(feed.getJSONObject(feed.length()));
      //  Log.d("DEBUG","sdf2=" +sdf2.format(feed[0].get(feed[0].size() - 2).getX()));
       Log.d("DEBUG","initialDelay="+String.valueOf(initialDelay/60000.0f)+" interval="+String.valueOf((interval[0])/60000.0f));
        if(initialDelay >=0&& interval[0] >0){
            final Handler handler = new Handler();
            final java.lang.Runnable runnable = new java.lang.Runnable() {
                @Override
                public void run() {
                    loadLastEntryFromServer("https://thingspeak.com/channels/333150/feeds/last.json");
                    //   progressBar.setVisibility(View.VISIBLE);
                    Log.d("GGG",String.valueOf(interval[0] +margin));
                    handler.postDelayed(this, interval[0] +margin);
                }
            };
            handler.postDelayed(runnable,initialDelay+margin);
        }else{
          //  Log.d("DEBUG","initialDelay="+String.valueOf(initialDelay)+" interval="+String.valueOf(interval));
        }
        initProgressBar(initialDelay);
    }
    long gt = 0;
    private void initProgressBar(long initialDelay){
        final int updateInterval =5;
        final float[] progress = {initialDelay};
        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                if(progress[0] < interval[0]){
                    progress[0] +=updateInterval;
                }else{
                    progress[0] = 0;
                }
            //    Log.d("DEBUG",String.valueOf((int) ((progress[0]/interval[0])*1000)));
               progressBar.setProgress((int) ((progress[0]/interval[0])*1000));
                handler.postDelayed(this, updateInterval);
            }
        };
        handler.postDelayed(runnable,updateInterval);
    }

    private void checkTalkBackCommand(String url){
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                JSONObject obj = getResponse();
                try {
                    executeTalckBackCommand(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    private void executeTalckBackCommand(JSONObject command) throws JSONException {
        switch (command.getString("command_string")){
            case "INTERVAL_CHANGED":  interval[0] = command.getInt("position"); break;
        }
    }
private String lastDate = "";
    private long loadLastEntryFromServer(String url){
        final long[] result = {0};
        executeJsonObjectReqest(url, new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = getResponse();
                    Entry[] entry =  new Entry[8];
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
                    String str = obj.getString("created_at");
                    long time = sdf.parse(str).getTime();
                    for(int k = 0; k < 8;k++){
                        entry[k] = new Entry(time, (float) obj.getDouble("field" + String.valueOf(k+1)));
                    }
                //    progressBar.setVisibility(View.GONE);
                        String date = str.substring(0,10);
                        if(!date.equals(lastDate)){
                            fileName = date;
                            saveWeatherData(fileName,dailyFeed.toString(), false);
                        }
                        lastDate = date;
                        dailyFeed.put(obj);

                        int margin = 2000;
                        if(time - lastEntryTime[0] > interval[0] + margin||time - lastEntryTime[0] < interval[0] - margin){
                            Log.d("DEBUG","poszlo");
                            checkTalkBackCommand("https://api.thingspeak.com/talkbacks/28333/commands/execute.json?api_key=QBRE5TVUGHQCNM88");
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
        return result[0];
    }
    private boolean flag  =true;
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
                    if(flag) {
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

    private void loadDailyMaximaFromServer(String url){
        executeJsonObjectReqest(url, new Runnable(){
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
                //    progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                  //      l.dailyMaximaArrived(feed);
                    }
                    saveWeatherData("dailyMaxima",feeds.toString(), true);
                } catch (Exception e) {
                    msg(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadDailyMaximaFromServer(String url, final ArrayList<Entry>[] feed, final JSONArray feeds2){
        executeJsonObjectReqest(url, new Runnable(){
            @Override
            public void run() {
                try {
                    JSONArray feeds = getResponse().getJSONArray("feeds");
                    ArrayList<Entry>[] feed = readJsonArray(feeds);
            //        progressBar.setVisibility(View.GONE);
                    for (OnDataArrivedListener l : listeners) {
                  //      l.dailyMaximaArrived(feed);
                    }
                    saveWeatherData("dailyMaxima",feeds.toString(), false);
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

    private ArrayList<Entry[][]> convertJsonToDailyMaxima(JSONArray feeds) throws Exception {
        ArrayList<Entry[][]> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        for (int k = 0; k < feeds.length(); k++) {
            long time = sdf.parse(feeds.getJSONObject(k).getString("created_at")).getTime();
            Entry[][] entry = new Entry[7][3];
            for (int j = 0; j < entry.length; j++) {
                try {
                    String[] vals = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(",");
                    for(int i = 0; i < vals.length;i++){
                        entry[j][i] = new Entry((float)time, Float.valueOf(vals[i]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                result.add(entry);
            }
        }
        return result;
    }

    private ArrayList<Entry>[] readJsonArray(JSONArray feeds) throws Exception {
        ArrayList<Entry>[] result = new ArrayList[7];
        for (int k = 0; k < result.length; k++) {
            result[k] = new ArrayList<>();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        long time = 1000;
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
     //   lastUpdatedTime = time;
        return result;
    }

    private void readJsonArray(JSONArray feeds, ArrayList<Entry>[] result) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN);
        long time = 1000;
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
      //  lastUpdatedTime = time;
    }


    public void dataRangeChanged(int value) {
        for (OnDataArrivedListener l : listeners) {
            l.dataRangeChanged(value);
        }
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

        void dailyMaximaArrived(ArrayList<Entry[][]> result);

        void dataRangeChanged(int entries);
    }

    private class Runnable implements java.lang.Runnable {
        private JSONObject response;

        public void setResponse(JSONObject response) {
            this.response = response;
        }

        public JSONObject getResponse() {
            return response;
        }

        @Override
        public void run() {
        }
    }
}
