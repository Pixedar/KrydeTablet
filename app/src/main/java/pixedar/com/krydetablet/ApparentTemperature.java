package pixedar.com.krydetablet;

public class ApparentTemperature {
    final double e = 2.71828182845904;

    private double heatIndex;
    private double humidex ;
    private double windHill;
    private double australianApparenTemp;
    private float t;
    private float rh;
    private float wind;
    private Range range;
    public void update(float t, float rh, float wind,Range range){
        this.wind = wind * 3.44808949784f;
        this.rh = rh;
        this.t = t;
        this.range = range;
        heatIndex = heatIndex(t, rh);
        humidex = humidex(t, rh);
        windHill = windHill(t, rh, wind);
        australianApparenTemp = australianApparenTemp(t, rh, wind);

/*        Log.d("DEBUG","start");
        Log.d("DEBUG",String.valueOf(wind));
        Log.d("DEBUG",String.valueOf(t));
        Log.d("DEBUG","rh");
        Log.d("DEBUG",String.valueOf(rh));
        Log.d("DEBUG","dew");
        Log.d("DEBUG",String.valueOf(dewPoint(t, rh)));*/
/*        Log.d("DEBUG","heatIndex");
        Log.d("DEBUG",String.valueOf(heatIndex));
        Log.d("DEBUG","humidex)");
        Log.d("DEBUG",String.valueOf(humidex));
        Log.d("DEBUG","windHill)");
        Log.d("DEBUG",String.valueOf(windHill));
        Log.d("DEBUG","austr)");
        Log.d("DEBUG",String.valueOf(australianApparenTemp));*/

    }
    public float getColorHue(){
        float hue = 0;
        if (t >= 27 && rh >= 40) {
            hue = (float) ((0.5 * getHumidexColor(humidex)) + (0.5 * getHeatIndexColor(heatIndex)));
        } else if (t >= 21 && rh <= 80) {
            hue = (float) ((0.6 * getHumidexColor(humidex))+(0.4 *getHeatIndexColor(heatIndex)));
        } else if (t >=16) {
            hue = getHumidexColor(humidex);
        }else {
           return -1;
        }
      //  float windPowerWeight = 0.8f;
     //   float windPower = map(wind, range.getMinAverangeWind(), range.getMaxAverangeWind() * 3.6f, 0, windPowerWeight);
        float windPower = map(wind, range.getMinAverangeWind(), range.getMaxAverangeWind() * 3.6f, 0, 1.0f);
       return  (hue*(1.0f - windPower)+(australianApparenTempColor(australianApparenTemp)*windPower));
    }
    public float getApparentTemperature(){
      //  float windPowerWeight = 0.7f;
       // float windPower = map(wind, range.getMinAverangeWind(), range.getMaxAverangeWind() * 3.6f, 0, windPowerWeight);
        float windPower = map(wind, range.getMinAverangeWind(), range.getMaxAverangeWind() * 3.6f, 0, 1.0f);
        if (t >= 27 && rh >= 40) {
            return (float) (australianApparenTemp*(1.0f - windPower) + heatIndex*windPower);
        } else if (t >= 21 && rh <= 80) {
            return (float) (australianApparenTemp*(1.0f - windPower) + heatIndex*windPower);
        } else if (t <= 10 && wind >= 4.8f) {
            if(t > 7){
                float m = map(t, 7.0f,10.0f,0.0f,0.5f);
                return (float) ((windHill*((0.5f-m)+0.5f))+(australianApparenTemp*m));
            }else {
                return (float) windHill;
            }
        } else if(t > 0){
            return (float) australianApparenTemp;
        }else {
            return -100;
        }

    }
    public float[] calculate(float t, float rh, float wind, Range range) {
        wind = wind * 3.6f;
        double heatIndex = heatIndex(t, rh);
        double humidex = humidex(t, rh);
        double windHill = windHill(t, rh, wind);
        double australianApparenTemp = australianApparenTemp(t, rh, wind);


        float heat;

        if (t >= 27 && rh >= 40) {
            heat = (float) (0.5 * australianApparenTemp + 0.5 * heatIndex);
        } else if (t >= 21 && rh <= 80) {
            heat = (float) (0.6 * australianApparenTemp + 0.4 * heatIndex);
        } else if (t <= 10 && wind >= 4.8f) {
            heat = (float) windHill;
        } else {
            heat = (float) australianApparenTemp;
        }


        float windPower = map(wind, range.getMinAverangeWind(), range.getMaxAverangeWind() * 3.6f, 0, 0.5f);

        float p;
        if (t > 20.0f) {
            p = 1.0f;
        } else if (t > 10.1f) {
            p = (float) Math.sqrt(t - 10) / 3.16f;
            if (p < 0) {
                p = 0;
            }
        } else {
            p = 0;
        }
/*
        Log.d("DEBUG","start");
        Log.d("DEBUG",String.valueOf(wind));
        Log.d("DEBUG",String.valueOf(t));
        Log.d("DEBUG","rh");
        Log.d("DEBUG",String.valueOf(rh));
        Log.d("DEBUG","dew");
        Log.d("DEBUG",String.valueOf(dewPoint(t, rh)));
        Log.d("DEBUG","heatIndex");
        Log.d("DEBUG",String.valueOf(heatIndex));
        Log.d("DEBUG","humidex)");
        Log.d("DEBUG",String.valueOf(humidex));
        Log.d("DEBUG","windHill)");
        Log.d("DEBUG",String.valueOf(windHill));
        Log.d("DEBUG","p)");
        Log.d("DEBUG",String.valueOf(p));
        Log.d("DEBUG","windPower)");
        Log.d("DEBUG",String.valueOf(windPower));
        Log.d("DEBUG","result)");
        Log.d("DEBUG",String.valueOf((float) ((heat*(0.5+0.5-windPower)+ windPower*windHill)*p + windHill*(1.0f-p))));
        Log.d("DEBUG","aus)");
        Log.d("DEBUG",String.valueOf(australianApparenTemp(t,rh,wind)));
        Log.d("DEBUG","end");
*/

        if (australianApparenTemp >= 16 && humidex >= 16) {

        } else {

        }
        return new float[]{(float) ((heat * (0.5 + 0.5 - windPower) + windPower * windHill) * p + windHill * (1.0f - p)), 0};


    }

