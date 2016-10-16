
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
        
        vbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mTextureCoords1 = vbb.asFloatBuffer();
        mTextureCoords1.put(textureCoords);
        mTextureCoords1.position(0);
        
        mTextureCoordsAnimated=textureCoords;
        
    }

    public void draw(GL10 gl)
    {
    	gl.glEnable(GL10.GL_TEXTURE_2D);
	    gl.glBindTexture(GL10.GL_TEXTURE_2D,mTexture0);
	    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	    
        gl.glFrontFace(GL11.GL_CW);
        gl.glVertexPointer(2, GL11.GL_FLOAT, 0, mFVertexBuffer);
        gl.glColorPointer(4, GL11.GL_FLOAT, 0, mColorBuffer);
        
        gl.glClientActiveTexture(GL10.GL_TEXTURE0);	
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,mTextureCoords0);
	 
        gl.glClientActiveTexture(GL10.GL_TEXTURE1);	
	    gl.glTexCoordPointer(2, GL10.GL_FLOAT,0,mTextureCoords1);
	    
    	multiTexture(gl,mTexture0,mTexture1);

        gl.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_BYTE, mIndexBuffer);
        gl.glFrontFace(GL11.GL_CCW);
    }

    public void setTextures(GL10 gl,Context context,int resourceID0, int resourceID1)
    {
    	mTexture0=createTexture(gl,context,resourceID0); 
    	mTexture1=createTexture(gl,context,resourceID1); 
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
    
	public void multiTexture(GL10 gl, int tex0, int tex1)
	{
	     float combineParameter= GL10.GL_DECAL;			//1
	    
	    // Set up the First Texture.
	     gl.glActiveTexture(GL10.GL_TEXTURE0);					//2
	     gl.glBindTexture(GL10.GL_TEXTURE_2D, tex0);				//3
	    
	    // Set up the Second Texture.
	     gl.glActiveTexture(GL10.GL_TEXTURE1);					
	     gl.glBindTexture(GL10.GL_TEXTURE_2D, tex1);				
	    
	   // Set the texture environment mode for this texture to combine.
	     gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, combineParameter);	//4
	}
	
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mColorBuffer;
    private FloatBuffer mTextureCoords0;
    private FloatBuffer mTextureCoords1;
    private ByteBuffer  mIndexBuffer;
    private float mTextureCoordsAnimated[];
  
    private int mTexture0;
    private int mTexture1;
}
