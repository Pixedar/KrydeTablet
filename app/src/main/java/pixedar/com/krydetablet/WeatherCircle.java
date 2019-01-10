package pixedar.com.krydetablet;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.github.mikephil.charting.data.Entry;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WeatherCircle extends GLSurfaceView {
    public Renderer renderer;

    public WeatherCircle(Context context) {
        super(context);
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(1);
        renderer = new Renderer();
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setListener(WeatherDataController weatherDataController) {
        renderer.setListener(weatherDataController);
    }

    public class Renderer implements GLSurfaceView.Renderer {

        public void onDrawFrame(GL10 gl10) {
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            gl10.glTranslatef(0, 0, -4); /// remove
            drawCircle(gl10);
            gl10.glLoadIdentity(); // remove
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            // Set the background color to black ( rgba ).
            gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
            // Enable Smooth Shading, default not really needed.
            gl10.glShadeModel(GL10.GL_SMOOTH);
            // Depth buffer setup.
            //    gl10.glClearDepthf(1.0f);
            // Enables depth testing.
            //   gl10.glEnable(GL10.GL_DEPTH_TEST);
            // The type of depth testing to do.
            //   gl10.glDepthFunc(GL10.GL_LEQUAL);
            // Really nice perspective calculations.
            gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_NICEST);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            // Sets the current view port to the new SIZE.
            unused.glViewport(0, 0, width, height);
            // Select the projection matrix
            unused.glMatrixMode(GL10.GL_PROJECTION);
            // Reset the projection matrix
            unused.glLoadIdentity();
            // Calculate the aspect ratio of the window
            GLU.gluPerspective(unused, 45.0f,
                    (float) width / (float) height,
                    0.1f, 100.0f);
            // Select the modelview matrix
            unused.glMatrixMode(GL10.GL_MODELVIEW);
            // Reset the modelview matrix
            unused.glLoadIdentity();
        }

        float[][] y = new float[2][4];
        private float interpolation = 0;
        private final static float TWO_PI = 6.283185f;
        private final static float RES = 20.0f;
        private final static float INTERPOLATION_INTERVAL = 0.009f;
        private final static int SIZE = 150;
        private float[] vertices = new float[(int) (((SIZE * Math.ceil(RES)) * 3) + 3)];
        private float[] colors = new float[(int) (((SIZE * Math.ceil(RES)) * 4) + 4)];
        private short[] indices = new short[(int) ((SIZE * Math.ceil(RES)) + 1)];


        void setListener(WeatherDataController weatherDataController) {
            weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
                @Override
                public void dataArrived(ArrayList<Entry>[] result) {
                    final float scale = 0.4f;
                    final float windThreshold = 1.7f;

                    setValue(result,GetFieldName.OUTSIDE_TEMP.getIndex(),0,-12.5f,12.5f,scale);
                    setValue(result,GetFieldName.PRESSURE.getIndex(),1,961,996,scale);

                    float WindAvg0 = (result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[0].size() - 2).getY() + result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[0].size() - 3).getY()) / 2.0f;
                    float WindAvg1 = (result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[0].size() - 1).getY() + result[GetFieldName.AVERAGE_WIND.getIndex()].get(result[0].size() - 2).getY()) / 2.0f;
                    if (WindAvg0 > windThreshold) {
                        y[0][2] = map(WindAvg0 - windThreshold, 0, 20f - windThreshold, 0, scale);
                    } else {
                        y[0][2] = 0;
                    }
                    if (WindAvg1 > windThreshold) {
                        y[1][2] = map(WindAvg1 - windThreshold, 0, 20f - windThreshold, 0, scale);
                    } else {
                        y[1][2] = 0;
                    }
                    setValue(result,GetFieldName.OUTSIDE_HUM.getIndex(),3,100,0,scale);
                    setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                    //   norm(y,0, 0.4f);
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

                @Override
                public void dataUpdated(Entry[] result) {
                    y[0][0] = y[1][0];
                    y[1][0] = map(result[0].getY(), -20, 35, 0, 0.4f);

                    y[0][1] = y[1][1];
                    y[1][1] = map(result[4].getY(), 961, 996, 0, 0.4f);

                    y[0][2] = y[1][2];
                    y[1][2] = map(result[5].getY(), 0, 20, 0, 0.4f);
                    interpolation = 0;
                    setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                }

                @Override
                public void dailyMaximaArrived(ArrayList<Entry[][]> result) {

                }

                @Override
                public void dataRangeChanged(int entries) {

                }
            });
        }

        private float map(float x, float in_min, float in_max, float out_min, float out_max) {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        }


        // Our vertex buffer.
        private FloatBuffer vertexBuffer;
        // Our index buffer.
        private ShortBuffer indexBuffer;
        private FloatBuffer colorBuffer;

        Renderer() {
            // a float is 4 bytes, therefore we multiply the number if
            // vertices with 4.
            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
/*                vertexBuffer.put(vertices);
                vertexBuffer.position(0);*/

            // short is 2 bytes, therefore we multiply the number if
            // vertices with 2.
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
        
        void drawCircle(GL10 gl) {
            int vertexIndex = 0;
            int colorIndex = 0;

            if (interpolation < 1) {
                interpolation += INTERPOLATION_INTERVAL;
            } else {
                setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
                vertices[vertexIndex] = 0;
                vertexIndex++;

                int rgb = Color.HSVToColor(new float[]{map(j, 0, (float) (TWO_PI * SIZE), 0, 340), 100, 100});
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
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0,
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


}