    private double windHill(float t, float rh, float v) {
        if (t <= 10.0f && v >= 4.8f) {
            return 13.12f + (0.6215 * t) - (11.37 * Math.pow(v, 0.16f)) + (0.3965 * t * Math.pow(v, 0.16f));
        } else {
            return  t;
        }
    }

    private double australianApparenTemp(float t, float rh, float v) {
        double g = (rh / 100.0f) * 6.105 * Math.exp((17.27f * t) / (237.7 + t));
        return t + (0.33f * g) - (0.7 * (v / 3.6f)) - 4.0f;
    }

    private double heatIndex(float t, float rh) {
        double result;
        if (t >= 27.0f && rh >= 40) {
            final double c1 = -8.78469475556d;
            final double c2 = 1.61139411d;
            final double c3 = 2.33854883889d;
            final double c4 = -0.14611605d;
            final double c5 = -0.012308094d;
            final double c6 = -0.0164248277778d;
            final double c7 = 0.002211732d;
            final double c8 = 0.00072546d;
            final double c9 = -0.000003582d;
            result = (c1 + c2 * t + c3 * rh + c4 * t * rh + c5 * Math.pow(t, 2) + c6 * Math.pow(rh, 2) + c7 * Math.pow(t, 2) * rh + c8 * t * Math.pow(rh, 2) + c9 * Math.pow(t, 2) * Math.pow(rh, 2));
        } else {
            final double c1 = 0.363445176d;
            final double c2 = 0.988622465d;
            final double c3 = 4.777114035d;
            final double c4 = -0.114037662d;
            final double c5 = -8.50208 * Math.pow(10, -4);
            final double c6 = -2.0716198 * Math.pow(10, -2);
            final double c7 = 6.87678 * Math.pow(10, -4);
            final double c8 = 2.74954 * Math.pow(10, -4);
            final double c9 = 0;
            result = (5.0f/9.0f)*((c1 + c2 * t + c3 * rh + c4 * t * rh + c5 * Math.pow(t, 2) + c6 * Math.pow(rh, 2) + c7 * Math.pow(t, 2) * rh + c8 * t * Math.pow(rh, 2) + c9 * Math.pow(t, 2) * Math.pow(rh, 2)) -32);
        }
        if(result > 58){
            return  58;
        }else if(result < 20){
            return 20;
        }else {
            return  result;
        }
    }

