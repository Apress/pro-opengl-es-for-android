package book.SolarSystem;

import book.SolarSystem.SolarSystemView;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class SolarSystemActivity extends Activity 
{
    /** Called when the activity is first created. */
	GLSurfaceView mGLSurfaceView;
	Button b_name, b_line, b_lens_flare, b_sweep;
	public final static int VERTICAL_ORIENTATION = 1;
	LinearLayout ll;
    public String TAG = "mySSRenderer";
    public static boolean name_flag = true;
    public static boolean lens_flare_flag = true;
    public static boolean line_flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLSurfaceView = new SolarSystemView(this);
        setContentView(mGLSurfaceView);
        mGLSurfaceView.requestFocus();
        mGLSurfaceView.setFocusableInTouchMode(true);
        
        ll = new LinearLayout(this);
        ll.setOrientation(VERTICAL_ORIENTATION);
        b_name = new Button(this);        
        b_name.setText("names");
        b_name.setBackgroundDrawable(getResources().getDrawable(book.SolarSystem.R.drawable.bluebuttonbig));
        ll.addView(b_name);
        
        b_line = new Button(this);        
        b_line.setText("lines");      
        b_line.setBackgroundDrawable(getResources().getDrawable(book.SolarSystem.R.drawable.greenbuttonbig));
        ll.addView(b_line, 1);  
        
        b_lens_flare = new Button(this);
        b_lens_flare.setText("lens flare");
        b_lens_flare.setBackgroundDrawable(getResources().getDrawable(book.SolarSystem.R.drawable.redbuttonbig));
        ll.addView(b_lens_flare);
            
        this.addContentView(ll, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
        b_name.setOnClickListener(new Button.OnClickListener() 
        {

			@Override
			public void onClick(View v) 
			{					
				if (name_flag == false)
					name_flag = true;
				else if (name_flag == true)
					name_flag = false;
				Log.d(TAG, "b_name clicked:" + name_flag);
			}			 	
        });
        
        b_line.setOnClickListener(new Button.OnClickListener() 
        {
			@Override
			
			public void onClick(View v) 
			{				 
				if (line_flag == false)
					line_flag = true;
				else if (line_flag == true)
					line_flag = false;
				Log.d(TAG, "b_line clicked");
			}        	
        });
        
        b_lens_flare.setOnClickListener(new Button.OnClickListener() 
        {

			@Override
			public void onClick(View v) 
			{
				if (lens_flare_flag == false)
					lens_flare_flag = true;
				else if (lens_flare_flag == true)
					lens_flare_flag = false;		
				Log.d(TAG, "b_lensflare clicked");
			}        	
        });
      }
}