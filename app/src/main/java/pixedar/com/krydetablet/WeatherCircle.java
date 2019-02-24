package pixedar.com.krydetablet;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WeatherCircle extends GLSurfaceView {
    public WeatherPatternsRenderer weatherPatternsRenderer;
    public Renderer renderer;
    public WeatherHarmony weatherHarmony;

    public void setRenderMode(boolean renderMode) {
        this.renderMode = renderMode;
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
/*        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        if(renderMode){
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }else{
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }*/
    }

    boolean renderMode = true;
    public WeatherCircle(Context context) {
        super(context);
        setEGLContextClientVersion(1);
        weatherPatternsRenderer = new WeatherPatternsRenderer();
        weatherHarmony =  new WeatherHarmony(this);
        renderer = new Renderer();
        setRenderer(renderer);
     //   setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setListener(WeatherDataController weatherDataController) {
       weatherPatternsRenderer.setListener(weatherDataController);
       weatherHarmony.setListener(weatherDataController);
    }

    public class Renderer implements GLSurfaceView.Renderer {

        public void onDrawFrame(GL10 gl10) {
            if(renderMode){
               weatherHarmony.draw(gl10);
            }else{
                weatherPatternsRenderer.draw(gl10);
            }
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

    }


}
