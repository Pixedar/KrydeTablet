package pixedar.com.krydetablet;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;
import org.json.JSONException;

public class Range {
    private float maxAverangeWind;
    private float minAverangeWind;
    private float maxWind;
    private float maxInsideTemperature;
    private float minInsideTemperature;
    private float maxInsideHumidity;
    private float minInsideHumidity;
    private int maxOutsideHumidity;
    private int minOutsideHumidity;
    private int maxRain;
    private float maxPressure;
    private float minPressure;
    private float maxOutsideTemperature;
    private float minOutsideTemperature;

    public Range() {
        resetToDefault();
    }

    public void resetToDefault() {
        maxAverangeWind = 8;
        minAverangeWind = 0;
        maxWind = 20;
        maxInsideTemperature = 28;
        minInsideTemperature = 19;
        maxInsideHumidity = 80;
        minInsideHumidity = 34;
        maxOutsideHumidity = 100;
        minOutsideHumidity = 10;
        maxRain = 8;
        maxPressure = 990;
        minPressure = 969;
        maxOutsideTemperature = 37;
        minOutsideTemperature = -30;
    }
    public void update(Entry[] result){
        if(result[GetFieldName.PRESSURE.getIndex()].getY() > getMaxPressure()){
            setMaxPressure(result[GetFieldName.PRESSURE.getIndex()].getY());
        }else if(result[GetFieldName.PRESSURE.getIndex()].getY() < getMinPressure()){
            setMinPressure(result[GetFieldName.PRESSURE.getIndex()].getY());
        }
        if(result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY() > getMaxOutsideTemperature()){
            setMaxOutsideTemperature(result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY());
        }else if(result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY() < getMinOutsideTemperature()){
            setMinOutsideTemperature(result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY());
        }
        if(result[GetFieldName.INSIDE_TEMP.getIndex()].getY() > getMaxInsideTemperature()){
            setMaxInsideTemperature(result[GetFieldName.INSIDE_TEMP.getIndex()].getY());
        }else if(result[GetFieldName.INSIDE_TEMP.getIndex()].getY() < getMinInsideTemperature()){
            setMinInsideTemperature(result[GetFieldName.INSIDE_TEMP.getIndex()].getY());
        }
        if(result[GetFieldName.INSIDE_HUM.getIndex()].getY() > getMaxInsideHumidity()){
            setMaxInsideHumidity(result[GetFieldName.INSIDE_HUM.getIndex()].getY());
        }else if(result[GetFieldName.INSIDE_HUM.getIndex()].getY() < getMinInsideHumidity()){
            setMinInsideHumidity(result[GetFieldName.INSIDE_HUM.getIndex()].getY());
        }
    }
    public void setAutoRange(JSONArray feeds){
        String separator = "p";
        for (int k = 0; k < feeds.length(); k++) {
            for (int j = 0; j < 7; j++) {
                try {
                    String str[];
                    switch (j+1){
                        case 2:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxOutsideTemperature = Float.valueOf(str[0]);
                            minOutsideTemperature = Float.valueOf(str[1]);
                            break;
                        case 4:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxInsideTemperature = Float.valueOf(str[0]);
                            minInsideTemperature = Float.valueOf(str[1]);
                            break;
                        case 5:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxInsideHumidity = Float.valueOf(str[0]);
                            minInsideHumidity = Float.valueOf(str[1]);
                            break;
                        case 6:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxPressure = Float.valueOf(str[0]);
                            minPressure = Float.valueOf(str[1]);
                            break;
                        case 7:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxAverangeWind = Float.valueOf(str[0]);
                            maxWind = Float.valueOf(str[1]);
                            break;
                        case 8:
                            str = feeds.getJSONObject(k).getString("field" + String.valueOf(j + 1)).split(separator);
                            maxRain= Integer.valueOf(str[0]);
                            minOutsideHumidity = Integer.valueOf(str[1]);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public float getMinAverangeWind() {
        return minAverangeWind;
    }

    public void setMinAverangeWind(float minAverangeWind) {
        this.minAverangeWind = minAverangeWind;
    }

    public float getMaxOutsideTemperature() {
        return maxOutsideTemperature;
    }

    public void setMaxOutsideTemperature(float maxOutsideTemperature) {
        this.maxOutsideTemperature = maxOutsideTemperature;
    }

    public float getMinOutsideTemperature() {
        return minOutsideTemperature;
    }

    public void setMinOutsideTemperature(float minOutsideTemperature) {
        this.minOutsideTemperature = minOutsideTemperature;
    }

    public float getMaxAverangeWind() {
        return maxAverangeWind;
    }

    public void setMaxAverangeWind(float maxAverangeWind) {
        this.maxAverangeWind = maxAverangeWind;
    }

    public float getMaxWind() {
        return maxWind;
    }

    public void setMaxWind(float maxWind) {
        this.maxWind = maxWind;
    }

    public float getMaxInsideTemperature() {
        return maxInsideTemperature;
    }

    public void setMaxInsideTemperature(float maxInsideTemperature) {
        this.maxInsideTemperature = maxInsideTemperature;
    }

    public float getMinInsideTemperature() {
        return minInsideTemperature;
    }

    public void setMinInsideTemperature(float minInsideTemperature) {
        this.minInsideTemperature = minInsideTemperature;
    }

    public float getMaxInsideHumidity() {
        return maxInsideHumidity;
    }

    public void setMaxInsideHumidity(float maxInsideHumidity) {
        this.maxInsideHumidity = maxInsideHumidity;
    }

    public float getMinInsideHumidity() {
        return minInsideHumidity;
    }

    public void setMinInsideHumidity(float minInsideHumidity) {
        this.minInsideHumidity = minInsideHumidity;
    }

    public int getMaxOutsideHumidity() {
        return maxOutsideHumidity;
    }

    public void setMaxOutsideHumidity(int maxOutsideHumidity) {
        this.maxOutsideHumidity = maxOutsideHumidity;
    }

    public int getMinOutsideHumidity() {
        return minOutsideHumidity;
    }

    public void setMinOutsideHumidity(int minOutsideHumidity) {
        this.minOutsideHumidity = minOutsideHumidity;
    }

    public int getMaxRain() {
        return maxRain;
    }

    public void setMaxRain(int maxRain) {
        this.maxRain = maxRain;
    }

    public float getMaxPressure() {
        return maxPressure;
    }

    public void setMaxPressure(float maxPressure) {
        this.maxPressure = maxPressure;
    }

    public float getMinPressure() {
        return minPressure;
    }

    public void setMinPressure(float minPressure) {
        this.minPressure = minPressure;
    }
}
