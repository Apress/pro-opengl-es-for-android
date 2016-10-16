package book.BouncyCube1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

/**
 * A vertex shaded cube.
 */
class Cube
{
    float[] mVertices = 
    {
            -1.0f,  1.0f, 1.0f,
             1.0f,  1.0f, 1.0f,
             1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f,
             
            -1.0f,  1.0f,-1.0f,
             1.0f,  1.0f,-1.0f,
             1.0f, -1.0f,-1.0f,
            -1.0f, -1.0f,-1.0f
    }; 

    float maxColor=1.0f;
    
    float[] mColors = 
    {
    		maxColor,   maxColor,      0.0f,maxColor,
            0.0f,       maxColor,  maxColor,maxColor,
            0.0f,           0.0f,      0.0f,maxColor,
            maxColor,       0.0f,  maxColor,maxColor,
            
    		maxColor,		0.0f,      0.0f,maxColor,
            0.0f,       maxColor,	   0.0f,maxColor,
            0.0f,           0.0f,  maxColor,maxColor,
            0.0f,       	0.0f,      0.0f,maxColor
    }; 

    float[] mNormals = 
    {
		0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,

        1.0f, 0.0f, 0.0f,
		1.0f, 0.0f, 0.0f,

        
        0.0f, 1.0f, 0.0f,
		0.0f, 1.0f, 0.0f,

		0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,	
        
        0.0f, -1.0f, 0.0f,
		0.0f, -1.0f, 0.0f,
        
        -1.0f, 0.0f, 0.0f,
		-1.0f, 0.0f, 0.0f,
       
	};
    
    byte[] tfan1 =					
    {
    		1,0,3,
    		1,3,2,
    		1,2,6,
    		1,6,5,
    		1,5,4,
    		1,4,0       		
    };

    byte[] tfan2 =					
    {
    		7,4,5,
    		7,5,6,
    		7,6,2,
    		7,2,3,
    		7,3,0,
    		7,0,4
    };
    
    public Cube()
    {      
        mTfan1 = ByteBuffer.allocateDirect(tfan1.length);
        mTfan1.put(tfan1);
        mTfan1.position(0);
        
        mTfan2 = ByteBuffer.allocateDirect(tfan2.length);
        mTfan2.put(tfan2);
        mTfan2.position(0);
    }

    public void draw(GL10 gl)
    {
        gl.glVertexPointer(3, GL11.GL_FLOAT, 0,makeFloatBuffer(mVertices));
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glColorPointer(4, GL11.GL_FLOAT, 0,makeFloatBuffer(mColors));
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glNormalPointer(GL11.GL_FLOAT, 0,makeFloatBuffer(mNormals));
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

    	gl.glDrawElements( gl.GL_TRIANGLE_FAN, 6 * 3, gl.GL_UNSIGNED_BYTE, mTfan1);
    	gl.glDrawElements( gl.GL_TRIANGLE_FAN, 6 * 3, gl.GL_UNSIGNED_BYTE, mTfan2);
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
	 
    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mNormalBuffer;
    private ByteBuffer  mColorBuffer;
    private ByteBuffer  mIndexBuffer;
    private ByteBuffer  mTfan1;
    private ByteBuffer  mTfan2;

}

