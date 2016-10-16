package book.BouncyCube1;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;
import book.BouncyCube1.*;

public class BouncyCube1Activity extends Activity 
{
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
     		GLSurfaceView view = new GLSurfaceView(this);      		
     		view.setEGLConfigChooser(8,8,8,8,16,1);
       		view.setRenderer(new CubeRenderer(true));
       		setContentView(view);
    }
}