package book.lensflare;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

public class LensFlareRenderer extends GLSurfaceView 
{
    private static String        LOG_TAG          = "EmulatorView";

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private final float TRACKBALL_SCALE_FACTOR = 36.0f;
    private LensRenderer mRenderer;
    private float mPreviousX;
    private float mPreviousY;
	CGPoint m_PointerLocation = new CGPoint();
	int m_FlareSource, flag = 0;
    LensFlare	m_LensFlare = new LensFlare();
	CreateTexture CT = new CreateTexture();
	public String TAG="myActivity";

	public LensFlareRenderer(Context context) 
	{
		
		 super(context);
	     mRenderer = new LensRenderer(context);
            
		boolean translucent;
		int depth=0;
		int stencil=0;
		
        setEGLConfigChooser(8,8,8,8,16,0);
/*	        
		if(depth>0)
			translucent=true;
		else
			translucent=false;
		
	     setEGLConfigChooser(translucent ?
                    new ConfigChooser(8, 8, 8, 8, depth, stencil) :
                    new ConfigChooser(5, 6, 5, 0, depth, stencil));
*/	        
	    getHolder().setFormat(PixelFormat.RGBA_8888);
	    setRenderer(mRenderer);
	}
	@Override public boolean onTrackballEvent(MotionEvent e) 
	{
	        m_PointerLocation.x=e.getX();
	        m_PointerLocation.y=e.getY();
	        
	        return true;
	}

    @Override public boolean onTouchEvent(MotionEvent e) 
    {
        m_PointerLocation.x=e.getX();
        m_PointerLocation.y=e.getY();
        
        return true;
    }
	    
	public class CGPoint 
	{
		float x;
		float y;
	};
	
	public class CGSize {
		float height;
		float width;
	};
	public class CGRect {
		CGPoint point;
		CGSize size;
	};

	public void CGRect(CGRect frame) {
		frame.point.x = 0;
	    frame.point.y = 0;
	    frame.size.height = 0;
	    frame.size.width = 0;	   
	}

	private class LensRenderer implements GLSurfaceView.Renderer {
        private Context context;

    	public LensRenderer(Context applicationContext) {
    		this.context = applicationContext;
    	}

    	@Override
    	public void onDrawFrame(GL10 gl) 
    	{
    		drawInRect(gl);
    	}
    	
    	int i = 0;
    	public void drawInRect(GL10 gl)
    	{
    		
    	    CGPoint centerRelative = new CGPoint();
    	    CGPoint windowDefault = new CGPoint();
    	    CGSize 	windowSize = new CGSize();
    	    float cx,cy;
    	    float aspectRatio;
    	    
    	   // CGRect frame = new CGRect();
    	    
    	    DisplayMetrics display = context.getResources().getDisplayMetrics();
    	    windowSize.width = display.widthPixels;
    	    windowSize.height = display.heightPixels;
    	    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    	    
    	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    	    cx=windowSize.width/2.0f;   	    
    	    cy=windowSize.height/2.0f;
    	    
    	    aspectRatio=cx/cy;
    	    
    	    centerRelative.x = m_PointerLocation.x-cx;// - windowsDefault.x;
    	    centerRelative.y =(cy-m_PointerLocation.y)/aspectRatio; //windowsDefault.y - m_PointerLocation.y;   //2
    	    
    	    CT.renderTextureAt(gl, centerRelative.x, centerRelative.y, windowSize, m_FlareSource, 3.0f, 1.0f, 1.0f, 1.0f, 1.0f);
     	    m_LensFlare.execute(gl, windowSize, m_PointerLocation);
    	}

    	@Override
    	public void onSurfaceChanged(GL10 gl, int width, int height) 
    	{
    		gl.glViewport(0, 0, width, height);

    
    		float aspectRatio;
    		float zNear = 0.1f;
    		float zFar = 1000f;
    		float fieldOfView = 50.0f / 57.3f;
    		float size;

    		gl.glEnable(GL10.GL_NORMALIZE);

    		aspectRatio = (float) width / (float) height; // h/w clamps the fov to
    														// the height, flipping
    														// it would make it
    														// relative to the width

    		// Set the OpenGL projection matrix

    		gl.glMatrixMode(GL10.GL_PROJECTION);

    		size = zNear * (float) (Math.tan((double) (fieldOfView / 2.0f)));
    		gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio,
    				zNear, zFar);

    		// Make the OpenGL modelview matrix the default

    		gl.glMatrixMode(GL10.GL_MODELVIEW);
    	}


