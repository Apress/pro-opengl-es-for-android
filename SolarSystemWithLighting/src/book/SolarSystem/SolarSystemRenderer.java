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
	public final static int X_VALUE = 0;
	public final static int Y_VALUE = 1;
	public final static int Z_VALUE = 2;
	Planet m_Earth;
	Planet m_Sun;
	float[] m_Eyeposition = {0.0f, 0.0f, 0.0f};

    public SolarSystemRenderer() 
    {
    	m_Eyeposition[X_VALUE] = 0.0f;               
    	m_Eyeposition[Y_VALUE] = 0.0f;
    	m_Eyeposition[Z_VALUE] = 5.0f;
    	            
    	m_Earth = new Planet(50, 50, .3f, 1.0f);     
    	m_Earth.setPosition(0.0f, 0.0f, -2.0f);   
    	            
    	m_Sun = new Planet(50, 50,1.0f, 1.0f);     
    	m_Sun.setPosition(0.0f, 0.0f, 0.0f);       

    }
    
	float angle = 0.0f;	

    public void onDrawFrame(GL10 gl)
	    {
	    float paleYellow[]={1.0f, 1.0f, 0.3f, 1.0f};               
	    float white[]={1.0f, 1.0f, 1.0f, 1.0f};            
	    float cyan[]={0.0f, 1.0f, 1.0f, 1.0f};    
	    float black[]={0.0f, 0.0f, 0.0f, 0.0f};        
	
	    float orbitalIncrement= 1.25f;                    
	    float[] sunPos={0.0f, 0.0f, 0.0f, 1.0f};                    
	
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	    gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
	    
	    gl.glPushMatrix();                              
	
	    gl.glTranslatef(-m_Eyeposition[X_VALUE], -m_Eyeposition[Y_VALUE],-m_Eyeposition[Z_VALUE]);
	                                                                                     
	
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));   
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));
	
	    gl.glPushMatrix();                           
	    angle+=orbitalIncrement;                      
	    gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);        
	    executePlanet(m_Earth, gl);                    
	    gl.glPopMatrix();                                         
	            
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(paleYellow));  
	                                                                                                                                                                                      //12
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(black));   
	    executePlanet(m_Sun, gl);                                                                                                                                //14
	            
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, makeFloatBuffer(black));  
	    gl.glPopMatrix();                                                                                                                                                   //16
    }

    private void executePlanet(Planet m_Planet, GL10 gl) 
    {
        float posX, posY, posZ;
        posX = m_Planet.m_Pos[0];                      
        posY = m_Planet.m_Pos[1];
        posZ = m_Planet.m_Pos[2];
            
        gl.glPushMatrix();
        gl.glTranslatef(posX, posY, posZ);           
        m_Planet.draw(gl);                                       
        gl.glPopMatrix();
    }

	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
	     
	 	float aspectRatio;
		float zNear =.1f;
		float zFar =1000f;
		float fieldOfView = 30.0f/57.3f;
		float	size;
		
	    gl.glViewport(0, 0, width, height);
		gl.glEnable(GL10.GL_NORMALIZE);
		
		aspectRatio=(float)width/(float)height;			
				
		gl.glMatrixMode(GL10.GL_PROJECTION);
		
		size = zNear * (float)(Math.tan((double)(fieldOfView/2.0f)));
		gl.glFrustumf(-size, size, -size/aspectRatio, size /aspectRatio, zNear, zFar);
				
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
        gl.glDepthMask(true);
        initLighting(gl);
    }

	private void initLighting(GL10 gl) 
	{
	    float[] sunPos={0.0f, 0.0f, 0.0f, 1.0f};            
	    float[] posFill1={-15.0f, 15.0f, 0.0f, 1.0f};            
	    float[] posFill2={-10.0f, -4.0f, 1.0f, 1.0f};            
	    float[] white={1.0f, 1.0f, 1.0f, 1.0f};            
	    float[] dimblue={0.0f, 0.0f, .2f, 1.0f};            
	    float[] cyan={0.0f, 1.0f, 1.0f, 1.0f};            
	    float[] yellow={1.0f, 1.0f, 0.0f, 1.0f};
	    float[] magenta={1.0f, 0.0f, 1.0f, 1.0f};            
	    float[] dimmagenta={.75f, 0.0f, .25f, 1.0f};            
	    float[] dimcyan={0.0f, .5f, .5f, 1.0f};            
	        
	    //Lights go here.
	        
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));        
	    
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));    

	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));

	    //Materials go here.
	        
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

	    gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION,.001f);
	    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);    
	    gl.glShadeModel(GL10.GL_SMOOTH);                
	    gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
	        
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
}
