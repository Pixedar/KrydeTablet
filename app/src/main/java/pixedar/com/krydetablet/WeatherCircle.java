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
    public void setListener(WeatherDataController weatherDataController){
        renderer.setListener(weatherDataController);
    }
    public class Renderer implements GLSurfaceView.Renderer {

        public void onDrawFrame(GL10 unused) {
            // Redraw background color
        ///    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
       //     GLES20.glDrawArrays(GLES20.GL_LINE_STRIP,0,1);
            // Clears the screen and depth buffer.
            unused.glClear(GL10.GL_COLOR_BUFFER_BIT);
            unused.glTranslatef(0, 0, -4);
            // Draw our square.
            draw(unused); // ( NEW )
            unused.glLoadIdentity();
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
            // Sets the current view port to the new size.
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

        public void setListener(WeatherDataController weatherDataController) {
            weatherDataController.setOnDataArrivedListener(new WeatherDataController.OnDataArrivedListener() {
                @Override
                public void dataArrived(ArrayList<Entry>[] result) {
                    float scale = 0.4f;
                    float windThreshold = 1.7f;
                    y[0][0] = map(result[0].get(result[0].size()-2).getY(),-12.5f,12,0,scale);
                    y[1][0] = map(result[0].get(result[0].size()-1).getY(),-12.5f,12,0,scale);

                    y[0][1] = map(result[4].get(result[0].size()-2).getY(),961,996,0,scale);
                    y[1][1] = map(result[4].get(result[0].size()-1).getY(),961,996,0,scale);

                    float WindAvg0 = (result[5].get(result[0].size()-2).getY() + result[5].get(result[0].size()-3).getY())/2.0f;
                    float WindAvg1 = (result[5].get(result[0].size()-1).getY() + result[5].get(result[0].size()-2).getY())/2.0f;
                    if( WindAvg0 > windThreshold) {
                        y[0][2] = map(WindAvg0 - windThreshold, 0, 20f-windThreshold, 0, scale);
                    }else{
                        y[0][2] = 0;
                    }
                    if( WindAvg1 > windThreshold) {
                        y[1][2] = map(WindAvg1 -windThreshold, 0, 20f-windThreshold, 0, scale);
                    }else{
                        y[1][2] = 0;
                    }

                    y[0][3] =scale- map(result[2].get(result[0].size()-2).getY(),0,100,0,scale);
                    y[1][3] =scale - map(result[2].get(result[0].size()-1).getY(),0,100,0,scale);
                    setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                 //   norm(y,0, 0.4f);
                 //   requestRender();
                }

                @Override
                public void dataUpdated(Entry[] result) {
                    y[0][0] =  y[1][0];
                    y[1][0] = map(result[0].getY(),-20,35,0,0.4f);

                    y[0][1] = y[1][1];
                    y[1][1] = map(result[4].getY(),961,996,0,0.4f);

                    y[0][2] =  y[1][2];
                    y[1][2] = map(result[5].getY(),0,20,0,0.4f);
                    q =0;
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

        private float map(float x, float in_min, float in_max, float out_min, float out_max)
        {
            return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
        }

            int al = 150;
            private float vertices[] = new float[((al*20)*3)+3];
            private float colors[] = new float[((al*20)*4)+4];
            private short[] indices = new short[(al*20)+1];
            // The order we like to connect them.
          //  private short[] indices = { 0, 1, 2, 0, 2, 3 };

            // Our vertex buffer.
            private FloatBuffer vertexBuffer;

            // Our index buffer.
            private ShortBuffer indexBuffer;


             private FloatBuffer colorBuffer;

            public Renderer() {
                // a float is 4 bytes, therefore we multiply the number if
                // vertices with 4.
                ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
                vbb.order(ByteOrder.nativeOrder());
                vertexBuffer = vbb.asFloatBuffer();
/*                vertexBuffer.put(vertices);
                vertexBuffer.position(0);*/

                // short is 2 bytes, therefore we multiply the number if
                // vertices with 2.
                for(short k =0;k < indices.length;k++){
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
            float q = 0;

            public void draw(GL10 gl) {
                int s = 0;
                int c =0;

             //   q+=0.05;

                if(q < 1){
                    q+=0.009;
                }else{
                    setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                }

                for (float j = 0; j < 6.283185*al; j+=6.283185/20.0f) {
                    float sinSum =0;

                    for (int k =0; k < y[0].length; k++) {
                   //      sinSum +=sin(y[l+1][k]*j*xScale)*q+ sin(y[l][k]*j*xScale)*(1-q);
                       //  sinSum +=sin(y[l+1][k]*j*xScale*k)*q+ sin(y[l][k]*j*xScale*k)*(1-q);

                        sinSum +=Math.sin(y[1][k]*j*(k+1))*q+ Math.sin(y[0][k]*j*(k+1))*(1-q);
                     //   sinSum +=Math.sin(y[1][k]*j*(k+1))*Math.sin(q)+ Math.sin(y[0][k]*j*(k+1))*Math.cos(q);
                    //    sinSum +=Math.sin(y[1][k]*j*(k+1))*Math.sin(q)*Math.sin(q)+ Math.sin(y[0][k]*j*(k+1))*Math.cos(q)*Math.cos(q);


                        // sinSum +=sin(y[l+1][k]*j*xScale+u)*q+ sin(y[l][k]*j*xScale+u)*(1-q);
                    }
              //      stroke(map(j,0,TWO_PI*200,90,0),100,100);
                    // vertex(j*2, height*0.5 +(sinSum+6)*20);
                    sinSum*=0.4;

                    vertices[s]= (float) ((float) Math.cos((j/20))*((sinSum/y[0].length)+1.1));


                    int rgb = Color.HSVToColor(new float[]{map(j, 0, (float) (6.283185 * al), 0, 340),100,100});
                    float r = ((rgb >> 16) & 0xFF)/255.f;
                    float g = ((rgb >> 8) & 0xFF)/255.f;
                    float b = (rgb & 0xFF)/255.0f;

                    colors[c] = r;
                    c++;
                    colors[c] = g;
                    c++;
                    colors[c] = b;
                    c++;
                    colors[c] = 1;
                    c++;

                    s++;
                    vertices[s]= (float) ((float) Math.sin((j/20))*((sinSum/y[0].length)+1.1));
                    s++;
                    vertices[s]= 0;
                    s++;
                //    Log.d("GGG",String.valueOf(s));

                }

                colorBuffer.put(colors);
                colorBuffer.position(0);


                vertexBuffer.put(vertices);
                vertexBuffer.position(0);

                // short is 2 bytes, therefore we multiply the number if
                // vertices with 2.
 /*               ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
                ibb.order(ByteOrder.nativeOrder());
                indexBuffer = ibb.asShortBuffer();*/
             //   indexBuffer.put(indices);
             //   indexBuffer.position(0);

                // Counter-clockwise winding.
              //  gl.glFrontFace(GL10.GL_CCW);
                // Enable face culling.
             //   gl.glEnable(GL10.GL_CULL_FACE);
                // What faces to remove with the face culling.
            //    gl.glCullFace(GL10.GL_BACK);

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
