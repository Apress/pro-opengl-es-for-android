package book.BouncyCube1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;


import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

class FBODemoRenderer extends Activity implements GLSurfaceView.Renderer 
{
	private static final String TAG = "MyActivity";
	private Context	context;
	private boolean m_TranslucentBackground;
	boolean m_FBOSupported=false;
	FBOController m_FBOController;

	public FBODemoRenderer(boolean useTranslucentBackground, Context context) 
	{
		this.context = context;
	}

    private boolean checkIfContextSupportsExtension(GL10 gl, String extension) 
    {
        String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS) + " ";
        return extensions.indexOf(" " + extension + " ") >= 0;
    }
    
    public void onDrawFrame(GL10 gl) 
    {
    	if(m_FBOController==null)
    	{
    		gl.glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    	}
    	else
	  	    m_FBOController.drawInRect(gl);
	}
	 
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		gl.glViewport(0, 0, width, height);

		float aspectRatio;
		float zNear = 0.1f;
		float zFar = 1000f;
		float fieldOfView = 60.0f / 57.3f;
		float size;

		gl.glEnable(GL10.GL_NORMALIZE);

		aspectRatio = (float) width / (float) height; 

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		size = zNear * (float) (Math.tan((double) (fieldOfView / 2.0f)));
		gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio,
				zNear, zFar);

        m_FBOSupported=checkIfContextSupportsExtension(gl,"GL_OES_framebuffer_object");

		if(m_FBOSupported)
		{
			int resid = book.BouncyCube1.R.drawable.hedly;

			m_FBOController = new FBOController();	
			m_FBOController.init(gl, this.context, resid, width, height);
		}
		else
		{		
			//nothing to see here. move along.
		}

		gl.glMatrixMode(GL10.GL_MODELVIEW);	
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{		
		gl.glDisable(GL10.GL_DITHER);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		if (m_TranslucentBackground) 
		{
			gl.glClearColor(0, 0, 0, 0);
		} 
		else 
		{
			gl.glClearColor(1, 1, 1, 1);
		}
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
	}
	
	private ByteBuffer extract(Bitmap bmp) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight()
				* bmp.getWidth() * 4);
		bb.order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = bb.asIntBuffer();

		for (int y = bmp.getHeight() - 1; y > -1; y--) 
		{

			for (int x = 0; x < bmp.getWidth(); x++) 
			{
				int pix = bmp.getPixel(x, bmp.getHeight() - y - 1);
				int alpha = ((pix >> 24) & 0xFF);
				int red = ((pix >> 16) & 0xFF);
				int green = ((pix >> 8) & 0xFF);
				int blue = ((pix) & 0xFF);

				ib.put(red << 24 | green << 16 | blue << 8 | alpha);
			}
		}
		bb.position(0);
		return bb;
	}

	protected static IntBuffer makeIntBuffer(int[] arr) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer fb = bb.asIntBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
}
