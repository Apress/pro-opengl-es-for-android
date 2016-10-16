package com.SolarSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

class SolarSystemRenderer implements GLSurfaceView.Renderer 
{
	private float mTransY;
	private float mAngle;
	private Planet mPlanet;
	
	private Planet m_Earth;
	private long m_Frametime=0;
	private long m_LastTime=0;
	private long m_LastFrameNumber=0;
	private long m_FrameNumber=0;
	private Planet m_Sun;
	private float[] m_Eyeposition = {0.0f, 0.0f, 0.0f};
	private boolean mTranslucentBackground;
	public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;
	public final static int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
	public final static int SS_FILLLIGHT2 = GL10.GL_LIGHT2;
	public final static int X_VALUE	= 0;
	public final static int Y_VALUE = 1;
	public final static int Z_VALUE = 2;
	
	public Context myContext;
	
	public SolarSystemRenderer(Context context) 
	{
		this.myContext = context;
	}

	private void initGeometry(GL10 gl) 
	{
		int resid;
		m_Eyeposition[X_VALUE] = 0.0f;					
		m_Eyeposition[Y_VALUE] = 1.15f;
		m_Eyeposition[Z_VALUE] = 10.0f;
			
		resid =  com.SolarSystem.R.drawable.earth_light;//1
		m_Earth = new Planet(100, 100, 1.0f, 1.0f, gl, myContext, true, resid);//2
		m_Earth.setPosition(0.0f, 0.0f, 0.0f);				
	}

	private void initLighting(GL10 gl) 
	{
		float[] sunPos={10.0f,2.0f,0.0f,1.0f};			
		float[] posFill1={-15.0f,15.0f,0.0f,1.0f};			
		float[] posFill2={-10.0f,-4.0f,1.0f,1.0f};			

		float[] white={1.0f,1.0f,1.0f,1.0f};			
		float[] dimblue={0.0f,0.0f,.2f,1.0f};			

		float[] cyan={0.0f,1.0f,1.0f,1.0f};			
		float[] yellow={1.0f,1.0f,0.0f,1.0f};
		float[] magenta={1.0f,0.0f,1.0f,1.0f};			
		float[] dimmagenta={.75f,0.0f,.25f,1.0f};			

		float[] dimcyan={0.0f,.5f,.5f,1.0f};			
		
		//lights go here
		
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));		
		
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));	

		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));

		//materials go here
		
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));
		
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);				

		gl.glShadeModel(GL10.GL_SMOOTH);				
		gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(SS_SUNLIGHT);
		gl.glEnable(SS_FILLLIGHT1);
		gl.glEnable(SS_FILLLIGHT2);
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

	 public void executePlanet(Planet mPlanet, GL10 gl) 
	 {
		gl.glTranslatef(mPlanet.m_Pos[0], mPlanet.m_Pos[1], mPlanet.m_Pos[2]);				
		mPlanet.draw(gl);						
	 }

	 float angle = 0.0f;	
	 public void onDrawFrame(GL10 gl)
	 {
	 	float[] paleYellow={1.0f,1.0f,0.3f,1.0f};			
		float[] white={1.0f,1.0f,1.0f,1.0f};			
		float[] cyan={0.0f,1.0f,1.0f,1.0f};	
		float[] black={0.0f,0.0f,0.0f,0.0f};			
			
		float orbitalIncrement=1.0f;				
		float[] sunPos={10.0f,0.0f,0.0f,1.0f};					

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	    gl.glClearColor(0.4f,0.0f,1.0f,1.0f);

	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    
        gl.glTranslatef(-m_Eyeposition[X_VALUE], -m_Eyeposition[Y_VALUE],	
				-m_Eyeposition[Z_VALUE]);
		
		gl.glLightfv(SS_SUNLIGHT,GL10.GL_POSITION, makeFloatBuffer(sunPos));		
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

		angle+=orbitalIncrement;					
		gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);					
		
		executePlanet(m_Earth, gl);	
		
        long time = SystemClock.uptimeMillis() % 4000L;
        long deltime=0;
        
        m_Frametime=SystemClock.uptimeMillis();
        
        deltime=m_Frametime-m_LastTime;
         
        if(deltime>1000)
        { 
        	long vertices=m_Earth.verticesPerUpdate();
        	float seconds=(float)(deltime/1000.0);
        	
        	long verticesSec=(long)(vertices/seconds);

        	float fps=(float)(m_FrameNumber-m_LastFrameNumber)/seconds;
             Log.i("FPS",": "+fps+", vps="+verticesSec);

             m_LastFrameNumber=m_FrameNumber;
             m_LastTime=m_Frametime;
        }
        
        m_FrameNumber++;

        float angle = 0.090f * ((int) time);
	 }

	 
	public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
	    gl.glViewport(0, 0, width, height);
	
	    float aspectRatio;
	    float zNear =.1f;
	    float zFar =1000;
	    float fieldOfView = 30.0f/57.3f;                                                                        //1
	    float size;
		
		gl.glEnable(GL10.GL_NORMALIZE);
		
		aspectRatio=(float)width/(float)height;                                              //2
	    	
		gl.glMatrixMode(GL10.GL_PROJECTION);                                             //3
		
		size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));    //4 
	
		gl.glFrustumf(-size, size, -size /aspectRatio,                                        //5
	                       size /aspectRatio, zNear, zFar);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);                                             //6
     }

	 public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	 {
		initGeometry(gl);
		initLighting(gl);
				
		gl.glDisable(GL10.GL_DITHER);                                                                             //16
		
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,                                 //17
		         GL10.GL_FASTEST);
		
		gl.glCullFace(GL10.GL_FRONT);
		
		if (!mTranslucentBackground)                                                                               //18
		{
		    gl.glClearColor(0, 0, 0, 0);                        
		}
		else 
		{
		     gl.glClearColor(1,1,1,1);
		}
		
		gl.glEnable(GL10.GL_CULL_FACE);                                                                       //19
		gl.glShadeModel(GL10.GL_SMOOTH);                                                                 //20
		gl.glEnable(GL10.GL_DEPTH_TEST);              
	 }
}
