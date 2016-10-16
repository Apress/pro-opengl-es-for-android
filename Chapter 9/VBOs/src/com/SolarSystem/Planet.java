package com.SolarSystem;

import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;


public class Planet 
{
    FloatBuffer m_VertexData;
    FloatBuffer m_NormalData;
    FloatBuffer m_ColorData;
    FloatBuffer m_TextureData;
    FloatBuffer m_InterleavedData;

	int[] m_VertexVBO=new int[1];
	int[] m_NormalVBO=new int[1];
	int[] m_TextureCoordsVBO=new int[1];
	int[] m_TextureIDs=new int[1];
	
    boolean m_UseNormals;
    long m_VerticesPerUpdate;
    float m_Scale;
    float m_Squash;
    float m_Radius;
    int m_Stacks, m_Slices;
    int m_NumVertices;
        
	public final static int NUM_XYZ_ELS = 3;
	public final static int NUM_NXYZ_ELS = 3;
	public final static int NUM_RGBA_ELS = 4;
	public final static int NUM_ST_ELS = 2;
	public final static int FLOAT_SIZE = 4;
	
    public float[] m_Pos = {0.0f, 0.0f, 0.0f};
	
    public Planet(int stacks, int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId) 
    {
        this.m_Stacks = stacks;
        this.m_Slices = slices;
        this.m_Radius = radius;
        this.m_Squash = squash;
        
        m_NumVertices=0;
        
        init(m_Stacks,m_Slices,radius,squash, gl, context, imageId, resourceId);
    }
    
    private void init(int stacks,int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId)		// 1
    {    	
    	float[] vertexData=null;
        float[] normalData=null;
        float[] colorData=null;
    	float[] textData=null;

        float colorIncrement=0f;
        	
        float blue=0f;
        float red=1.0f;

        int vIndex=0;				//vertex index
        int cIndex=0;				//color index
        int nIndex=0;				//normal index
        int tIndex=0;				//texture index
        	
    	if(imageId == true) 
    	{
        	m_TextureIDs[0]=createTexture(gl, context, resourceId);	//2
    	}
    	
        m_Scale=radius;						
        m_Squash=squash;
    				
        colorIncrement=1.0f/(float)stacks;
        
        m_Stacks = stacks;
        m_Slices = slices;
        		    				
        //Vertices
        
        vertexData = new float[ 3*((m_Slices*2+2) * m_Stacks)];
        		
        //Color data
        
        colorData = new float[ (4*(m_Slices*2+2) * m_Stacks)];
  		
    	//Normal pointers for lighting
        
        normalData = new float[3*((m_Slices*2+2) * m_Stacks)];

		if(imageId == true)		//3
			textData = new float [2 * ((m_Slices*2+2) * (m_Stacks))];
		
    	int	phiIdx, thetaIdx;
    		
    	//Latitude

    	for(phiIdx=0; phiIdx < m_Stacks; phiIdx++)		    		
    	{
    		//Starts at -1.57 and goes up to +1.57 radians.
    		///The first circle.
    		float phi0 = (float)Math.PI * ((float)(phiIdx+0) * (1.0f/(float)(m_Stacks)) - 0.5f);	
    			
    		//The next, or second one.
    		float phi1 = (float)Math.PI * ((float)(phiIdx+1) * (1.0f/(float)(m_Stacks)) - 0.5f);	

    		float cosPhi0 = (float)Math.cos(phi0);				
    		float sinPhi0 = (float)Math.sin(phi0);
    		float cosPhi1 = (float)Math.cos(phi1);
    		float sinPhi1 = (float)Math.sin(phi1);
    		
    		float cosTheta, sinTheta;
    		
    		//Longitude
    		for(thetaIdx=0; thetaIdx < m_Slices; thetaIdx++)			
    		{
    			//Increment along the longitude circle each "slice."
    			float theta = (float) (2.0f*(float)Math.PI * ((float)thetaIdx) * (1.0/(float)(m_Slices-1)));			
    			cosTheta = (float)Math.cos(theta);		
    			sinTheta = (float)Math.sin(theta);
    				
    			//We're generating a vertical pair of points, such 
    			//as the first point of stack 0 and the first point of 
    			//stack 1 above it. This is how TRIANGLE_STRIPS work, 
    			//taking a set of 4 vertices and essentially drawing two 
    			//triangles at a time. The first is v0-v1-v2, and the next
    			//is v2-v1-v3, etc.
    				
    			//Get x-y-z for the first vertex of stack.
    				
    			vertexData[vIndex]   = m_Scale*cosPhi0*cosTheta;
    			vertexData[vIndex+1] = m_Scale*(sinPhi0*m_Squash); 
    			vertexData[vIndex+2] = m_Scale*(cosPhi0*sinTheta); 
    				
    			vertexData[vIndex+3]   = m_Scale*cosPhi1*cosTheta;
    			vertexData[vIndex+4] = m_Scale*(sinPhi1*m_Squash); 
    			vertexData[vIndex+5] = m_Scale*(cosPhi1*sinTheta); 

    			//Normal pointers for lighting
    				
    			normalData[nIndex+0] = (float)(cosPhi0 * cosTheta); 
    			normalData[nIndex+2] = cosPhi0 * sinTheta; 
    			normalData[nIndex+1] = sinPhi0;
    			
    			//Get x-y-z for the first vertex of stack N.
    			
    			normalData[nIndex+3] = cosPhi1 * cosTheta; 
    			normalData[nIndex+5] = cosPhi1 * sinTheta; 
    			normalData[nIndex+4] = sinPhi1;	

    			if(textData != null) 
    			{		//4
    				float texX = (float)thetaIdx * (1.0f/(float)(m_Slices-1));
    				textData [tIndex + 0] = texX; 
    				textData [tIndex + 1] = (float)(phiIdx+0) * (1.0f/(float)(m_Stacks));
    				textData [tIndex + 2] = texX; 
    				textData [tIndex + 3] = (float)(phiIdx+1) * (1.0f/(float)(m_Stacks));
    			}
    			
 				colorData[cIndex+0] = (float)red;		    				
 				colorData[cIndex+1] = (float)0f;
    			colorData[cIndex+2] = (float)blue;
    			colorData[cIndex+4] = (float)red;
    			colorData[cIndex+5] = (float)0f;
    			colorData[cIndex+6] = (float)blue;
    			colorData[cIndex+3] = (float)1.0;
    			colorData[cIndex+7] = (float)1.0;

    			cIndex+=2*4;
    			vIndex+=2*3;
    			nIndex+=2*3;
    				
    			if(textData!=null)			//5
    				tIndex+= 2*2;
    			
    			blue+=colorIncrement;				
    			red-=colorIncrement;
    			
    			//Degenerate triangle to connect stacks and maintain
                  //winding order.
    			
			    vertexData[vIndex+0] = vertexData[vIndex+3] = vertexData[vIndex-3]; 
			    vertexData[vIndex+1] = vertexData[vIndex+4] = vertexData[vIndex-2]; 
			    vertexData[vIndex+2] = vertexData[vIndex+5] = vertexData[vIndex-1];
			        			
			    normalData[nIndex+0] = normalData[nIndex+3] = normalData[nIndex-3]; 
			    normalData[nIndex+1] = normalData[nIndex+4] = normalData[nIndex-2]; 
			    normalData[nIndex+2] = normalData[nIndex+5] = normalData[nIndex-1];
  		
	
		        if(textData!= null) 
		        {			//6
		        	textData [tIndex + 0] = textData [tIndex + 2] = textData [tIndex -2]; 
		        	textData [tIndex + 1] = textData [tIndex + 3] = textData [tIndex -1];
		        }    	        	
    		}			
    	}
 	
        m_NumVertices=((m_Slices*2+2) * (m_Stacks));

    	m_Pos[0]= 0.0f;
    	m_Pos[1]= 0.0f;
    	m_Pos[2]= 0.0f;
 
        m_VertexData = makeFloatBuffer(vertexData);
        m_NormalData = makeFloatBuffer(normalData);
        m_ColorData = makeFloatBuffer(colorData);
        
        if(textData!= null)
        	m_TextureData = makeFloatBuffer(textData);		
        
        createVBO(gl);
    }
    
