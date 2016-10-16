package book.SolarSystem;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class SolarSystemRenderer implements GLSurfaceView.Renderer 
{
	public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;
	public final static int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
	public final static int SS_FILLLIGHT2 = GL10.GL_LIGHT2;
	public final static int X_VALUE = 0;
	public final static int Y_VALUE = 1;
	public final static int Z_VALUE = 2;
	Planet m_Earth;
	Planet m_Sun;
	float[] m_Eyeposition = { 0.0f, 0.0f, 0.0f };
	public Context myAppcontext;

	static float angle = 0.0f;

	private void execute(GL10 gl) {
		   float posFill1[]={-8.0f, 0.0f, 7.0f, 1.0f};			
		   float cyan[]={0.0f, 1.0f, 1.0f, 1.0f};
		   float orbitalIncrement=0.5f;
		   float sunPos[]={0.0f, 0.0f, 0.0f, 1.0f};
					
		 gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
				    
	     gl.glEnable(GL10.GL_DEPTH_TEST);	
		 gl.glClearColor(0.0f, 0.25f, 0.35f, 1.0f);
		 gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		 gl.glPushMatrix();
				    
			 gl.glTranslatef(-m_Eyeposition[X_VALUE],-m_Eyeposition[Y_VALUE],-m_Eyeposition[Z_VALUE]);
			 gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
				    
			 gl.glEnable(SS_FILLLIGHT1);
			 gl.glEnable(SS_FILLLIGHT2);
					
			 gl.glPushMatrix();
					
			 angle+=orbitalIncrement;
			 gl.glRotatef(angle, 0.0f, 1.0f, 0.0f);	 
			 executePlanet(m_Earth, gl);
			 gl.glPopMatrix();
			 gl.glPopMatrix();
		}


	private void executePlanet(Planet m_Planet, GL10 gl) {

		float posX, posY, posZ;
		posX = m_Planet.m_Pos[0]; // 17
		posY = m_Planet.m_Pos[1];
		posZ = m_Planet.m_Pos[2];

		gl.glPushMatrix();
		gl.glTranslatef(posX, posY, posZ); // 18
		m_Planet.draw(gl); // 19
		gl.glPopMatrix();
	}

	public SolarSystemRenderer(Context context) {
		this.myAppcontext = context;
		// mPlanet=new Planet(50, 50,1.0f, 1.0f);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.0f, 0.25f, 0.35f, 1.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glTranslatef(0.0f, (float) Math.sin(mTransY), -4.0f);

		gl.glRotatef(mAngle, 1, 0, 0);
		gl.glRotatef(mAngle, 0, 1, 0);

		execute(gl);
		// mPlanet.draw(gl);

		// mTransY+=.015f;
		// mAngle+=.4;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		/*
		 * Set our projection matrix. This doesn't have to be done each time we
		 * draw, but usually a new projection needs to be set when the viewport
		 * is resized.
		 */

		float aspectRatio;
		float zNear = .1f;
		float zFar = 1000f;
		float fieldOfView = 40.0f / 65.3f;
		float size;

		gl.glEnable(GL10.GL_NORMALIZE);

		aspectRatio = (float) width / (float) height; // h/w clamps the fov to
														// the height, flipping
														// it would make it
														// relative to the width

		// Set the OpenGL projection matrix

		gl.glMatrixMode(GL10.GL_PROJECTION);

		size = zNear * (float) (Math.tan((double) (fieldOfView / 3.0f)));
		gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio,
				zNear, zFar);

		// Make the OpenGL modelview matrix the default

		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}

	private void initGeometry(GL10 gl) 
	{
		m_Eyeposition[X_VALUE] = 0.0f; // 1
		m_Eyeposition[Y_VALUE] = 0.0f;
		m_Eyeposition[Z_VALUE] = 5.0f;

		int resid = book.SolarSystem.R.drawable.earth_light;
		int normresid = book.SolarSystem.R.drawable.earth_normal_hc;

		m_Earth = new Planet(50, 50, 1.0f, 1.0f, gl, myAppcontext, true, resid,normresid); // 2
		m_Earth.setPosition(0.0f, 0.0f, 0.0f); // 3
	}

	private void initLighting(GL10 gl) {
		float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] posFill1 = { -15.0f, 15.0f, 0.0f, 1.0f };
		float[] posFill2 = { -10.0f, -4.0f, 1.0f, 1.0f };

		float[] white = { 1.0f, 1.0f, 1.0f, 1.0f };
		float[] dimblue = { 0.0f, 0.0f, .2f, 1.0f };

		float[] cyan = { 0.0f, 1.0f, 1.0f, 1.0f };
		float[] yellow = { 1.0f, 1.0f, 0.0f, 1.0f };
		float[] magenta = { 1.0f, 0.0f, 1.0f, 1.0f };
		float[] dimmagenta = { .75f, 0.0f, .25f, 1.0f };

		float[] dimcyan = { 0.0f, .5f, .5f, 1.0f };

		// lights go here

		gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
		gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));

		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION, makeFloatBuffer(posFill1));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));
		gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR, makeFloatBuffer(dimcyan));

		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION, makeFloatBuffer(posFill2));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR, makeFloatBuffer(dimmagenta));
		gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE, makeFloatBuffer(dimblue));

		// materials go here

		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
				makeFloatBuffer(cyan));
//		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, makeFloatBuffer(white));

//		gl.glLightf(SS_SUNLIGHT, GL10.GL_QUADRATIC_ATTENUATION, .001f);

		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);

		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(SS_SUNLIGHT);
		gl.glEnable(SS_FILLLIGHT1);
		gl.glEnable(SS_FILLLIGHT2);
		gl.glLoadIdentity();
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 * By default, OpenGL enables features that improve quality but reduce
		 * performance. One might want to tweak that especially on software
		 * renderer.
		 */
		initGeometry(gl);
		initLighting(gl);

		gl.glDisable(GL10.GL_DITHER);

		/*
		 * Some one-time OpenGL initialization can be made here probably based
		 * on features of this particular context
		 */
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_BACK);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

	}

	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	private Planet mPlanet;
	private float mTransY;
	private float mAngle;
}