    	@Override
    	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    	{
    		int resid, i = 0;
    		
    		gl.glDisable(GL10.GL_DITHER);

    		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

    		gl.glEnable(GL10.GL_CULL_FACE);
    		gl.glCullFace(GL10.GL_BACK);
    		gl.glShadeModel(GL10.GL_SMOOTH);
    		gl.glEnable(GL10.GL_DEPTH_TEST);

    		   		
    		resid = book.lensflare.R.drawable.gimpsun3;
    		m_FlareSource = CT.createTexture(gl, this.context, true, resid);

	        m_LensFlare.createFlares(gl, this.context);
	    }
	}
	
    private static class ConfigChooser implements GLSurfaceView.EGLConfigChooser
    {
        // Subclasses can adjust these values:
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];
        
        public ConfigChooser(int r, int g, int b, int a, int depth,
                int stencil)
      {
           mRedSize = r;
           mGreenSize = g;
           mBlueSize = b;
           mAlphaSize = a;
           mDepthSize = depth;
           mStencilSize = stencil;
      }
        /*
         * This EGL config specification is used to specify 2.0 rendering. We
         * use a minimum size of 4 bits for red/green/blue, but will perform
         * actual matching in chooseConfig() below.
         */
        private static int   EGL_OPENGL_ES2_BIT = 4;
        private static int[] s_configAttribs2   =
                                                {
                                                          EGL10.EGL_RED_SIZE,
                                                          4,
                                                          EGL10.EGL_GREEN_SIZE,
                                                          4,
                                                          EGL10.EGL_BLUE_SIZE,
                                                          4,
                                                          EGL10.EGL_RENDERABLE_TYPE,
                                                          EGL_OPENGL_ES2_BIT,
                                                          EGL10.EGL_NONE
                                                };


        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
        {

             /*
              * Get the number of minimally matching EGL configurations
              */
             int[] num_config = new int[1];
             egl.eglChooseConfig(display, s_configAttribs2, null, 0,
                       num_config);

             int numConfigs = num_config[0];

             if (numConfigs <= 0)
             {
                  throw new IllegalArgumentException(
                            "No configs match configSpec");
             }

             /*
              * Allocate then read the array of minimally matching EGL configs
              */
             EGLConfig[] configs = new EGLConfig[numConfigs];
             egl.eglChooseConfig(display, s_configAttribs2, configs,
                       numConfigs, num_config);

             if (true)
             {
                  printConfigs(egl, display, configs);
             }
             /*
              * Now return the "best" one
              */
             return chooseConfig(egl, display, configs);
        }


        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                  EGLConfig[] configs)
        {
             for (EGLConfig config : configs)
             {
                  int d = findConfigAttrib(egl, display, config,
                            EGL10.EGL_DEPTH_SIZE, 0);
                  int s = findConfigAttrib(egl, display, config,
                            EGL10.EGL_STENCIL_SIZE, 0);

                  // We need at least mDepthSize and mStencilSize bits
                  if (d < mDepthSize || s < mStencilSize)
                       continue;

                  // We want an *exact* match for red/green/blue/alpha
                  int r = findConfigAttrib(egl, display, config,
                            EGL10.EGL_RED_SIZE, 0);
                  int g = findConfigAttrib(egl, display, config,
                            EGL10.EGL_GREEN_SIZE, 0);
                  int b = findConfigAttrib(egl, display, config,
                            EGL10.EGL_BLUE_SIZE, 0);
                  int a = findConfigAttrib(egl, display, config,
                            EGL10.EGL_ALPHA_SIZE, 0);

                  if (r == mRedSize && g == mGreenSize && b == mBlueSize
                            && a == mAlphaSize)
                       return config;
             }
             return null;
        }


        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                  EGLConfig config, int attribute, int defaultValue)
        {

             if (egl.eglGetConfigAttrib(display, config, attribute, mValue))
             {
                  return mValue[0];
             }
             return defaultValue;
        }


        private void printConfigs(EGL10 egl, EGLDisplay display,
                  EGLConfig[] configs)
        {
             int numConfigs = configs.length;
             for (int i = 0; i < numConfigs; i++)
             {
                  Log.w(LOG_TAG, String.format("Configuration %d:\n", i));
                  printConfig(egl, display, configs[i]);
             }
        }


        private void printConfig(EGL10 egl, EGLDisplay display,
                  EGLConfig config)
        {
             int[] attributes = {
                       EGL10.EGL_BUFFER_SIZE,
                       EGL10.EGL_ALPHA_SIZE,
                       EGL10.EGL_BLUE_SIZE,
                       EGL10.EGL_GREEN_SIZE,
                       EGL10.EGL_RED_SIZE,
                       EGL10.EGL_DEPTH_SIZE,
                       EGL10.EGL_STENCIL_SIZE,
                       EGL10.EGL_CONFIG_CAVEAT,
                       EGL10.EGL_CONFIG_ID,
                       EGL10.EGL_LEVEL,
                       EGL10.EGL_MAX_PBUFFER_HEIGHT,
                       EGL10.EGL_MAX_PBUFFER_PIXELS,
                       EGL10.EGL_MAX_PBUFFER_WIDTH,
                       EGL10.EGL_NATIVE_RENDERABLE,
                       EGL10.EGL_NATIVE_VISUAL_ID,
                       EGL10.EGL_NATIVE_VISUAL_TYPE,
                       0x3030, // EGL10.EGL_PRESERVED_RESOURCES,
                       EGL10.EGL_SAMPLES,
                       EGL10.EGL_SAMPLE_BUFFERS,
                       EGL10.EGL_SURFACE_TYPE,
                       EGL10.EGL_TRANSPARENT_TYPE,
                       EGL10.EGL_TRANSPARENT_RED_VALUE,
                       EGL10.EGL_TRANSPARENT_GREEN_VALUE,
                       EGL10.EGL_TRANSPARENT_BLUE_VALUE,
                       0x3039, // EGL10.EGL_BIND_TO_TEXTURE_RGB,
                       0x303A, // EGL10.EGL_BIND_TO_TEXTURE_RGBA,
                       0x303B, // EGL10.EGL_MIN_SWAP_INTERVAL,
                       0x303C, // EGL10.EGL_MAX_SWAP_INTERVAL,
                       EGL10.EGL_LUMINANCE_SIZE,
                       EGL10.EGL_ALPHA_MASK_SIZE,
                       EGL10.EGL_COLOR_BUFFER_TYPE,
                       EGL10.EGL_RENDERABLE_TYPE,
                       0x3042 // EGL10.EGL_CONFORMANT
             };
             String[] names = {
                       "EGL_BUFFER_SIZE",
                       "EGL_ALPHA_SIZE",
                       "EGL_BLUE_SIZE",
                       "EGL_GREEN_SIZE",
                       "EGL_RED_SIZE",
                       "EGL_DEPTH_SIZE",
                       "EGL_STENCIL_SIZE",
                       "EGL_CONFIG_CAVEAT",
                       "EGL_CONFIG_ID",
                       "EGL_LEVEL",
                       "EGL_MAX_PBUFFER_HEIGHT",
                       "EGL_MAX_PBUFFER_PIXELS",
                       "EGL_MAX_PBUFFER_WIDTH",
                       "EGL_NATIVE_RENDERABLE",
                       "EGL_NATIVE_VISUAL_ID",
                       "EGL_NATIVE_VISUAL_TYPE",
                       "EGL_PRESERVED_RESOURCES",
                       "EGL_SAMPLES",
                       "EGL_SAMPLE_BUFFERS",
                       "EGL_SURFACE_TYPE",
                       "EGL_TRANSPARENT_TYPE",
                       "EGL_TRANSPARENT_RED_VALUE",
                       "EGL_TRANSPARENT_GREEN_VALUE",
                       "EGL_TRANSPARENT_BLUE_VALUE",
                       "EGL_BIND_TO_TEXTURE_RGB",
                       "EGL_BIND_TO_TEXTURE_RGBA",
                       "EGL_MIN_SWAP_INTERVAL",
                       "EGL_MAX_SWAP_INTERVAL",
                       "EGL_LUMINANCE_SIZE",
                       "EGL_ALPHA_MASK_SIZE",
                       "EGL_COLOR_BUFFER_TYPE",
                       "EGL_RENDERABLE_TYPE",
                       "EGL_CONFORMANT"
             };
             int[] value = new int[1];
 //            for (int i = 0; i < attributes.length; i++)
            	 
             //only use the first 10 values
             
             for (int i = 0; i<10; i++)
             {
                  int attribute = attributes[i];
                  String name = names[i];
                  if (egl.eglGetConfigAttrib(display, config, attribute,
                            value))
                  {                         
                       Log.w(LOG_TAG, String.format("  %s: %d\n", name,
                                 value[0]));
                  }
                  else
                  {
                       // Log.w(TAG, String.format("  %s: failed\n", name));
                       while (egl.eglGetError() != EGL10.EGL_SUCCESS)
                            ;
                  }
             }
        }

    }
}