    protected static FloatBuffer makeFloatBuffer(float[] arr) 
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*FLOAT_SIZE);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer fb = bb.asFloatBuffer();
        fb.put(arr);
        fb.position(0);
        return fb;
    }
  
    public void createVBO(GL10 gl)
    {
        int size=NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS+NUM_ST_ELS;

        createInterleavedData();
        
    	GLES11.glGenBuffers(1,m_VertexVBO,0);
        checkGlError("glGenBuffers"); 

        GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, m_VertexVBO[0]);
        checkGlError("glBindBuffer"); 

        GLES11.glBufferData(GLES11.GL_ARRAY_BUFFER,size*FLOAT_SIZE*m_NumVertices,m_InterleavedData,GLES11.GL_STATIC_DRAW);
        checkGlError("glBufferData"); 
    }
    
    private void createInterleavedData()
    {
        int i;
        int j;
        float[] interleavedArray;
        int size;
       
        size=NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS+NUM_ST_ELS;
        
        interleavedArray=new float[size*m_NumVertices];
      
        for(i=0;i<m_NumVertices;i++)
        {    
        	j=i*size;
        	
        	//vertex data

        	interleavedArray[j+0]=m_VertexData.get();
        	interleavedArray[j+1]=m_VertexData.get();
        	interleavedArray[j+2]=m_VertexData.get();
        	
        	//normal data
        	
        	interleavedArray[j+3]=m_NormalData.get();
        	interleavedArray[j+4]=m_NormalData.get();
        	interleavedArray[j+5]=m_NormalData.get();
        	
        	//color data
        	
        	interleavedArray[j+6]=m_ColorData.get();
        	interleavedArray[j+7]=m_ColorData.get();
        	interleavedArray[j+8]=m_ColorData.get();
        	interleavedArray[j+9]=m_ColorData.get();
       	
        	//texture coords
        	
        	interleavedArray[j+10]=m_TextureData.get();
        	interleavedArray[j+11]=m_TextureData.get(); 
        }
        
        m_InterleavedData=makeFloatBuffer(interleavedArray);

        m_VertexData.position(0);
        m_NormalData.position(0);
        m_ColorData.position(0);
        m_TextureData.position(0);
    }
    
    public long verticesPerUpdate() 
    {
     	return m_VerticesPerUpdate;    	
    }
    
    public void draw(GL10 gl) 
    {
           int startingOffset;
           int i;
           int maxDuplicates=10;                                                                                                                                //1
           boolean useVBO=true;                                                                                                                               //2
                                                                                                                                                                                       

           int stride=(NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS                                                       
                       +NUM_ST_ELS)*FLOAT_SIZE;

           GLES11.glEnable(GLES11.GL_CULL_FACE);
           GLES11.glCullFace(GLES11.GL_BACK);
           GLES11.glDisable ( GLES11.GL_BLEND);
           GLES11.glDisable(GLES11.GL_TEXTURE_2D);
       	
           if(useVBO)                                                                                                                                                       //4
           {         	
                 GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, m_VertexVBO[0]);                                     

                 GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);                                                        
               
                 GLES11.glVertexPointer(NUM_XYZ_ELS,GL11.GL_FLOAT,stride,0); 
   	        
                 GLES11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
                 GLES11.glNormalPointer(GL11.GL_FLOAT,stride,NUM_XYZ_ELS*FLOAT_SIZE);
   		    	
                 GLES11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
   	          
                 GLES11.glTexCoordPointer(2,GL11.GL_FLOAT,stride,
                             (NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS)*FLOAT_SIZE); 
   	    	
                 GLES11.glEnable(GL11.GL_TEXTURE_2D);	            
                 GLES11.glBindTexture(GL11.GL_TEXTURE_2D, m_TextureIDs[0]);
           } 
           else
           {
               GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER,0);                                                                        
           	
               m_InterleavedData.position(0);

               GLES11.glEnableClientState(GL10.GL_VERTEX_ARRAY);
               GLES11.glVertexPointer(NUM_XYZ_ELS,GL11.GL_FLOAT,stride,m_InterleavedData);

               m_InterleavedData.position(NUM_XYZ_ELS);
   	        
               GLES11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
               GLES11.glNormalPointer(GL11.GL_FLOAT,stride,m_InterleavedData);

               m_InterleavedData.position(NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS);

               GLES11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
               GLES11.glTexCoordPointer(2,GL11.GL_FLOAT,stride,m_InterleavedData);               

               GLES11.glEnable(GL11.GL_TEXTURE_2D);		       
               GLES11.glBindTexture(GL11.GL_TEXTURE_2D, m_TextureIDs[0]);
           }
   	            
           for(i=0;i<maxDuplicates;i++)                                                                                                                  //8
           {
           	GLES11.glTranslatef(0.0f,0.2f,0.0f);
           	GLES11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);	
           }
          
           GLES11.glDisable(GL11.GL_BLEND);
           GLES11.glDisable(GL11.GL_TEXTURE_2D);
           GLES11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
           GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER,0);
           
           m_VerticesPerUpdate=maxDuplicates*m_NumVertices;
       } 


    public void drawx(GL10 gl) 
    {
    	int i;
    	int maxDuplicates=10;
    	
    	GLES11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);

    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    	gl.glEnable(GL10.GL_CULL_FACE);					
        gl.glCullFace(GL10.GL_BACK);
         
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
    	gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	
        if(m_TextureData != null)	
    	{
    		gl.glEnable(GL10.GL_TEXTURE_2D);		//1
    		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    		gl.glBindTexture(GL10.GL_TEXTURE_2D, m_TextureIDs[0]);
    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, m_TextureData);
    	}
        
	    gl.glMatrixMode(GL10.GL_MODELVIEW); 
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_VertexData);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, m_NormalData);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);	
        
        for(i=0;i<maxDuplicates;i++)
        {
        	gl.glTranslatef(0.0f,0.2f,0.0f);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);
        }
        
	
	    gl.glDisable(GL10.GL_BLEND);
	    gl.glDisable(GL10.GL_TEXTURE_2D);
	    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
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
    
	public void setPosition(float x, float y, float z) 
	{
		m_Pos[0] = x;
		m_Pos[1] = y;
		m_Pos[2] = z;		
	}
 
    private void checkGlError(String op) 
    {
        int error;
        
        while ((error = GLES11.glGetError()) != GLES11.GL_NO_ERROR) 
        {
            Log.e("chapter 9", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}

