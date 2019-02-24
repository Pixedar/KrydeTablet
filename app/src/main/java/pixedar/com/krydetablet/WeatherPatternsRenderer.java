package pixedar.com.krydetablet;

import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;

import org.json.JSONArray;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class WeatherPatternsRenderer {
    private int numberOfCircles = 6;
    private float time;
    private int numberOfLines = 200;
    private float values[] = new float[numberOfCircles];
    private float lastValues[] = new float[numberOfCircles];
    private float factor = 1;
    private float factorScale = 0.013152f;
    private float rotationSpeed = 0.17f;
    private boolean flag = false;
    private float circleScale = 0.35f;
    private float margin = 5.1f;
    private float scaleRange[] = {0.7f,0.2f};

    private float[] vertices = new float[((numberOfLines * 4))];
    //  private float[] colors = new float[ ((numberOfLines   * 6))];
    private short[] indices = new short[(numberOfLines * 2)];

    private FloatBuffer vertexBuffer;
    private ShortBuffer indexBuffer;
    //  private FloatBuffer colorBuffer;


    public WeatherPatternsRenderer() {
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 6);
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

   /*     ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
        cbb.order(ByteOrder.nativeOrder());
        colorBuffer = cbb.asFloatBuffer();*/
    }

    public void draw(GL10 gl10) {
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        gl10.glTranslatef(-1.8f, 0.85f, -4);
        time += 0.005 * rotationSpeed;

        if (factor < 1) {
            factorScale*=0.9870135f;
            factor+= factorScale;
        }

        drawCircle(gl10, 0);
        gl10.glTranslatef(circleScale * margin, 0, 0);
        drawCircle(gl10, 1);
        gl10.glTranslatef(circleScale * margin, 0, 0);
        drawCircle(gl10, 2);

        //    gl10.glTranslatef(0, -1.7f,0);
        gl10.glTranslatef(0, -1.55f, 0);
        drawCircle(gl10, 3);
        gl10.glTranslatef(-circleScale * margin, 0, 0);
        drawCircle(gl10, 4);
        gl10.glTranslatef(-circleScale * margin, 0, 0);
        drawCircle(gl10, 5);
        gl10.glLoadIdentity();
    }

    void setListener(final WeatherDataController weatherDataController) {
        final Range range = new Range();
        final Handler handler = new Handler();
        final java.lang.Runnable runnable = new java.lang.Runnable() {
            @Override
            public void run() {
                weatherDataController.getAutoRange();
                handler.postDelayed(this, 43200000);
            }
        };
        handler.postDelayed(runnable, 43200000);
        weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
            @Override
            public void dataArrived(ArrayList<Entry>[] result) {
                values[0] = map(result[GetFieldName.OUTSIDE_TEMP.getIndex()].get(result[GetFieldName.OUTSIDE_TEMP.getIndex()].size() - 1).getY(), range.getMinOutsideTemperature(), range.getMaxOutsideTemperature(), scaleRange[1],scaleRange[0]);
                values[1] = map(result[GetFieldName.PRESSURE.getIndex()].get(result[GetFieldName.PRESSURE.getIndex()].size() - 1).getY(), range.getMinPressure(), range.getMaxPressure(), scaleRange[1],scaleRange[0]);
                values[2] = map(result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[GetFieldName.AVERAGE_WIND.getIndex()].size() - 1).getY(), range.getMinAverangeWind(), range.getMaxAverangeWind(), scaleRange[1],scaleRange[0]);
                values[5] = map(result[GetFieldName.INSIDE_TEMP.getIndex()].get(result[GetFieldName.INSIDE_TEMP.getIndex()].size() - 1).getY(), range.getMinInsideTemperature(), range.getMaxInsideTemperature(), scaleRange[1],scaleRange[0]);
                values[4] = map(result[GetFieldName.INSIDE_HUM.getIndex()].get(result[GetFieldName.INSIDE_HUM.getIndex()].size() - 1).getY(), range.getMinInsideHumidity(), range.getMaxInsideHumidity(), scaleRange[1],scaleRange[0]);
                values[3] = map(result[GetFieldName.OUTSIDE_HUM.getIndex()].get(result[GetFieldName.OUTSIDE_HUM.getIndex()].size() - 1).getY(), range.getMinOutsideHumidity(), range.getMaxOutsideHumidity(), scaleRange[1],scaleRange[0]);
                Log.d("DEBUG", String.valueOf(values[0]));
                flag = true;
            }

            @Override
            public void dataUpdated(Entry[] result) {
                range.update(result);
                System.arraycopy(values, 0, lastValues, 0, values.length);
                factor = 0;
                factorScale =0.013152f;
                values[0] = map(result[GetFieldName.OUTSIDE_TEMP.getIndex()].getY(), range.getMinOutsideTemperature(), range.getMaxOutsideTemperature(), scaleRange[1],scaleRange[0]);
                values[1] = map(result[GetFieldName.PRESSURE.getIndex()].getY(),  range.getMinPressure(), range.getMaxPressure(), scaleRange[1],scaleRange[0]);
                values[2] = map(result[GetFieldName.AVERAGE_WIND.getIndex()].getY(), range.getMinAverangeWind(), range.getMaxAverangeWind(), scaleRange[1],scaleRange[0]);
                values[5] = map(result[GetFieldName.INSIDE_TEMP.getIndex()].getY(),  range.getMinInsideTemperature(), range.getMaxInsideTemperature(), scaleRange[1],scaleRange[0]);
                values[4] = map(result[GetFieldName.INSIDE_HUM.getIndex()].getY(), range.getMinInsideHumidity(), range.getMaxInsideHumidity(), scaleRange[1],scaleRange[0]);
                values[3] = map(result[GetFieldName.OUTSIDE_HUM.getIndex()].getY(), range.getMinOutsideHumidity(), range.getMaxOutsideHumidity(), scaleRange[1],scaleRange[0]);
                Log.d("DEBUG", "arrived2");
            }

        });
        weatherDataController.setOnAutoRangeChangedListener(new WeatherDataController.OnAutoRangeChanged() {
            @Override
            public void autoRangeChanged(JSONArray feed) {
                range.setAutoRange(feed);
            }
        });
    }


    private float map(float x, float in_min, float in_max, float out_min, float out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }
//0.99	0.01

    void drawCircle(GL10 gl, int index) {
        int vertexIndex = 0;
        float val = values[index] * factor + lastValues[index] * (1 - factor);
        int rgb = Color.HSVToColor(new float[]{map(val, scaleRange[1],scaleRange[0], 300, 0), 100, 100});
        gl.glColor4f(((rgb >> 16) & 0xFF) / 255.f, ((rgb >> 8) & 0xFF) / 255.f, (rgb & 0xFF) / 255.0f, 1);

        float a = 1.0f * circleScale;
        float b = 0.1f * circleScale;

        for (float i = 0; i < numberOfLines; i++) {
            vertices[vertexIndex] = (float) (Math.sin((time + i) / b) * a + Math.cos((time + i) / val) * a);
            vertexIndex++;
            vertices[vertexIndex] = (float) (Math.cos((time + i) / b) * a + Math.sin((time + i) / val) * a);
            vertexIndex++;

            vertices[vertexIndex] = (float) (Math.sin((time + i) / b) * b + Math.cos((time + i) / val) * a);
            vertexIndex++;
            vertices[vertexIndex] = (float) (Math.cos((time + i) / b) * b + Math.sin((time + i) / val) * a);
            vertexIndex++;
        }

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0,
                vertexBuffer);

        // Enable the color array buffer to be used during rendering.
        //    gl.glEnableClientState(GL10.GL_COLOR_ARRAY); // NEW LINE ADDED.
        // Point out the where the color buffer is.
        //   gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer); // NEW LINE ADDED.


        gl.glDrawElements(GL10.GL_LINES, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);

        gl.glDrawElements(GL10.GL_POINTS, indices.length,
                GL10.GL_UNSIGNED_SHORT, indexBuffer);
        // Disable the vertices buffer.
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
    public int getNumberOfCircles() {
        return numberOfCircles;
    }

    public void setNumberOfCircles(int numberOfCircles) {
        this.numberOfCircles = numberOfCircles;
    }

    public float getFactorScale() {
        return factorScale;
    }

    public void setFactorScale(float factorScale) {
        this.factorScale = factorScale;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
    public float getMargin() {
        return margin;
    }

    public void setMargin(float margin) {
        this.margin = margin;
    }
}

