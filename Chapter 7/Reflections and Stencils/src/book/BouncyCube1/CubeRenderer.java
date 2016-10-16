package book.BouncyCube1;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLSurfaceView;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class CubeRenderer implements GLSurfaceView.Renderer 
{
	float mOriginZ=-8.0f;
	
    public CubeRenderer(boolean useTranslucentBackground) 
    {
        mTranslucentBackground = useTranslucentBackground;
        mCube = new Cube();
    }

    public void onDrawFrame(GL10 gl) 
    {   	
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_STENCIL_BUFFER_BIT);
        gl.glClearColor(0.0f,0.0f,0.0f,1.0f);

        renderToStencil(gl);
                 
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    
        gl.glPushMatrix();
        
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glDisable(GL10.GL_DEPTH_TEST);
        
        //flip the image
        
        gl.glTranslatef(0.0f,((float)(Math.sin(-mTransY)/2.0f)-2.5f),mOriginZ);	
        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);

        gl.glScalef(1.0f, -1.0f, 1.0f);
        gl.glFrontFace(GL10.GL_CW);
        
        gl.glEnable(GL10.GL_BLEND); 
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR); 

        mCube.draw(gl);
        
        gl.glDisable(GL10.GL_BLEND); 

        gl.glPopMatrix();

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDisable(GL10.GL_STENCIL_TEST);
        
        //now the main image
        
        gl.glPushMatrix();
        gl.glScalef(1.0f, 1.0f, 1.0f);
        gl.glFrontFace(GL10.GL_CCW);
        
        gl.glTranslatef(0.0f,(float)(1.5f*(Math.sin(mTransY)/2.0f)+2.0f),mOriginZ);
        
        gl.glRotatef(mAngle, 0.0f, 1.0f, 0.0f);
        
        mCube.draw(gl);

        gl.glPopMatrix();

        mTransY+=.075f;
        mAngle+=.4f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
         gl.glViewport(0, 0, width, height);
        
     	 float aspectRatio;
    	 float zNear =0.1f;
    	 float zFar =1000f;
    	 float fieldOfView = 50.0f/57.3f;
    	 float	size;
    	
    	gl.glEnable(GL10.GL_NORMALIZE);
    	
    	aspectRatio=(float)width/(float)height;				//h/w clamps the fov to the height, flipping it would make it relative to the width
    	    	
    	gl.glMatrixMode(GL10.GL_PROJECTION);
    	
    	size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
    	gl.glFrustumf(-size, size, -size/aspectRatio, size /aspectRatio, zNear, zFar);
    	
    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {
         gl.glDisable(GL10.GL_DITHER);

         gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                 GL10.GL_FASTEST);

         if (mTranslucentBackground) 
         {
             gl.glClearColor(0,0,0,0);
         } 
         else 
         {
             gl.glClearColor(1,1,1,1);
         }
         gl.glEnable(GL10.GL_CULL_FACE);
         gl.glCullFace(GL10.GL_BACK);
         gl.glShadeModel(GL10.GL_SMOOTH);
         gl.glEnable(GL10.GL_DEPTH_TEST);
    }
    
 	public void renderToStencil(GL10 gl)
    {	
        gl.glEnable(GL10.GL_STENCIL_TEST);
        gl.glStencilFunc(GL10.GL_ALWAYS,1, 0xFFFFFFFF);
        gl.glStencilOp(GL10.GL_REPLACE, GL10.GL_REPLACE, GL10.GL_REPLACE);

        renderStage(gl);
            
        gl.glStencilFunc(GL10.GL_EQUAL, 1, 0xFFFFFFFF);
        gl.glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP,GL10.GL_KEEP);
    }
 	
 	public void renderStage(GL10 gl)
 	{
 
 	    float[] flatSquareVertices = 		
 	    {
 	        -1.0f,  0.0f, -1.0f,
 	         1.0f,  0.0f, -1.0f,
 	        -1.0f,  0.0f,  1.0f,
 	         1.0f,  0.0f,  1.0f
 	    };
	    
 	    FloatBuffer vertexBuffer;

 	    float[] colors= 
 	    {
 	       1.0f,   0.0f,  0.0f, 0.5f,
 	       1.0f,   0.0f,  0.0f, 1.0f,
 	       0.0f,   0.0f,  0.0f, 0.0f,
 	       0.5f,   0.0f,  0.0f, 0.5f
 	    };
 	    
 	    FloatBuffer colorBuffer;
      
 	    gl.glFrontFace(GL10.GL_CW);
 	    gl.glPushMatrix();
 	    gl.glTranslatef(0.0f,-2.0f,mOriginZ);	
 	    gl.glScalef(2.5f,1.5f,2.0f);
 	    
 	    gl.glVertexPointer(3, GL11.GL_FLOAT, 0,makeFloatBuffer(flatSquareVertices));
 	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	    
 	    gl.glColorPointer(4, GL11.GL_FLOAT, 0,makeFloatBuffer(colors));

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
 	    
 	    gl.glDepthMask(false);
	    gl.glColorMask(true,false,false, true);
 	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0, 4);
 	    gl.glColorMask(true,true,true,true);
 	    gl.glDepthMask(true);
 	    
 	    gl.glPopMatrix();
 	}

	 protected static FloatBuffer makeFloatBuffer(float[] arr) 
	 {
	      ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
	      bb.order(ByteOrder.nativeOrder());
	      FloatBuffer fb = bb.asFloatBuffer();
	      fb.put(arr);
	      fb.position(0);
	      return fb;
	 }
	 
    private boolean mTranslucentBackground;
    private Cube mCube;
    private float mTransY;
    private float mAngle;
}
