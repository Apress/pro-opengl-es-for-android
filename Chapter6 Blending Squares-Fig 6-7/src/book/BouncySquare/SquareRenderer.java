
package book.BouncySquare;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.opengl.GLSurfaceView;
import android.opengl.GLES11Ext;

import java.lang.Math;


class SquareRenderer implements GLSurfaceView.Renderer 
{
    public SquareRenderer(boolean useTranslucentBackground) 
    {
        float squareColorsYMCA[] = 
        {
        	 1.0f, 1.0f, 0.0f, 0.5f,
        	 0.0f, 1.0f, 1.0f, 0.5f,
        	 0.0f, 0.0f, 0.0f, 0.5f,
        	 1.0f, 0.0f, 1.0f, 0.5f
        };
        			 
        float squareColorsRGBA[] = 
        {
        	 1.0f, 0.0f, 0.0f, 0.5f,
        	 0.0f, 1.0f, 0.0f, 0.5f,
        	 0.0f, 0.0f, 1.0f, 0.5f,
        	 1.0f, 1.0f, 1.0f, 0.5f
        };
        
        mTranslucentBackground = useTranslucentBackground;	
        mSquareYMCA = new Square(squareColorsYMCA);
        
        mSquareRGBA = new Square(squareColorsRGBA);

    }

    public void onDrawFrame(GL10 gl) 
    {       
    	gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
        
       //SQUARE 1
        
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);
        mSquareYMCA.draw(gl);
        
        //SQUARE 2
        
		gl.glLoadIdentity();
		gl.glTranslatef( (float)(Math.sin(mTransY)/2.0f),0.0f, -2.9f);

		mSquareRGBA.draw(gl);

        mTransY += .075f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
         gl.glViewport(0, 0, width, height);

         float ratio = (float) width / height;
         gl.glMatrixMode(GL11.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {
         gl.glDisable(GL11.GL_DITHER);

         gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
                 GL11.GL_FASTEST);

         if (mTranslucentBackground) {
             gl.glClearColor(.0f,.5f,.5f,1.0f);
         } else {
             gl.glClearColor(1,1,1,1);
         }
         gl.glEnable(GL11.GL_CULL_FACE);
         gl.glShadeModel(GL11.GL_SMOOTH);
         
		 gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
         
         gl.glDisable(GL11.GL_DEPTH_TEST);
         gl.glEnable(GL11.GL_BLEND);
    }
    
    private boolean mTranslucentBackground;
    private Square mSquareYMCA;
    private Square mSquareRGBA;
    private float mTransY;
    private float mAngle;
}