    private float getHeatIndexColor(double heatIndex) {
        if (heatIndex >= 27 && heatIndex <= 52) {
            return map((float) heatIndex, 27, 52, 60, 0);
        } else if (heatIndex > 52) {
            return 0;
        } else if (heatIndex < 27 && heatIndex <= 21) {
            return map((float) heatIndex, 21, 27, 100, 64);
        } else {
            return 100;
        }

    }

    private float getHumidexColor(double humidex) {
/*        if (humidex >= 16 && humidex <= 29) {
            return map((float)humidex,16,29,120,70);
        }else if(humidex > 29&& humidex <=39){
            return map((float)humidex,29,39,70,45);
        }else if (humidex > 39&& humidex <=45){
            return map((float)humidex,39,45,45,20);
        }else {
            return 0;
        }*/

        if (humidex >= 50) {
            return 0;
        } else if (humidex > 46 && humidex <= 50) {
            return map((float) humidex, 46, 50, 19, 0);
        } else if (humidex > 40 && humidex >= 46) {
            return map((float) humidex, 40, 46, 45, 19);
        } else if (humidex > 30 && humidex <= 40) {
            return map((float) humidex, 30, 40, 64, 45);
        } else if (humidex > 16 && humidex <= 30) {
            return map((float) humidex, 16, 30, 150, 64);
        } else {
            return 150;
        }
    }

    private float australianApparenTempColor(double australianApparenTemp) {
        if (australianApparenTemp >= 27 && australianApparenTemp <= 52) {
            return map((float) australianApparenTemp, 27, 52, 60, 0);
        } else if (australianApparenTemp > 52) {
            return 0;
        } else if (australianApparenTemp >= 16 && australianApparenTemp <= 27) {
            return map((float) australianApparenTemp, 16, 27, 150, 60);
        } else {
            return 150;
        }
    }

    private double humidex(float t, float rh) {
        double dewPoint = dewPoint(t, rh);
        if(dewPoint > 28){
            dewPoint = 28;
        }
        double result = (t + 0.5555f * (Math.pow(6.11f * e, 5417.7530f * ((1 / (273.16)) - (1 / (273.15 + dewPoint)))) - 10.0f));
        if(result > 59){
            return 59;
        }else if(result < 0){
            return 0;
        }else {
            return result;
        }
    }

    public double dewPoint(float t, float rh) {
        final float a = 6.1121f;
        final float b = 18.678f;
        final float c = 257.14f;
        final float d = 234.5f;

        return ((c * lambda(t, rh, b, d, c)) / (b - lambda(t, rh, b, d, c)));
    }

/*    private double lambda(float t, float rh, float b, float d, float c) {
        return Math.log(rh / 100.0f) * Math.pow(e, (b - (t / d)) * (t / (c + t)));
    }*/

    private double lambda(float t, float rh, float b, float d, float c) {
        return Math.log(rh / 100.0f) + (b * t / (c + t));
    }

    private float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
}
