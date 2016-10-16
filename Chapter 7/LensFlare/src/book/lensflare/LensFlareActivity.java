package book.lensflare;

//import book.BouncyCube1.CubeRenderer;
import book.lensflare.LensFlareRenderer;
import android.R;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;
import android.view.WindowManager;

public class LensFlareActivity extends Activity {

	/** Called when the activity is first created. */
	GLSurfaceView mGLSurfaceView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);   
    	 
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                 WindowManager.LayoutParams.FLAG_FULLSCREEN);
         
         /*
         mGLSurfaceView = new GLSurfaceView(this);
    	 mGLSurfaceView.setRenderer(new LensFlareRenderer(this));
         setContentView(mGLSurfaceView);
         mGLSurfaceView.requestFocus();
         mGLSurfaceView.setFocusableInTouchMode(true);
    	 */
         
         mGLSurfaceView = new LensFlareRenderer(this);
         setContentView(mGLSurfaceView);
         mGLSurfaceView.requestFocus();
         mGLSurfaceView.setFocusableInTouchMode(true);
    }
}