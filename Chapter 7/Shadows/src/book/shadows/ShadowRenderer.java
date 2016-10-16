package book.shadows;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLSurfaceView;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class ShadowRenderer implements GLSurfaceView.Renderer 
{
	float mOriginZ=-8.0f;
    float mSpinX=-1.0f;
    float mSpinY=0.0f;
    float mSpinZ=0.0f;
    float mWorldY=-1.0f;
    float mWorldZ=-20.0f;
    float mWorldRotationX=35.0f;
    float mWorldRotationY=0.0f;
    float mLightRadius=2.5f;
    int mFrameNumber=0;
    boolean mLightsOnFlag=true;
    int mLightsOnFlagTrigger=300;
    float mLightPosX;
    float mLightPosY=10.0f;
    float mLightPosZ;
    float mLightHight=15.0f;
	float mMinY=5.0f;
    float mLightAngle=0.0f;
    float[] mLightPos;
    float[] mShadowMat;

    public ShadowRenderer(boolean useTranslucentBackground) 
    {
        mTranslucentBackground = useTranslucentBackground;
        
        mShadowMat=new float[16];
        mLightPos=new float[3];
        
        mCube = new Cube();
    }

    public void calculateShadowMatrix()
    {
        float[] shadowMat_local = 
        {  
            mLightPosY,     0.0f,         0.0f,         0.0f, 
           -mLightPosX,     0.0f,  -mLightPosZ,        -1.0f, 
                  0.0f,     0.0f,   mLightPosY,         0.0f, 
                  0.0f,     0.0f,         0.0f,   mLightPosY 
        };
        
        for (int i=0;i<16;i++)
        {
            mShadowMat[i] = shadowMat_local[i];                      
        }
    }
    
    public void drawShadow(GL10 gl,boolean wireframe)
    {
    	FloatBuffer vertexBuffer;
    	
        gl.glPushMatrix();     
        
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glRotatef(mWorldRotationX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(mWorldRotationY, 0.0f, 1.0f, 0.0f);
      
        gl.glMultMatrixf(makeFloatBuffer(mShadowMat)); // Multiply shadow matrix with current
        
        //place the shadows
       
        gl.glTranslatef(0.0f,(float)(Math.sin(mTransY)/2.0)+mMinY, 0.0f);
      
        gl.glRotatef((float)mSpinZ,0.0f,0.0f,1.0f);
        gl.glRotatef((float)mSpinY,0.0f,1.0f,0.0f);
        gl.glRotatef((float)mSpinX,1.0f,0.0f,0.0f);
        
        //draw them 
        
        if(mFrameNumber>150)
        	mCube.drawShadow(gl,true);
        else
        	mCube.drawShadow(gl,false);
    	
        gl.glDisable(GL10.GL_BLEND);
        
        gl.glPopMatrix();      
    }
  
	private void initLighting(GL10 gl) 
	{
		//call this if you want the cube to really be lit, but it doesn't look that great
		
		float[] lightPos={0.0f,0.0f,0.0f,1.0f};			
		float[] white={1.0f,1.0f,1.0f,1.0f};			
		float[] cyan={0.0f,1.0f,1.0f,1.0f};					
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, makeFloatBuffer(lightPos));
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, makeFloatBuffer(white));

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, makeFloatBuffer(cyan));
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

		gl.glShadeModel(GL10.GL_SMOOTH);				
		gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);

	}
	
    public void onDrawFrame(GL10 gl) 
    {   	  		
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
        
        gl.glEnable(GL10.GL_DEPTH_TEST);	      
        
        updateLightPosition(gl);                                                       //3
        
        
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f,mWorldY,mWorldZ);                                          //5
        gl.glRotatef(mWorldRotationX, 1.0f, 0.0f, 0.0f); 
        gl.glRotatef(mWorldRotationY, 0.0f, 1.0f, 0.0f); 
        
        renderStage(gl);                                                                //6

        if(mFrameNumber>(mLightsOnFlagTrigger/2))                                       //7
        	mLightsOnFlag=false;
        else
        	mLightsOnFlag=true;
        
        if(mFrameNumber>mLightsOnFlagTrigger)
        	mFrameNumber=0;
        
//        drawLight(GL10.GL_LIGHT0);                                                    //8
        
        gl.glDisable(GL10.GL_DEPTH_TEST );                                              //9
        
        calculateShadowMatrix();                                                        //10
        
        drawShadow(gl,true);                                                                 //11
        
        gl.glShadeModel(GL10.GL_SMOOTH );                                                                          
        
        gl.glTranslatef(0.0f,(float)(Math.sin(mTransY)/2.0)+mMinY, 0.0f);               //14
        
        gl.glRotatef( mSpinZ, 0.0f, 0.0f, 1.0f );                                       //15
        gl.glRotatef( mSpinY, 0.0f, 1.0f, 0.0f );
        gl.glRotatef( mSpinX, 1.0f, 0.0f, 0.0f );
        
        gl.glEnable( GL10.GL_DEPTH_TEST );                                              //16
        gl.glFrontFace(GL10.GL_CCW);
       
 //       gl.glEnable(GL10.GL_LIGHTING );                                                //4

        mCube.draw(gl);
        
        gl.glDisable(GL10.GL_BLEND);
        
        mFrameNumber++;
        
        mSpinX+=.4f;
        mSpinY+=.6f;
        mSpinZ+=.9f;
        
        mTransY+=.075f;
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
 		float scale=3.0f;
 		
 	    float[] flatSquareVertices = 		
 	    {
 	        -1.0f,  -0.01f, -1.0f,
 	         1.0f,  -0.01f, -1.0f,
 	        -1.0f,  -0.01f,  1.0f,
 	         1.0f,  -0.01f,  1.0f
 	    };
	    
 	    FloatBuffer vertexBuffer;

 	    float[] colors = 
 	    {
 	       1.0f,   1.0f,  0.0f, 0.5f,
 	       1.0f,   0.0f,  0.0f, 1.0f,
 	       0.0f,   0.0f,  0.0f, 0.0f,
 	       0.5f,   0.0f,  0.0f, 0.5f
 	    };
 	    
 	    FloatBuffer colorBuffer;
       
 	    gl.glFrontFace(GL10.GL_CW);
 	    gl.glPushMatrix();
 	    
 	    gl.glTranslatef(0.0f,0.0f,0.0f);
 	    
 	    gl.glScalef(scale,scale,scale);
 	    
 	    gl.glVertexPointer(3, GL11.GL_FLOAT, 0,makeFloatBuffer(flatSquareVertices));
 	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    
 	    gl.glColorPointer(4, GL11.GL_FLOAT, 0,makeFloatBuffer(colors));
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
 	    
 	    gl.glDepthMask(false);
	    gl.glColorMask(true,true,false, true);
 	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP,0, 4);
 	    gl.glColorMask(true,true,true,true);
 	    gl.glDepthMask(true);
 	    
 	    gl.glPopMatrix();
 	}

	private void updateLightPosition(GL10 gl) 
	{
	    mLightAngle +=1.0f;                //in degrees
	
	    mLightPosX   = (float) (mLightRadius * Math.cos(mLightAngle/57.29f));
	    mLightPosY   = mLightHight;
	    mLightPosZ   = (float) (mLightRadius * Math.sin(mLightAngle/57.29f));
	  
	    //   iLightPos[0] = iLightPosX;
	    mLightPos[1] = mLightPosY;
	    //   iLightPos[2] = iLightPosZ;
	    
	    mLightPos[0]=mLightPosX;
	    mLightPos[2]=mLightPosZ;
	    
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, makeFloatBuffer(mLightPos));
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
