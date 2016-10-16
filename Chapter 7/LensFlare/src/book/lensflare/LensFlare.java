package book.lensflare;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.util.Log;
import book.lensflare.*;
import book.lensflare.LensFlareRenderer.CGPoint;
import book.lensflare.LensFlareRenderer.CGRect;
import book.lensflare.LensFlareRenderer.CGSize;

public class LensFlare 
{

	int[] m_Flares = new int[25];
	Flare[] myFlares = new Flare[25];
	public Context context;
	public String TAG = "MyACtivity";
	
	public void init() 
	{
		int i;
		for (i = 0; i< 24; i++)
			myFlares[i] = new Flare();
	}
	
	public void createFlares(GL10 gl, Context context)
	{
	    float ff=.004f;
	    int resid, i = 0;
	    this.context = context;
	    
	    init();
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[0] = myFlares[0].init(gl, context, resid, .5f, .05f-ff, 1.0f, .73f, .30f, .4f);

	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[1] = myFlares[1].init(gl, context, resid, 0.5f, .05f-ff, 1.0f, .73f, .50f, .4f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[2] = myFlares[2].init(gl, context, resid, 1.5f, .06f-ff, 0.75f, 1.00f, .40f, .2f);
	    
	    resid = book.lensflare.R.drawable.halo;
	    m_Flares[3] = myFlares[3].init(gl, context, resid, 0.5f, .03f-ff, 1.0f, .73f, .20f, .4f);
	    
	    resid = book.lensflare.R.drawable.halo;
	    m_Flares[4] = myFlares[4].init(gl, context, resid, 0.3f, .05f-ff, 1.0f, .85f, .40f, .4f);
	    
	    resid = book.lensflare.R.drawable.hexagon;
	    m_Flares[5] = myFlares[5].init(gl, context, resid, 0.3f, .065f-ff, 0.5f, .95f, .56f, .33f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[6] = myFlares[6].init(gl, context, resid, 0.3f, .03f-ff, 1.0f, .85f, .56f, .35f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[7] = myFlares[7].init(gl, context, resid, 0.35f, .05f-ff, 1.0f, .90f, .40f, .4f);

	    resid = book.lensflare.R.drawable.hexagon;
	    m_Flares[8] = myFlares[8].init(gl, context, resid, 1.5f, .07f-ff, 0.8f, .95f, .56f, .55f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[9] = myFlares[9].init(gl, context, resid, 0.3f, .06f-ff, 1.0f, .85f, .56f, .31f);
	    
	    resid = book.lensflare.R.drawable.halo;
	    m_Flares[10] = myFlares[10].init(gl, context, resid, 1.5f, .02f-ff, 1.0f, .85f, .56f, .4f);
	    
	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[11] = myFlares[11].init(gl, context, resid, 0.5f, .068f-ff, 1.0f, .85f, .56f, .45f);
	    
	    resid = book.lensflare.R.drawable.flarefuzzy;
	    m_Flares[12] = myFlares[12].init(gl, context, resid, 0.5f, .03f-ff, 1.0f, .85f, .56f, .45f);
	    
	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[13] = myFlares[13].init(gl, context, resid, 0.3f, .017f-ff, 1.0f, .85f, .56f, .4f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[14] = myFlares[14].init(gl, context, resid, 0.25f, .06f-ff, 1.0f, .85f, .56f, .3f);
	    
	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[15] = myFlares[15].init(gl, context, resid, 1.5f, .09f-ff, 1.0f, .85f, .56f, .44f);
	    
	    resid = book.lensflare.R.drawable.hexagon;
	    m_Flares[16] = myFlares[16].init(gl, context, resid, 0.56f, .065f-ff, 0.5f, .95f, .56f, .33f);
	    
	    resid = book.lensflare.R.drawable.hexagon;
	    m_Flares[17] = myFlares[17].init(gl, context, resid, 0.56f, .071f-ff, 0.5f, .85f, .56f, .65f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[18] = myFlares[18].init(gl, context, resid, 1.0f, .01f-ff, 1.0f, .85f, .56f, .3f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[19] = myFlares[19].init(gl, context, resid, 0.25f, .03f-ff, 1.0f, .70f, .50f, .3f);
	    
	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[20] = myFlares[20].init(gl, context, resid, 1.5f, .02f-ff, 1.0f, .70f, .56f, .5f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[21] = myFlares[21].init(gl, context, resid, 1.0f, .018f-ff, 1.0f, .60f, .25f, .5f);
	    
	    resid = book.lensflare.R.drawable.hexagonblur;
	    m_Flares[22] = myFlares[22].init(gl, context, resid, 1.0f, .035f-ff, 1.0f, .75f, .56f, .35f);
	    
	    resid = book.lensflare.R.drawable.glow;
	    m_Flares[23] = myFlares[23].init(gl, context, resid, 10.0f, 0.0f-ff, 1.0f, .60f, .56f, 1.0f);

	    //the sun is on this side of the streak
	}

	static float deltaX=40.0f,deltaY=40.0f;
    static float offsetFromCenterX,offsetFromCenterY;
    static float startingOffsetFromCenterX,startingOffsetFromCenterY;
    static int counter=0;

	public void execute(GL10 gl,CGSize size, CGPoint source)
	{
	    int i;
	    float cx,cy;
	    float aspectRatio;

	    cx=(float) (size.width/2.0f);
	    cy=(float) (size.height/2.0f);

	    aspectRatio=cx/cy;
	    
	    startingOffsetFromCenterX = cx-source.x;
	    startingOffsetFromCenterY = (source.y-cy)/aspectRatio;

	    offsetFromCenterX = startingOffsetFromCenterX;
	    offsetFromCenterY = startingOffsetFromCenterY;
    
	    deltaX = (float) (2.0f * startingOffsetFromCenterX);
	    deltaY = (float) (2.0f * startingOffsetFromCenterY);
	    
	    for (i = 23; i >= 0; i--) 
	    {
	    	offsetFromCenterX -= deltaX * myFlares[i].getVectorPosition();
	        offsetFromCenterY -= deltaY * myFlares[i].getVectorPosition();

	    	myFlares[i].renderFlareAt(gl, m_Flares[i], offsetFromCenterX, offsetFromCenterY, size, this.context);    
	    }

	    counter++;
	}
}
