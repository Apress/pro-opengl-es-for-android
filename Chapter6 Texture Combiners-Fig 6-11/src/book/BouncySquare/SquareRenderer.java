/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package book.BouncySquare;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLES11Ext;

import java.lang.Math;


class SquareRenderer implements GLSurfaceView.Renderer 
{
    public SquareRenderer(boolean useTranslucentBackground,Context context) 
    {
        float squareColorsYMCA[] = 
        {
        		 1.0f, 1.0f, 0.0f, 1.0f,
        		 0.0f, 1.0f, 1.0f, 1.0f,
        		 0.0f, 0.0f, 0.0f, 1.0f,
        		 1.0f, 0.0f, 1.0f, 1.0f
        };
        			 
        float squareColorsRGBA[] = 
        {
        		 1.0f, 0.0f, 0.0f, 1.0f,
        		 0.0f, 1.0f, 0.0f, 1.0f,
        		 0.0f, 0.0f, 1.0f, 1.0f,
        		 1.0f, 1.0f, 1.0f, 1.0f
        };
        
        mTranslucentBackground = useTranslucentBackground;	
        
        mContext = context;
        mSquare1 = new Square(squareColorsYMCA);
    }

    public void onDrawFrame(GL10 gl) 
    {       
    	gl.glClearColor(0.0f,0.0f,0.0f,1.0f);
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        
		gl.glColor4f(1.0f,1.0f,1.0f,1.0f);			
        
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);
        mSquare1.draw(gl);

        mTransY += .075f;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) 
    {
         gl.glViewport(0, 0, width, height);

         float ratio = (float) width / height;
         gl.glMatrixMode(GL11.GL_PROJECTION);
         gl.glLoadIdentity();
         gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) 
    {
         gl.glDisable(GL11.GL_DITHER);

         gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,
                 GL11.GL_FASTEST);

         if (mTranslucentBackground) 
         {
             gl.glClearColor(.0f,.5f,.5f,1.0f);
         } else 
         {
             gl.glClearColor(1,1,1,1);
         }
         
         gl.glEnable(GL11.GL_CULL_FACE);
         gl.glShadeModel(GL11.GL_SMOOTH);
         
		 gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
         
         gl.glDisable(GL11.GL_DEPTH_TEST);
         gl.glEnable(GL11.GL_BLEND);
         
         mSquare1.setTextures(gl,mContext,book.BouncySquare.R.drawable.hedly,book.BouncySquare.R.drawable.splash_masked);
    }
    
    private boolean mTranslucentBackground;
    private Square mSquare1;
    private Square mSquare2;
    private Context mContext;
    private float mTransY;
    private float mAngle;
}
