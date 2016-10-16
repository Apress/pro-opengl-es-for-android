package book.lensflare;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import book.lensflare.*;
import book.lensflare.LensFlareRenderer.CGSize;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import android.view.Display;
import book.lensflare.LensFlareRenderer.CGPoint;

public class CreateTexture {

	int[] textures = new int[1];
	public boolean m_UseMipmapping = true;

	public void renderTextureAt(GL10 gl, float postionX, float postionY, CGSize windowsSize, 
						int textureId, float size, float r, float g, float b, float a) { //1

		float scaledX, scaledY;
		float zoomBias = .1f;
		// Just an extra scaling, and this will be affected at some point when
		// zoom is in place.
		float scaledSize;

		float squareVertices[] = { 
				-1.0f, -1.0f, 0.0f, 
				1.0f, -1.0f, 0.0f,
				-1.0f, 1.0f, 0.0f, 
				1.0f, 1.0f, 0.0f };

		float textureCoords[] = { 
				0.0f, 0.0f, 
				1.0f, 0.0f, 
				0.0f, 1.0f, 
				1.0f, 1.0f
		};

		
	
		float aspectRatio = windowsSize.height / windowsSize.width;

		scaledX = (float) (2.0f * postionX / windowsSize.width); // 2
		scaledY = (float) (2.0f * postionY / windowsSize.height);

		gl.glDisable(GL10.GL_DEPTH_TEST); // 3
		gl.glDisable(GL10.GL_LIGHTING);

		gl.glMatrixMode(GL10.GL_PROJECTION); // 4
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glOrthof(-1.0f, 1.0f, -1.0f * aspectRatio, 1.0f * aspectRatio, -1.0f, 1.0f); // 5

		gl.glMatrixMode(GL10.GL_MODELVIEW); // 6
		gl.glLoadIdentity();

		gl.glTranslatef(scaledX, scaledY, 0); // 7

		scaledSize = zoomBias * size; // 8

		gl.glScalef(scaledSize, scaledSize, 1); // 9

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, makeFloatBuffer(squareVertices));
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glEnable(GL10.GL_TEXTURE_2D); // 10
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, makeFloatBuffer(textureCoords));
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		gl.glColor4f(r, g, b, a); // 11
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glMatrixMode(GL10.GL_PROJECTION); // 12
		gl.glPopMatrix();

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glPopMatrix();

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_LIGHTING);

	}

	public int createTexture(GL10 gl, Context contextRegf, boolean imageID,
			int resource) {

		Bitmap tempImage = BitmapFactory.decodeResource(
				contextRegf.getResources(), resource); // 1

		if (imageID == true) {
			gl.glGenTextures(1, textures, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		}

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); // 4

		if (m_UseMipmapping == false) {
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR_MIPMAP_NEAREST);
		} else {
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
					GL10.GL_LINEAR);
		}

		tempImage.recycle();// 6
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
