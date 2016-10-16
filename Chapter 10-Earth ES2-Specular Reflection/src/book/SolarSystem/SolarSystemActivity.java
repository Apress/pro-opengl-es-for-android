package book.SolarSystem;


import book.SolarSystem.SolarSystemRendererES2;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.WindowManager;

public class SolarSystemActivity extends Activity 
{
    /** Called when the activity is first created. */
    @Override

    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
 		GLSurfaceView view = new GLSurfaceView(this);
        if (detectOpenGLES20()) 
        {
        	view.setEGLContextClientVersion(2);
     		view.setEGLConfigChooser(8,8,8,8,16,1);
        	view.setRenderer(new SolarSystemRendererES2(this));
        	
            setContentView(view);
        }  
    }

    private boolean detectOpenGLES20() {
        ActivityManager am =
            (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        return (info.reqGlEsVersion >= 0x20000);
    }

}