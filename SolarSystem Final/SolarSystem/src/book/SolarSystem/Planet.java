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
    FloatBuffer m_textureData;

    float m_Scale;
    float m_Squash;
    float m_Radius;
    int m_Stacks, m_Slices;
	static public boolean m_UseMipmapping = false;
	SolarSystemView context;
	float[]	textPtr = null;
	float[] m_TexCoordsData;

	Planet tempPlanet;
	
    float[] m_Pos = {0.0f, 0.0f, 0.0f};
	float[] textureCoords = 
	{
		0.0f, 1.0f, 
		1.0f, 1.0f, 
		0.0f, 0.0f, 
		1.0f, 0.0f
	};


    public Planet(int stacks, int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId) 
    {
        this.m_Stacks = stacks;
        this.m_Slices = slices;
        this.m_Radius = radius;
        this.m_Squash = squash;

        init(m_Stacks,m_Slices,radius,squash, gl, context, imageId, resourceId);
    }
    
    private void init(int stacks,int slices, float radius, float squash, GL10 gl, Context context, boolean imageId, int resourceId)		//chapter 5
    {    	
    	float[] vertexData;
    	float[] normalData;
    	float[] colorData;
    	float colorIncrement=0f;
    	
    	float blue=0f;
    	float red=1.0f;
    	int numVertices=0;
    	int vIndex=0;				//vertex index
    	int cIndex=0;				//color index
    	int nIndex=0;				//normal index
    	int tIndex=0;				//texture index
    	
		if(imageId == true) {
			createTexture(gl, context, resourceId, imageId);
		}

    	m_Scale=radius;						
    	m_Squash=squash;
    	
    	colorIncrement=1.0f/(float)stacks;
    	{
    		m_Stacks = stacks;
    		m_Slices = slices;
    		    				
    		//vertices
            vertexData = new float[ 3*((m_Slices*2+2) * m_Stacks)];	
    		
    		//color data
            colorData = new float[ (4*(m_Slices*2+2) * m_Stacks)];

  		
    		//normal pointers for lighting
            normalData = new float[3*((m_Slices*2+2) * m_Stacks)];

   			if(imageId == true)
    		{
 				textPtr = m_TexCoordsData = new float [2 * ((m_Slices*2+2) * (m_Stacks))];			//texture coords
    		}
   		
    		int	phiIdx, thetaIdx;
    		
    		//latitude

    		for(phiIdx=0; phiIdx < m_Stacks; phiIdx++)			
    		{
    			//starts at -1.57 goes up to +1.57 radians
    			//the first circle

    			float phi0 = (float)Math.PI * ((float)(phiIdx+0) * (1.0f/(float)(m_Stacks)) - 0.5f);	
    			
    			//the next, or second one.

    			float phi1 = (float)Math.PI * ((float)(phiIdx+1) * (1.0f/(float)(m_Stacks)) - 0.5f);	

    			float cosPhi0 = (float)Math.cos(phi0);				
    			float sinPhi0 = (float)Math.sin(phi0);
    			float cosPhi1 = (float)Math.cos(phi1);
    			float sinPhi1 = (float)Math.sin(phi1);
    			
    			float cosTheta, sinTheta;
    			
    			//longitude
    			
    			for(thetaIdx=0; thetaIdx < m_Slices; thetaIdx++)			
    			{
    				//increment along the longitude circle each "slice"
    				
    				float theta = (float) (2.0f*(float)Math.PI * ((float)thetaIdx) * (1.0/(float)(m_Slices-1)));			
    				cosTheta = (float)Math.cos(theta);		
    				sinTheta = (float)Math.sin(theta);
    				
    				//we're generating a vertical pair of points, such 
    				//as the first point of stack 0 and the first point of stack 1
    				//above it. This is how TRIANGLE_STRIPS work, 
    				//taking a set of 4 vertices and essentially drawing two triangles
    				//at a time. The first is v0-v1-v2 and the next is v2-v1-v3. Etc.
    				
    				//get x-y-z for the first vertex of stack
    				
    				vertexData[vIndex]   = m_Scale*cosPhi0*cosTheta;
    				vertexData[vIndex+1] = m_Scale*(sinPhi0*m_Squash); 
    				vertexData[vIndex+2] = m_Scale*(cosPhi0*sinTheta); 
    				
    				vertexData[vIndex+3]   = m_Scale*cosPhi1*cosTheta;
    				vertexData[vIndex+4] = m_Scale*(sinPhi1*m_Squash); 
    				vertexData[vIndex+5] = m_Scale*(cosPhi1*sinTheta); 

    				//normal pointers for lighting
    				
    				normalData[nIndex+0] = (float)(cosPhi0 * cosTheta); 
    				normalData[nIndex+2] = cosPhi0 * sinTheta; 
    				normalData[nIndex+1] = sinPhi0;		//get x-y-z for the first vertex of stack N
    				normalData[nIndex+3] = cosPhi1 * cosTheta; 
    				normalData[nIndex+5] = cosPhi1 * sinTheta; 
    				normalData[nIndex+4] = sinPhi1;	

    				if(textPtr != null) 
    				{
    					float texX = (float)thetaIdx * (1.0f/(float)(m_Slices-1));
    					textPtr[tIndex + 0] = texX; 
    					textPtr[tIndex + 1] = (float)(phiIdx+0) * (1.0f/(float)(m_Stacks));
    					textPtr[tIndex + 2] = texX; 
    					textPtr[tIndex + 3] = (float)(phiIdx+1) * (1.0f/(float)(m_Stacks));
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
    				if (textPtr != null)
    					tIndex+=2*2;
    			}
    			
    			blue+=colorIncrement;				
    			red-=colorIncrement;
    			
    			// degenerate triangle to connect stacks and maintain winding order
    			
    			vertexData[vIndex+0] = vertexData[vIndex+3] = vertexData[vIndex-3]; 
    			vertexData[vIndex+1] = vertexData[vIndex+4] = vertexData[vIndex-2]; 
    			vertexData[vIndex+2] = vertexData[vIndex+5] = vertexData[vIndex-1];
    			
    			normalData[nIndex+0] = normalData[nIndex+3] = normalData[nIndex-3]; 
    			normalData[nIndex+1] = normalData[nIndex+4] = normalData[nIndex-2]; 
    			normalData[nIndex+2] = normalData[nIndex+5] = normalData[nIndex-1];
  		
	
    			if(textPtr != null) 
    			{
    				textPtr[tIndex + 0] = textPtr[tIndex + 2] = textPtr[tIndex -2]; 
    				textPtr[tIndex + 1] = textPtr[tIndex + 3] = textPtr[tIndex -1];
    			}
    		}
    	}

    	m_Pos[0]= 0.0f;
    	m_Pos[1]= 0.0f;
    	m_Pos[2]= 0.0f;

        m_VertexData = makeFloatBuffer(vertexData);
        m_NormalData = makeFloatBuffer(normalData);
        m_ColorData = makeFloatBuffer(colorData);
        
        if(textPtr != null)
        	m_textureData = makeFloatBuffer(textPtr);
    }

    public void draw(GL10 gl) 
    {
    	gl.glMatrixMode(GL10.GL_MODELVIEW);
    	gl.glEnable(GL10.GL_CULL_FACE);					
        gl.glCullFace(GL10.GL_BACK);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
    	gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	
        if(textPtr != null)	
    	{
    		gl.glEnable(GL10.GL_TEXTURE_2D);		//2
    		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    		
   			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
    		
    		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, m_textureData);				
    	}

    	gl.glMatrixMode(GL10.GL_MODELVIEW); 
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_VertexData);
        gl.glNormalPointer(GL10.GL_FLOAT, 0, m_NormalData);
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);		
    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, (m_Slices+1)*2*(m_Stacks-1)+2);
    	
    	gl.glDisable(GL10.GL_BLEND);
    	gl.glDisable(GL10.GL_TEXTURE_2D);
    	gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
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
	
	private int[] textures = new int[1];
	
	public int createTexture(GL10 gl, Context contextRegf, int resource, boolean imageID) 
	{
		
		Bitmap image = BitmapFactory.decodeResource(contextRegf.getResources(), resource);
		
		if (imageID == true) 
		{
			gl.glGenTextures(1, textures, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		}
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0); 

		if (m_UseMipmapping == true) 
		{
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
		} 
		else 
		{
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
		}

		image.recycle();
		return resource;

	}
}