package book.lensflare;

import javax.microedition.khronos.opengles.GL10;

import book.lensflare.*;
import book.lensflare.LensFlareRenderer.CGPoint;
import book.lensflare.LensFlareRenderer.CGSize;
import android.app.Application;
import android.content.Context;
import android.util.Log;

public class Flare {

    float m_Size;
    float m_Red;
    float m_Green;
    float m_Blue;
    float m_Alpha;
    float m_VectorPosition;
    int  m_textureId;
	public String TAG = "MyACtivity";
    
	public int init(GL10 gl, Context context, int resid, float msize, float vectorPosition, float r, float g, float b, float a) 
	{
			CreateTexture ct = new CreateTexture();
			m_textureId = ct.createTexture(gl, context, true, resid);
	        
	        m_Size = msize;
	        m_VectorPosition = vectorPosition;
	        m_Red = r;
	        m_Green = g;
	        m_Blue = b;
	        m_Alpha = a;
	        
	        m_Red *= m_Alpha;
	        m_Green *= m_Alpha;
	        m_Blue *= m_Alpha;
	        return m_textureId;
	}

	public float getVectorPosition()
	{
	    return m_VectorPosition;
	}

	public void renderFlareAt(GL10 gl, int textureID, float x, float y, CGSize size, Context context)
	{
	    CreateTexture ct = new CreateTexture();
	    ct.renderTextureAt(gl, x, y, size, textureID, m_Size, m_Red, m_Green, m_Blue, m_Alpha);
	    //:position name:m_Name size:m_Size  r:m_Red g:m_Green b:m_Blue a:m_Alpha];
	}


}
