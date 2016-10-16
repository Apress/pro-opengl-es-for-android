package book.SolarSystem;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;

public class Constellations 
{
	Outline m_Outline;
    Stars m_Stars;
    Context context;
    public String TAG = "myconst";
    
    public Constellations(Context myAppContxt) 
    {
    	this.context = myAppContxt;
    }
    
	public void init(GL10 gl, Context context)
	{
		
	    m_Outline = new Outline(context);
        m_Outline.init(gl, "outlines2.plist", 0.0f, 1.0f, 1.0f, .3f);
        
	    m_Stars = new Stars(context);
	    m_Stars.init(context, "stars.plist");
	}

	public void executeStars(GL10 gl)
	{	
		m_Stars.execute(gl);
		
	}
	
	public void executeOutlines(GL10 gl, boolean constOutlinesOn, boolean constNamesOn) {
		m_Outline.execute(gl, constOutlinesOn, constNamesOn);
	}

}
