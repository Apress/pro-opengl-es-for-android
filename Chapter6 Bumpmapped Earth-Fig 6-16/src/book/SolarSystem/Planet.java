package book.SolarSystem;

import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.util.Log;

public class Planet extends Application
{
    FloatBuffer m_VertexData;
    FloatBuffer m_NormalData;
    FloatBuffer m_ColorData;
    FloatBuffer m_TextureData;

    int m_Texture0;
    int m_Texture1;
    
    float m_Scale;
    float m_Squash;
    float m_Radius;
    int m_Stacks, m_Slices;
	static public boolean m_UseMipmapping = false;
	SolarSystemRenderer context;
	float[]	textPtr = null;
	float[] m_TexCoordsData;
	static int m_BumpmapID;
	Planet tempPlanet;
	
    float[] m_Pos = {0.0f, 0.0f, 0.0f};
	float[] textureCoords = 
	{
		0.0f, 1.0f, 
		1.0f, 1.0f, 
		0.0f, 0.0f, 
		1.0f, 0.0f
	};

    public Planet(int stacks, int slices, float radius, float squash, GL10 gl, Context context,
    			boolean imageId, int earthID, int bumpmapID) 
    {
        this.m_Stacks = stacks;
        this.m_Slices = slices;
        this.m_Radius = radius;
        this.m_Squash = squash;

        init(m_Stacks,m_Slices,radius,squash, gl, context, imageId, earthID,bumpmapID);
    }
    
    private void init(int stacks,int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int textureID, int normalMapID)		// 1
    {    	
    	float[] vertexData;
        float[] normalData;
        float[] colorData;
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
    		m_Texture0=createTexture(gl, context, normalMapID);	
    		m_Texture1=createTexture(gl, context, textureID);	//2
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
 	
    	m_Pos[0]= 0.0f;
    	m_Pos[1]= 0.0f;
    	m_Pos[2]= 0.0f;
 
        m_VertexData = makeFloatBuffer(vertexData);
        m_NormalData = makeFloatBuffer(normalData);
        m_ColorData = makeFloatBuffer(colorData);
        
        if(textData!= null)
        	m_TextureData = makeFloatBuffer(textData);		
    }
    
    public void draw(GL10 gl) 
    {
        gl.glMatrixMode(GL10.GL_MODELVIEW);				
        gl.glEnable(GL10.GL_CULL_FACE);					
        gl.glCullFace(GL10.GL_BACK);					
        gl.glEnable(GL10.GL_LIGHTING);
gl.glEnable(GL10.GL_BLEND);
//gl.glDisable(GL10.GL_LIGHTING);
       gl.glFrontFace(GL10.GL_CCW);						

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);			
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_VertexData);			

        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);                                 

        gl.glBindTexture(GL10.GL_TEXTURE_2D, m_Texture0);
 		
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, m_TextureData);	
                 
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,m_TextureData);
        
        gl.glMatrixMode(GL10.GL_MODELVIEW); 
          
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, m_NormalData);	
          
        gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, m_ColorData);	

        multiTextureBumpMap(gl, m_Texture0, m_Texture1);
        
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);	

    }
    
    float lightAngle=30.0f;

	public void multiTextureBumpMap(GL10 gl, int mainTexture, int normalTexture) 
	{
	    float x,y,z;
	    
	    lightAngle+=0.5f;							//1
	    
	    if(lightAngle>180)
	        lightAngle=0;
	    
	    // Set up the light vector.
	    
	    x = (float) Math.sin(lightAngle * (3.14159 / 180.0f));                 			//2
	    y = 0.0f;
	    z = (float) Math.cos(lightAngle * (3.14159 / 180.0f));
	    
	    // Half shifting to have a value between 0.0f and 1.0f.
	    
	    x = x * 0.5f + 0.5f;						//3
	    y = y * 0.5f + 0.5f;
	    z = z * 0.5f + 0.5f;
	    

//	    gl.glColor4f(x, y, z, 1.0f);  						//4
	    
	    //The color and normal map are combined.

//gl.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);  						//4

	    gl.glActiveTexture(GL11.GL_TEXTURE0);					//5
	    gl.glBindTexture(GL11.GL_TEXTURE_2D, mainTexture);
	    
	    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_COMBINE);	//6
	    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_DOT3_RGB);	//7
	    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB, GL11.GL_TEXTURE);	//8
	    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB, GL11.GL_PREVIOUS);	//9
	    
	    // Set up the Second Texture, and combine it with the result of the Dot3 combination.
	        
	    gl.glActiveTexture(GL11.GL_TEXTURE1);					//10
	    gl.glBindTexture(GL11.GL_TEXTURE_2D, normalTexture);
	    
	    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);	//11
	    
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

	public void setPosition(float x, float y, float z) {
		m_Pos[0] = x;
		m_Pos[1] = y;
		m_Pos[2] = z;		
	}
	
	
    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
    	int[] textures = new int[1];
    	
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // 1

    	gl.glGenTextures(1, textures, 0); // 2
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]); // 3

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 4
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); // 5a
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // 5b	

    	tempImage.recycle();//6

    	return textures[0];
    }
}