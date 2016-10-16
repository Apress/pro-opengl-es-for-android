
package book.BouncySquare;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

/**
 * A vertex shaded square.
 */
class Square
{
    public Square(float colors[])
    {
        float vertices[] = 
        {
                -1.0f, -1.0f,
                 1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f
        }; 

        float textureCoords[] = 
   		 {				
   					0.0f, 1.0f, 
   					1.0f, 1.0f, 
   					0.0f, 0.0f, 
   					1.0f, 0.0f
   		 };

        
        byte indices[] = 
        {
            0, 3, 1,
            0, 2, 3
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
        
        vbb = ByteBuffer.allocateDirect(colors.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mColorBuffer = vbb.asFloatBuffer();
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
        
        vbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mTextureCoords0 = vbb.asFloatBuffer();
        mTextureCoords0.put(textureCoords);
        mTextureCoords0.position(0);        
    }

    public void draw(GL10 gl)
    {
        gl.glFrontFace(GL11.GL_CW);

    	gl.glEnable(GL10.GL_TEXTURE_2D);
	    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);	
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,mTextureCoords0);
	 
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);	
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,mTextureCoords0);
	    
 //       gl.glMatrixMode(GL10.GL_MODELVIEW); 


        
        multiTextureBumpMap(gl, mEarthTexture, mNormalTexture);
        
        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glFrontFace(GL11.GL_CCW);
    }

    public void setTextures(GL10 gl,Context context,int resourceID0, int resourceID1)
    {
    	mEarthTexture=createTexture(gl,context,resourceID1); 
    	mNormalTexture=createTexture(gl,context,resourceID0); 
    }

    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
        int[] textures = new int[1];

    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // 1

    	gl.glGenTextures(1, textures, 0); 					// 2
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]); 	// 3

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 4
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); // 5a
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // 5b	

    	tempImage.recycle();//6

    	return textures[0];
    }
    
    static float lightAngle=0.0f;
    public void multiTextureBumpMap(GL10 gl, int mainTexture, int normalTexture) 
    {
        float x,y,z;
    	    
        
        lightAngle+=.3f;																//1
    	    
        if(lightAngle>180)
            lightAngle=0;
    	    
        // Set up the light vector.
        x = (float) Math.sin(lightAngle * (3.14159 / 180.0f));   						//2
        y = 0.0f;
        z = (float) Math.cos(lightAngle * (3.14159 / 180.0f));
    	    
        // Half shifting to have a value between 0.0f and 1.0f.
        x = x * 0.5f + 0.5f;						 									//3
        y = y * 0.5f + 0.5f;
        z = z * 0.5f + 0.5f;
    	    
        gl.glColor4f(x, y, z, 1.0f);  				       								//4
    	    
        //The color and normal map are combined.
        gl.glActiveTexture(GL10.GL_TEXTURE0);			       							//5
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mainTexture);
        
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL11.GL_COMBINE);	//6
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL11.GL_COMBINE_RGB, GL11.GL_DOT3_RGB);    	//7
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL11.GL_SRC0_RGB, GL11.GL_TEXTURE);			//8
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL11.GL_SRC1_RGB, GL11.GL_PREVIOUS);			//9
    	    
        // Set up the Second Texture, and combine it with the result of the Dot3 combination.
    	        
        gl.glActiveTexture(GL10.GL_TEXTURE1);											//10
        gl.glBindTexture(GL10.GL_TEXTURE_2D, normalTexture);
    	    
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);	//11
    	    
    }
	
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mTextureCoords0;
    private FloatBuffer mTextureCoords1;
    private ByteBuffer  mIndexBuffer;
    private float mTextureCoordsAnimated[];
  
    private int mEarthTexture;
    private int mNormalTexture;
}
