package book.BouncyCube1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;



public class FBOController 
{
	public Context context;
	int[] m_FBO1 = new int[3];
	int[] m_FBOTexture = new int[1];
	public String TAG = "MyActivity";
	int[] originalFBO = new int[1];
    int[] depthBuffer = new int[1];
    int m_ImageTexture;					//the texture of the embedded image
	static float m_TransY = 0.0f;
	static float m_RotX = 0.0f;
	static float m_RotZ = 0.0f;
	static float m_Z = -1.5f;
	int[] m_DefaultFBO = new int[1];
	int m_Counter=0;
	boolean m_FullScreen=false;
    
	public int init(GL10 gl, Context contextRegf,int resource, int width, int height)
	{
		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

	       //Cache the original FBO, and restore it later.
	    
		gl11ep.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, makeIntBuffer(originalFBO));     //1
	    
		gl11ep.glGenRenderbuffersOES(1, makeIntBuffer(depthBuffer));                                                //2
		gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, depthBuffer[0]);
	 
		gl11ep.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,                          //3
	        		GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
	    
		//Make the texture to render to.
	        
		gl.glGenTextures(1, m_FBOTexture, 0);                                                                        //4
		gl.glBindTexture(GL10.GL_TEXTURE_2D, m_FBOTexture[0]);
	        
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, width, height, 0, 
				GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5,null);
	    
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
	    	
	    //Now create the actual FBO.
	    
		gl11ep.glGenFramebuffersOES(3, m_FBO1,0);
	        
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, m_FBO1[0]);
	    
		// Attach the texture to the FBO.
	    
		gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,                        //6
	          GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, m_FBOTexture[0], 0);
	    
		// Attach the depth buffer we created earlier to our FBO.
	    
		gl11ep.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,                   //7
				GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,GL11ExtensionPack.GL_RENDERBUFFER_OES, depthBuffer[0]);
	    
	   // Check that our FBO creation was successful.

	    gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);                   //8
	    
	    int uStatus = gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);

	    if(uStatus != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES)
	    {
	       	return 0;
	    }
	    
        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, originalFBO[0]);        //9
	    
        //and while we're at it, do the image texture as well
	        
        m_ImageTexture = createTexture(gl,contextRegf,resource); 
        
        return 1;
	}

	public int getFBOName() // 10
	{
		return m_FBO1[0];
	}

	public int getTextureName() // 11
	{
		return m_FBOTexture[0];
	}

	public void drawInRect(GL10 gl)
	{
		GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;
		
	    float squareVertices[] = 		
	    {
	       -0.5f, -0.5f, 0.0f,
	        0.5f, -0.5f, 0.0f,
	       -0.5f,  0.5f, 0.0f,
	        0.5f,  0.5f, 0.0f
	    };
	    
	    float fboVertices[] = 		                                                    //1
	    {
	       -0.5f, -0.75f, 0.0f,
	        0.5f, -0.75f, 0.0f,
	       -0.5f,  0.75f, 0.0f,
	        0.5f,  0.75f, 0.0f
	    };
	    
	    float textureCoords1[] = 
	    {				
	        0.0f, 0.0f,
	        1.0f, 0.0f,
	        0.0f, 1.0f,
	        1.0f, 1.0f
	    };

	    if((m_Counter%250)==0)
	    {
	    	if(m_FullScreen)
	    		m_FullScreen=false;
	    	else
	    		m_FullScreen=true;
	    }
	    
        gl.glDisable(GL10.GL_CULL_FACE);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        
	    if(m_DefaultFBO[0] == 0) 
	    {
	        gl11.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, makeIntBuffer(m_DefaultFBO));  //2
	    }
	    
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY | GL10.GL_DEPTH_BUFFER_BIT);
	    
	    gl.glEnable(GL10.GL_TEXTURE_2D); 

	    //Draw to the off-screen FBO first.
	    
	    if(!m_FullScreen)
	    {           
	    	gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, m_FBO1[0]);   
	    }
	    
	    gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
	    
	    gl.glPushMatrix();
	    
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glLoadIdentity();
	    	    
	    gl.glTranslatef(0.0f, (float)(Math.sin(m_TransY)/2.0f),m_Z);

	    gl.glRotatef(m_RotZ, 0.0f, 0.0f, 1.0f);                                         
	    
	    gl.glBindTexture(GL10.GL_TEXTURE_2D,m_ImageTexture);				
	    
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, makeFloatBuffer(textureCoords1));
	    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(squareVertices));
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    	    
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);                                //5
	    	    
	    gl.glPopMatrix();
	    
	    //now draw the offscreen frame buffer into another framebuffer
	
	    if(!m_FullScreen)
	    {
		    gl.glPushMatrix();

		    gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, m_DefaultFBO[0]);    //6
           
            gl.glMatrixMode(GL10.GL_MODELVIEW);
		    gl.glLoadIdentity();   
		                                                                
		    gl.glTranslatef(0.0f, (float)(Math.sin(m_TransY)/2.0f), m_Z);
		    gl.glRotatef(m_RotX, 1.0f, 0.0f, 0.0f);                                       
	
		    gl.glBindTexture(GL10.GL_TEXTURE_2D, m_FBOTexture[0]);	     			
		    
		    gl.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);                            
		    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		    
		    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(textureCoords1));
		    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		    
		    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(fboVertices));                                                         //9
		    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    
		    gl.glPopMatrix();
	    }
	    
	    m_TransY += 0.025f;
	    m_RotX+=1.0f;
	    m_RotZ+=1.0f;
	    m_Counter++;
	}

    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
    	int[] textures = new int[1];
    	
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); 

    	gl.glGenTextures(1, textures, 0); 
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); 
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); 
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 	

    	tempImage.recycle();//6

    	gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
    	
    	return textures[0];
    }
    
	protected FloatBuffer makeFloatBuffer(float[] arr) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	
	protected IntBuffer makeIntBuffer(int[] arr) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer fb = bb.asIntBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
}
