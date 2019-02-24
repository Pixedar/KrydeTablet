package pixedar.com.krydetablet;

import android.graphics.Color;
import android.opengl.GLSurfaceView;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class WeatherHarmony {
    float[][] y = new float[2][5];
    private float interpolation = 0;
    private final static float TWO_PI = 6.283185f;
    private final static float RES = 20.0f;
    private final static float INTERPOLATION_INTERVAL = 0.009f;
    private final static int SIZE = 150;
    private float[] vertices = new float[(int) (((SIZE * Math.ceil(RES)) * 2) + 2)];
    private float[] colors = new float[(int) (((SIZE * Math.ceil(RES)) * 4) + 4)];
    private short[] indices = new short[(int) ((SIZE * Math.ceil(RES)) + 1)];

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    private FloatBuffer colorBuffer;
    private GLSurfaceView glSurfaceView[] = new GLSurfaceView[1];
    private Range range;
    WeatherHarmony(GLSurfaceView glSurfaceView) {
        this.glSurfaceView[0] = glSurfaceView;
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();

        for (short k = 0; k < indices.length; k++) {
            indices[k] = k;
        }
        ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuffer = ibb.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();
    }

    public void draw(GL10 gl10) {
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glTranslatef(0, 0, -4); /// remove
        drawCircle(gl10);
        gl10.glLoadIdentity(); // remove
    }


    void setListener(WeatherDataController weatherDataController) {
        range = new Range();
        weatherDataController.setOnAutoRangeChangedListener(new WeatherDataController.OnAutoRangeChanged() {
            @Override
            public void autoRangeChanged(JSONArray feed) {
                range.setAutoRange(feed);
            }
        });

        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            final float scale = 0.46f;
            final float windThreshold = 1.6f;
            final float humThreshold = 92;
            private float rain =0;
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                setValue(result,GetFieldName.OUTSIDE_TEMP.getIndex(),0,range.getMinOutsideTemperature(),range.getMaxOutsideTemperature(),scale);
                setValue(result,GetFieldName.PRESSURE.getIndex(),1,range.getMinPressure(),range.getMaxPressure(),scale);

                float WindAvg0 = (result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 2).getY() + result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 3).getY()) / 2.0f;
                float WindAvg1 = (result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 1).getY() + result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 2).getY()) / 2.0f;
                if (WindAvg0 > windThreshold) {
                    y[0][2] = map(WindAvg0 - windThreshold, 0, 19f - windThreshold, 0, scale);
                } else {
                    y[0][2] = 0;
                }
                if (WindAvg1 > windThreshold) {
                    y[1][2] = map(WindAvg1 - windThreshold, 0, 19f - windThreshold, 0, scale);
                } else {
                    y[1][2] = 0;
                }
                setValue(result,GetFieldName.OUTSIDE_HUM.getIndex(),3,range.getMaxOutsideHumidity(),range.getMinOutsideHumidity(),scale);

                glSurfaceView[0].setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                //   requestRender();
            }
            private void setValue(ArrayList<Entry>[] result,int index1,int index2,float a,float b,float scale){
                float i = result[index1].get(result[index1].size() - 2).getY();
                float j = result[index1].get(result[index1].size() - 1).getY();
                if(j >=a&&j<=b&&i >=a&&i<=b){
                    y[0][index2] = map(result[0].get(result[0].size() - 2).getY(), a, b, 0, scale);
                    y[1][index2] = map(result[0].get(result[0].size() - 1).getY(), a, b, 0, scale);
                }
            }
            private void setValue(Entry[] result,int index1,int index2,float a,float b,float scale){
                y[0][index2] = y[1][index2];
                y[1][index2] = map(result[index1].getY(), a, b, 0, scale);
            }
            private void setValue(float value,int index2,float a,float b,float scale){
                y[0][index2] = y[1][index2];
                y[1][index2] = map(value, a, b, 0, scale);
            }

            @Override
            public void dataUpdated(Entry[] result) {
                range.update(result);
                setValue(result,GetFieldName.OUTSIDE_TEMP.getIndex(),0,range.getMinOutsideTemperature(),range.getMaxOutsideTemperature(),scale);
                setValue(result,GetFieldName.PRESSURE.getIndex(),1,range.getMinPressure(),range.getMaxPressure(),scale);
                if(result[GetFieldName.AVERAGE_WIND.getIndex()].getY() > windThreshold){
                    y[0][2] = y[1][2];
                    y[1][2] = map(result[GetFieldName.AVERAGE_WIND.getIndex()].getY()- windThreshold, 0, 19 - windThreshold, 0, scale);
                }else{
                    y[0][2] = y[1][2];
                    y[1][2] = 0;
                }
                if(result[GetFieldName.OUTSIDE_HUM.getIndex()].getY() < humThreshold){
                    setValue(result,GetFieldName.OUTSIDE_HUM.getIndex(),3,range.getMaxOutsideHumidity(),range.getMinOutsideHumidity(),scale);
                }else{
                    y[0][3] = y[1][3];
                    y[1][3] = 0;
                }

                rain-=rain*0.4;
                if(rain <0.1){
                    rain =0;
                }
                if(result[GetFieldName.RAIN.getIndex()].getY() > rain){
                    if(result[GetFieldName.RAIN.getIndex()].getY() <=range.getMaxRain()) {
                        rain = result[GetFieldName.RAIN.getIndex()].getY();
                    }
                }
                setValue(rain,4,0,range.getMaxRain(),scale);


                interpolation = 0;
                glSurfaceView[0].setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            }
        });
    }

    private float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    void drawCircle(GL10 gl) {
        int vertexIndex = 0;
        int colorIndex = 0;

        if (interpolation < 1) {
            interpolation += INTERPOLATION_INTERVAL;
        } else {
            glSurfaceView[0].setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }

        for (float j = 0; j <TWO_PI  * SIZE; j += TWO_PI  / RES) {
            float sinSum = 0;
            for (int k = 0; k < y[0].length; k++) {
                //      sinSum +=sin(y[l+1][k]*j*xScale)*interpolation+ sin(y[l][k]*j*xScale)*(1-interpolation);
                //  sinSum +=sin(y[l+1][k]*j*xScale*k)*interpolation+ sin(y[l][k]*j*xScale*k)*(1-interpolation);

                sinSum += Math.sin(y[1][k] * j * (k + 1)) * interpolation + Math.sin(y[0][k] * j * (k + 1)) * (1 - interpolation);
                //   sinSum +=Math.sin(y[1][k]*j*(k+1))*Math.sin(interpolation)+ Math.sin(y[0][k]*j*(k+1))*Math.cos(interpolation);
                //    sinSum +=Math.sin(y[1][k]*j*(k+1))*Math.sin(interpolation)*Math.sin(interpolation)+ Math.sin(y[0][k]*j*(k+1))*Math.cos(interpolation)*Math.cos(interpolation);
                // sinSum +=sin(y[l+1][k]*j*xScale+u)*interpolation+ sin(y[l][k]*j*xScale+u)*(1-interpolation);
            }
            //      stroke(map(j,0,TWO_PI*200,90,0),100,100);
            // vertex(j*2, height*0.5 +(sinSum+6)*20);
            sinSum *= 0.4;

            vertices[vertexIndex] = (float) ((float) Math.cos((j / RES)) * ((sinSum / y[0].length) + 1.1));
            vertexIndex++;
            vertices[vertexIndex] = (float) ((float) Math.sin((j / RES)) * ((sinSum / y[0].length) + 1.1));
            vertexIndex++;
            //   vertices[vertexIndex] = 0;
            //    vertexIndex++;

            int rgb = Color.HSVToColor(new float[]{map(j, 0, (float) (TWO_PI * SIZE), 0, 315), 100, 100});
            float r = ((rgb >> 16) & 0xFF) / 255.f;
            float g = ((rgb >> 8) & 0xFF) / 255.f;
            float b = (rgb & 0xFF) / 255.0f;

            colors[colorIndex] = r;
            colorIndex++;
            colors[colorIndex] = g;
            colorIndex++;
            colors[colorIndex] = b;
            colorIndex++;
            colors[colorIndex] = 1;
            colorIndex++;
        }

        colorBuffer.put(colors);
        colorBuffer.position(0);

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        // Enabled the vertices buffer for writing and to be used during
        // rendering.
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // Specifies the location and data format of an array of vertex
        // coordinates to use when rendering.
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0,
                vertexBuffer);

        // Enable the color array buffer to be used during rendering.
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY); // NEW LINE ADDED.
        // Point out the where the color buffer is.
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer); // NEW LINE ADDED.


        gl.glDrawElements(GL10.GL_LINE_STRIP, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

    }
}
