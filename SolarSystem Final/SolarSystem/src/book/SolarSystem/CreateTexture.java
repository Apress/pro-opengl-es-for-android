package book.SolarSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import book.SolarSystem.*;
import book.SolarSystem.SolarSystemView.SolarSystem.CGSize;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLUtils;
import android.view.Display;

public class CreateTexture 
{
	int[] textures = new int[1];
	public boolean m_UseMipmapping = true;

	public float renderTextureAt(GL10 gl, float postionX, float postionY, float depth, CGSize windowsSize, 
						int textureId, float size, float r, float g, float b, float a) 
	{
		float scaledX, scaledY;
		float zoomBias = .1f;
		float scaledSize;

		float squareVertices[] = 
		{ 
			-1.0f, -1.0f, depth, 
			1.0f, -1.0f,  depth,
			-1.0f, 1.0f, depth, 
			1.0f, 1.0f, depth
		};

		float textureCoords[] = { 
				0.0f, 0.0f, 
				1.0f, 0.0f, 
				0.0f, 1.0f, 
				1.0f, 1.0f
		};
	
		float aspectRatio = windowsSize.height / windowsSize.width;

		scaledX = (float) (2.0f * postionX / windowsSize.width); 
		scaledY = (float) (2.0f * postionY / windowsSize.height)*aspectRatio;
		
		gl.glDisable(GL10.GL_DEPTH_TEST); 

		gl.glDisable(GL10.GL_LIGHTING);

	    gl.glDisable(GL10.GL_CULL_FACE);
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glMatrixMode(GL10.GL_PROJECTION); 
		gl.glPushMatrix();
		gl.glLoadIdentity();
		
		gl.glOrthof(-1.0f, 1.0f, -1.0f * aspectRatio, 1.0f * aspectRatio, -1.0f,1000); 

		gl.glMatrixMode(GL10.GL_MODELVIEW); 
	    gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glTranslatef(scaledX, scaledY, 0); 

		scaledSize = zoomBias * size; 

		gl.glScalef(scaledSize, scaledSize, 1); 

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(squareVertices));
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glEnable(GL10.GL_TEXTURE_2D); 
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(textureCoords));
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glColor4f(r, g, b, a); 
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glMatrixMode(GL10.GL_PROJECTION); 
		gl.glPopMatrix();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_LIGHTING);

		gl.glDisable(GL10.GL_BLEND);
		
		return scaledSize;
	}

	public int createTexture(GL10 gl, Context contextRegf, boolean imageID,int resource) 
	{
		Bitmap tempImage = BitmapFactory.decodeResource(
				contextRegf.getResources(), resource); 

		if (imageID == true) 
		{
			gl.glGenTextures(1, textures, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		}

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); 

		if (m_UseMipmapping == false) 
		{
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
		} 
		else 
		{
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
		}

		tempImage.recycle();
		return textures[0];
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
}
