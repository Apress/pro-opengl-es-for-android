package book.SolarSystem;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class SolarSystemRenderer implements GLSurfaceView.Renderer 
{
    public final static int SS_SUNLIGHT = GL10.GL_LIGHT0; 
    public final static int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
    public final static int SS_FILLLIGHT2 = GL10.GL_LIGHT2;
	private Planet mPlanet;
	private float mTransY;
	private float mAngle;
	
    public SolarSystemRenderer() 
    {
    	mPlanet=new Planet(100,100,1.0f, 1.0f);
    }
    
    public void onDrawFrame(GL10 gl) 
    {
         gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
         gl.glClearColor(0.5f,0.5f,0.5f,1.0f);
         gl.glMatrixMode(GL10.GL_MODELVIEW);
         gl.glLoadIdentity();
    	
         gl.glTranslatef(0.0f,0.0f, -4.0f);
	
         mPlanet.draw(gl);
    }
	
	public void onSurfaceChanged(GL10 gl, int width, int height) {
	     gl.glViewport(0, 0, width, height);
	
	     /*
	      * Set our projection matrix. This doesn't have to be done
	      * each time we draw, but usually a new projection needs to
	      * be set when the viewport is resized.
	      */
	     
	 	float aspectRatio;
		float zNear =.1f;
		float zFar =1000f;
		float fieldOfView = 30.0f/57.3f;
		float	size;
		
		gl.glEnable(GL10.GL_NORMALIZE);
		
		aspectRatio=(float)width/(float)height;				//h/w clamps the fov to the height, flipping it would make it relative to the width
		
		//Set the OpenGL projection matrix
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
		gl.glFrustumf(-size, size, -size/aspectRatio, size /aspectRatio, zNear, zFar);
		
		//Make the OpenGL modelview matrix the default
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
        gl.glDisable(GL10.GL_DITHER);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glCullFace(GL10.GL_BACK);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthMask(false);
        initLighting(gl);
    }

	private void initLighting(GL10 gl) 
	{
		float[] posMain={5.0f,4.0f,6.0f,1.0f};			
		float[] posFill1={-15.0f,15.0f,0.0f,1.0f};			
		float[] posFill2={-10.0f,-4.0f,1.0f,1.0f};			
			
		float[] white={1.0f,1.0f,1.0f,1.0f};			
		float[] red={1.0f,0.0f,0.0f,1.0f};
		float[] dimred={.5f,0.0f,0.0f,1.0f};			
			
		float[] green={0.0f,1.0f,0.0f,0.0f};
		float[] dimgreen={0.0f,.5f,0.0f,0.0f};			
		float[] blue={0.0f,0.0f,1.0f,1.0f};			
		float[] dimblue={0.0f,0.0f,.2f,1.0f};			
			
		float[] cyan={0.0f,1.0f,1.0f,1.0f};			
		float[] yellow={1.0f,1.0f,0.0f,1.0f};
		float[] magenta={1.0f,0.0f,1.0f,1.0f};			
		float[] dimmagenta={.75f,0.0f,.25f,1.0f};			
			
		float[] dimcyan={0.0f,.5f,.5f,1.0f};			
				
		//Lights go here.
			
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(posMain));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));		
			
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));	
			
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
			
		gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION, .005f);
			
		//Materials go here.
			
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));
			
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);				
			
		gl.glShadeModel(GL10.GL_SMOOTH);				
		gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
			
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(SS_SUNLIGHT);
		gl.glEnable(SS_FILLLIGHT1);
		gl.glEnable(SS_FILLLIGHT2);
			
		gl.glLoadIdentity(); 
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
}
