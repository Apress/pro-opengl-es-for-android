package book.SolarSystem;

import java.util.*;
import java.nio.*;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.opengl.GLES20;
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
    FloatBuffer m_InterleavedData;

    int m_Texture0;
    int m_Texture1;
    int size;
    
	public final static int NUM_XYZ_ELS = 3;
	public final static int NUM_NXYZ_ELS = 3;
	public final static int NUM_RGBA_ELS = 4;
	public final static int NUM_ST_ELS = 2;
	public final static int FLOAT_SIZE = 4;

	public final static int PLANET_BLEND_MODE_NONE = 1;
	public final static int PLANET_BLEND_MODE_ATMO = 2;
	public final static int PLANET_BLEND_MODE_SOLID = 3;
	public final static int PLANET_BLEND_MODE_FADE = 4;
    
    int m_NumVertices;
    float m_Scale;
    float m_Squash;
    float m_Radius;
    int m_Stride;
    int m_Stacks, m_Slices;
	static public boolean m_UseMipmapping = false;
	SolarSystemRendererES2 context;
	float[]	textPtr = null;
	float[] m_TexCoordsData;
	static int m_BumpmapID;
	Planet tempPlanet;
    float[] m_Pos = {0.0f, 0.0f, 0.0f};

   public Planet(int stacks, int slices, float radius, float squash, GL10 gl, Context context,boolean createTextureCoords, int textureID) 
    {
        this.m_Stacks = stacks;
        this.m_Slices = slices;
        this.m_Radius = radius;
        this.m_Squash = squash;

        init(m_Stacks,m_Slices,radius,squash, gl, context,createTextureCoords,textureID);
    }
	    
    private void init(int stacks,int slices, float radius, float squash, GL10 gl, Context context,boolean createTextureCoords, int textureID)		// 1
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
        	
        if(textureID>=0)
        	m_Texture0=createTexture(gl, context, textureID);	
        else
        	m_Texture0=-1;
        
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

        //I may want too create texture coords even though I don't supply a texture here at 
        //initialization, cuz I want to supply it at run time
        
		if(createTextureCoords == true)		
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
    				
    			if(textData!=null)			
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
              
        size=NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS;

        if(textData!= null)
        {   
        	size+=NUM_ST_ELS;
        	m_TextureData = makeFloatBuffer(textData);
        }
        
        m_Stride=size*FLOAT_SIZE;
        
        createInterleavedData();
    }
    
    private void createInterleavedData()
    {
        int i;
        int j;
        float[] interleavedArray;
        int size;
        int offset=0;
        
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
    }
    
    public void draw(GL10 gl,int vertexLocation,int normalLocation,int colorLocation, int textureLocation,int textureID) 
    {
    	//overrides any default texture that may have been supplied at creation time
    	
    	if(textureID>=0)
    	{
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
    	}
    	else if(m_Texture0>=0)
    	{
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, m_Texture0);
    	}
    	
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        
        m_InterleavedData.position(0);
        
        GLES20.glVertexAttribPointer(vertexLocation, 3, GLES20.GL_FLOAT, false,m_Stride, m_InterleavedData);
        GLES20.glEnableVertexAttribArray(vertexLocation);

        m_InterleavedData.position(NUM_XYZ_ELS);
    
        if(normalLocation>=0)
        {
        	GLES20.glVertexAttribPointer(normalLocation, 3, GLES20.GL_FLOAT, false,m_Stride, m_InterleavedData);
            GLES20.glEnableVertexAttribArray(normalLocation);
        }
        
        m_InterleavedData.position(NUM_XYZ_ELS+NUM_NXYZ_ELS);
        
        //use the color data if a location is supplied, otherwise skip it, but we still
        //need to increment the buffer's position as it will always have the color data
        //whether we use it or not.
        
        if(colorLocation>=0)
        {
        	GLES20.glVertexAttribPointer(colorLocation, 4, GLES20.GL_FLOAT, false,m_Stride, m_InterleavedData);
            GLES20.glEnableVertexAttribArray(colorLocation);
        }
       
        m_InterleavedData.position(NUM_XYZ_ELS+NUM_NXYZ_ELS+NUM_RGBA_ELS);

    	GLES20.glVertexAttribPointer(textureLocation, 2, GLES20.GL_FLOAT, false,m_Stride, m_InterleavedData);
        GLES20.glEnableVertexAttribArray(textureLocation);

    	GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);	
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

	public void setPosition(float x, float y, float z) 
	{
		m_Pos[0] = x;
		m_Pos[1] = y;
		m_Pos[2] = z;		
	}
	
    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
    	int[] textures = new int[1];
    	
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // 1

    	gl.glGenTextures(1, textures, 0); 
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]); 

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); 
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); 
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 	

    	tempImage.recycle();//6

    	gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
    	
    	return textures[0];
    }
    
    public void setBlendMode(int blendMode)
    {
    	GLES20.glEnable(GLES20.GL_BLEND);

        if(blendMode==PLANET_BLEND_MODE_NONE)
        	GLES20.glDisable(GLES20.GL_BLEND);    
        else if(blendMode==PLANET_BLEND_MODE_FADE)
        	GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        else if(blendMode==PLANET_BLEND_MODE_ATMO)
        	GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        else if(blendMode==PLANET_BLEND_MODE_SOLID)
        	GLES20.glDisable(GLES20.GL_BLEND);    
    }
}