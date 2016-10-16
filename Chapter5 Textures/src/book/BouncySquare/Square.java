
package book.BouncySquare;

import android.content.Context;
import android.graphics.*;
import android.opengl.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * A vertex shaded square.
 */
class Square
{
    public Square()
    {
        float vertices[] = 
        {
                -1.0f, -1.0f,
                 1.0f, -1.0f,
                -1.0f,  1.0f,
                 1.0f,  1.0f
        }; 

        byte maxColor=(byte)255;
        
        byte colors[] = 
        {
        		maxColor,maxColor,       0,maxColor,
                0,       maxColor,maxColor,maxColor,
                0,              0,       0,maxColor,
                maxColor,       0,maxColor,maxColor
        }; 
        
        byte indices[] = 
        {
            0, 3, 1,
            0, 2, 3
        };

        float[] textureCoords = 	
        {		        		
        	0.0f, 0.0f,
        	1.0f, 0.0f,
        	0.0f, 1.0f,
        	1.0f, 1.0f		
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();
        mFVertexBuffer.put(vertices);
        mFVertexBuffer.position(0);
        
        mColorBuffer = ByteBuffer.allocateDirect(colors.length);
        mColorBuffer.put(colors);
        mColorBuffer.position(0);

        mIndexBuffer = ByteBuffer.allocateDirect(indices.length);
        mIndexBuffer.put(indices);
        mIndexBuffer.position(0);
        
        ByteBuffer tbb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = tbb.asFloatBuffer();
        mTextureBuffer.put(textureCoords);
        mTextureBuffer.position(0); 
    }

    public void draw(GL10 gl)
    { 
    	gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mFVertexBuffer);
    	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
    	gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, mColorBuffer);
    	gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    	    
    	gl.glEnable(GL10.GL_TEXTURE_2D); 				//1
    	gl.glEnable(GL10.GL_BLEND);					//2	
    	     
    	gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);			//3
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);		//4 
    	    
    	gl.glTexCoordPointer(2, GL10.GL_FLOAT,0, mTextureBuffer);	//5
    	gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);		//6	

    	gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);			//7
    	    
    	gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);		//8
    }

    private int[] textures = new int[1];
    
    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); // 1

    	gl.glGenTextures(1, textures, 0); // 2
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]); // 3

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 4
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); // 5a
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // 5b	

    	tempImage.recycle();//6

    	return resource;
    }

    private Float mTransY;
    public FloatBuffer  mTextureBuffer;
    private FloatBuffer mFVertexBuffer;
    private ByteBuffer  mColorBuffer;
    private ByteBuffer  mIndexBuffer;
}
