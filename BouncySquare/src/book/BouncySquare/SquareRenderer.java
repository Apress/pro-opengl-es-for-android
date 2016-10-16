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

import android.opengl.GLSurfaceView;
import java.lang.Math;


class SquareRenderer implements GLSurfaceView.Renderer 
{
    public SquareRenderer(boolean useTranslucentBackground) 
    {
        mTranslucentBackground = useTranslucentBackground;	
        mSquare = new Square();
    }

    public void onDrawFrame(GL10 gl) 
    {
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

    	gl.glMatrixMode(GL10.GL_MODELVIEW);       
    	gl.glLoadIdentity();                                          
    	gl.glTranslatef(0.0f,(float)Math.sin(mTransY), -3.0f);   
	   
	   	gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);             
	   	gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

	   	mSquare.draw(gl);                            

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

         gl.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT,GL11.GL_FASTEST);

         if (mTranslucentBackground) 
         {
             gl.glClearColor(0,0,0,0);
         } 
         else 
         {
             gl.glClearColor(1,1,1,1);
         }
         
         gl.glEnable(GL11.GL_CULL_FACE);
         gl.glShadeModel(GL11.GL_SMOOTH);
         gl.glEnable(GL11.GL_DEPTH_TEST);
    }
    private boolean mTranslucentBackground;
    private Square mSquare;
    private float mTransY;
    private float mAngle;
}
