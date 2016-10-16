package book.SolarSystem;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.R;
import android.R.xml;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

class SolarSystemView extends GLSurfaceView 
{
	SolarSystem mRenderer;

	public final static String TAG = "Lens Flare";
	static final float DEGREES_PER_RADIAN = 57.29f;
	static int NONE = 0;
	static int DRAG = 1;
	static int ZOOM = 2;
	static int X_INDEX=0;
	static int Y_INDEX=1;
	static int Z_INDEX=2;
	static int RADIUS_INDEX=3;
	public float m_PosX;
	public float m_PosY;
	static float m_Zoom , m_LastZoom  = 0.0f;
	int m_Gesture = NONE;
	float m_ZoomBias = .01f;
	static float m_OldDist = 0;
	static int m_PinchFlag = 0, m_DragFlag = 0;
	static float m_StartFOV = 0.0f;
	static float m_CurrentFOV = 0.0f;
	static float m_FieldOfView = 30.0f;
	static Planet m_Earth;
	static Planet m_Sun;
	int m_FlareSource=0;
	LensFlare m_LensFlare = new LensFlare();
	CreateTexture CT = new CreateTexture();
	static float m_OldSunSx=0, m_OldSunSy=0;
	static float m_WinX, m_WinY, m_WinZ;
	public final static float STANDARD_FOV = 30.0f;
	float m_ScreenRadius;
	Constellations m_constellation;
	static boolean outlineOn = true;
	static boolean lineOn = true;
	static boolean lens_flare = true;
	
	public SolarSystemView(Context context) 
	{
		super(context);
		
		mRenderer = new SolarSystem(context);	
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		
		getHolder().setFormat(PixelFormat.RGBA_8888);
		
		setRenderer(mRenderer);
	}

	public boolean onTouchEvent(MotionEvent ev) 
	{
		boolean retval = true;
		
		switch (ev.getAction() & MotionEvent.ACTION_MASK) 
		{
			case MotionEvent.ACTION_DOWN:
				m_Gesture = DRAG;
				break;
				
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				m_Gesture = NONE;
				m_LastTouchPoint.x = ev.getX();
				m_LastTouchPoint.y = ev.getY();
				break;
				
			case MotionEvent.ACTION_POINTER_DOWN:
				m_OldDist = spacing(ev);
	
				midPoint(m_MidPoint, ev);
				m_Gesture = ZOOM;
				m_StartFOV = mRenderer.getFieldOfView();

				m_LastTouchPoint.x = m_MidPoint.x;
				m_LastTouchPoint.y = m_MidPoint.y;
				m_CurrentTouchPoint.x=m_MidPoint.x;
				m_CurrentTouchPoint.y=m_MidPoint.y;
				
				break;
				
			case MotionEvent.ACTION_MOVE:
				if (m_Gesture == DRAG) 
				{	
					retval = handleDragGesture(ev);
				} 
				else if (m_Gesture == ZOOM) 
				{
					retval = handlePinchGesture(ev);
				}
				break;
		}

		return retval;
	}

	final PointF m_CurrentTouchPoint = new PointF();
	PointF m_MidPoint = new PointF();
	PointF m_LastTouchPoint = new PointF();
	static int m_GestureMode  = 0;
	static int DRAG_GESTURE = 1;
	static int PINCH_GESTURE = 2;

	public boolean handleDragGesture(MotionEvent ev) 
	{
		m_LastTouchPoint.x = m_CurrentTouchPoint.x;
		m_LastTouchPoint.y = m_CurrentTouchPoint.y;

		m_CurrentTouchPoint.x = ev.getX();
		m_CurrentTouchPoint.y = ev.getY();

		m_GestureMode  = DRAG_GESTURE;
		m_DragFlag = 1;

		return true;
	}

	public boolean handlePinchGesture(MotionEvent ev) 
	{
		float minFOV = 5.0f;
		float maxFOV = 100.0f;
		float newDist = spacing(ev);
		
		m_Zoom  = m_OldDist/newDist;

		if (m_Zoom  > m_LastZoom ) 
		{
			m_LastZoom  = m_Zoom ;
		} 
		else if (m_Zoom  <= m_LastZoom ) 
		{
			m_LastZoom  = m_Zoom ;
		}
		
		m_CurrentFOV = m_StartFOV * m_Zoom ;
		m_LastTouchPoint = m_MidPoint;
		m_GestureMode  = PINCH_GESTURE;
		
		if (m_CurrentFOV >= minFOV && m_CurrentFOV <= maxFOV) 
		{
			mRenderer.setFieldOfView(m_CurrentFOV);
			return true;
		} 
		else
			return false;
	}

	private float spacing(MotionEvent event) 
	{
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) 
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	public class SolarSystem implements GLSurfaceView.Renderer 
	{
		public final static int SS_SUNLIGHT = GL10.GL_LIGHT0;
		public final static int SS_FILLLIGHT1 = GL10.GL_LIGHT1;
		public final static int SS_FILLLIGHT2 = GL10.GL_LIGHT2;
		public final static int X_VALUE = 0;
		public final static int Y_VALUE = 1;
		public final static int Z_VALUE = 2;
		public float m_SunDepth=-10f;
		
		GL10 m_GL = null;

		float[] m_Eyeposition = { 0.0f, 0.0f, 0.0f };
		public Context myAppcontext;

		int origwidth, origheight;
		float angle = 0.0f;

		public class CGPoint 
		{
			float x;
			float y;
		};

		public class CGSize 
		{
			float height;
			float width;
		};

		public class CGRect 
		{
			CGPoint point;
			CGSize size;
		};

		public void setHoverPosition(GL10 gl, int nFlags, PointF location,
				PointF prevLocation, Planet m_Planet) 
		{
			double dx;
			double dy;
			Quaternion orientation = new Quaternion(0, 0, 0, 1.0);
			Quaternion tempQ;
			Vector3 offset = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 objectLoc = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 vpLoc = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 offsetv = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 temp = new Vector3(0.0f, 0.0f, 0.0f);
			float reference = 300.0f;
			float scale = 2.0f;
			float matrix3[][] = new float[3][3];
			boolean debug = false;
		
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		
			orientation = Miniglu.gluGetOrientation();					//1
		
			vpLoc.x = m_Eyeposition[0];						//2
			vpLoc.y = m_Eyeposition[1];
			vpLoc.z = m_Eyeposition[2];
		
			objectLoc.x = m_Planet.m_Pos[0];						//3
			objectLoc.y = m_Planet.m_Pos[1];
			objectLoc.z = m_Planet.m_Pos[2];
		
			offset.x = (objectLoc.x - vpLoc.x);						//4
			offset.y = (objectLoc.y - vpLoc.y);
			offset.z = (objectLoc.z - vpLoc.z);
		
			offsetv.z = temp.Vector3Distance(objectLoc, vpLoc); 				//5
		
			dx = (double) (location.x - prevLocation.x); 
			dy = (double) (location.y - prevLocation.y);
		
			float multiplier;
		
			multiplier = origwidth / reference;
		
			gl.glMatrixMode(GL10.GL_MODELVIEW);
		
			// Rotate around the X-axis.
		
			float c, s;									//6
			float rad = (float) (scale * multiplier * dy / reference)/2.0f;
		
			s = (float) Math.sin(rad * .5); 						
			c = (float) Math.cos(rad * .5);
		
			temp.x = s;
			temp.y = 0.0f;
			temp.z = 0.0f;
								
			Quaternion tempQ1 = new Quaternion(temp.x, temp.y, temp.z, c);
		
			tempQ1 = tempQ1.mulThis(orientation);
					
			// Rotate around the Y-axis.
		
			rad = (float) (scale * multiplier * dx / reference);				//7
		
			s = (float) Math.sin(rad * .5);
			c = (float) Math.cos(rad * .5);
								
			temp.x = 0.0f;
			temp.y = s;
			temp.z = 0.0f;
					
			Quaternion tempQ2 = new Quaternion(temp.x, temp.y, temp.z, c);
		
			tempQ2 = tempQ2.mulThis(tempQ1);
					
			orientation=tempQ2;
		
			matrix3 = orientation.toMatrix();						//8
		
			matrix3 = orientation.tranposeMatrix(matrix3); 				//9
			offsetv = orientation.Matrix3MultiplyVector3(matrix3, offsetv);
		
			m_Eyeposition[0] = (float)(objectLoc.x + offsetv.x); 				//10
			m_Eyeposition[1] =  (float)(objectLoc.y + offsetv.y);
			m_Eyeposition[2] =  (float)(objectLoc.z + offsetv.z);
		
			lookAtTarget(gl, m_Planet);
		}



		public void lookAtTarget(GL10 gl, Planet m_Planet) 
		{

			Miniglu.gluLookAt(gl, m_Eyeposition[X_VALUE], m_Eyeposition[Y_VALUE],
					m_Eyeposition[Z_VALUE], m_Planet.m_Pos[0],
					m_Planet.m_Pos[1], m_Planet.m_Pos[2], 0.0f, 1.0f, 0.0f);
		}

		public float getFieldOfView() 
		{
			return m_FieldOfView;
		}

		public void setFieldOfView(float fov) 
		{
			m_FieldOfView = fov;
			m_PinchFlag = 1;
		}

		public SolarSystem(Context context) 
		{
			this.myAppcontext = context;
		}

		public void onDrawFrame(GL10 gl) 
		{
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

			if (m_GestureMode == PINCH_GESTURE && m_PinchFlag == 1) 
			{
				setClipping(gl, origwidth, origheight);
				m_PinchFlag = 0;
			} 
			else if (m_GestureMode  == DRAG_GESTURE && m_DragFlag == 1) 
			{
				setHoverPosition(gl, 0, m_CurrentTouchPoint, m_LastTouchPoint, m_Earth);
				
				m_DragFlag = 0;
			}

			execute(gl);
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) 
		{
			gl.glViewport(0, 0, width, height);

			origwidth = width;
			origheight = height;

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			setClipping(gl, width, height);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
		}

		private void setClipping(GL10 gl, int width, int height) 
		{
			float aspectRatio;
			float zNear = .1f;
			float zFar = 2000f;
			float size;
			gl.glViewport(0, 0, width, height);

			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();

			gl.glEnable(GL10.GL_NORMALIZE);

			aspectRatio = (float) width / (float) height; 

			// Set the OpenGL projection matrix

			size = zNear * (float) (Math.tan((double) (m_FieldOfView / (DEGREES_PER_RADIAN * 2.0f))));
			
			gl.glFrustumf(-size, size, -size / aspectRatio, size / aspectRatio,
					zNear, zFar);

			gl.glMatrixMode(GL10.GL_MODELVIEW);
		}

		private void initGeometry(GL10 gl) 
		{
			// Let 1.0=1 million miles.
			// The sun's radius=.4.
			// The earth's radius=.04 (10x larger to make it easier to see).

			m_Eyeposition[X_VALUE] = 0.0f; 
			m_Eyeposition[Y_VALUE] = 0.0f;
			m_Eyeposition[Z_VALUE] = 93.25f;
			
			m_Earth = new Planet(48, 48, .04f, 1.0f, gl, myAppcontext, true,
					book.SolarSystem.R.drawable.earth); 
			m_Earth.setPosition(0.0f, 0.0f, 93.0f); 

			m_Sun = new Planet(48, 48, 0.4f, 1.0f, gl, myAppcontext, false, 0);
			m_Sun.setPosition(0.0f, 0.0f, 0.0f); 
		}
	    private void initLighting(GL10 gl) 
	    {
	    float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };
	    float[] posFill1 = {  15.0f, 15.0f, 2.0f, 1.0f };
	    float[] posFill2 = {  -10.0f, 2.0f, 7.0f, 1.0f };
	    
	    float[] white = { 1.0f, 1.0f, 1.0f, .5f };
	    float[] dimwhite = { 0.3f, 0.3f, 0.3f, .5f };
	    float[] dimblue = { 0.0f, 0.0f, .4f, 1.0f };
	    
	    float[] cyan = { 0.0f, 1.0f, 1.0f, 1.0f };
	    float[] yellow = { 1.0f, 1.0f, 0.0f, 1.0f };
	    float[] dimmagenta = { .75f, 0.0f, .25f, 1.0f };
	    
	    float[] dimcyan = { 0.2f, .2f, .5f, 1.0f };
	    
	    // lights go here
	    
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
	    gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));
	    
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION,
	    makeFloatBuffer(posFill1));
	    // gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE,
	    // makeFloatBuffer(dimblue));
	    gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR,
	    makeFloatBuffer(dimwhite));
	    
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION,
	    makeFloatBuffer(posFill2));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR,
	    makeFloatBuffer(dimcyan));
	    gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE,
	    makeFloatBuffer(dimwhite));
	    
	    // materials go here
	    
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
	    makeFloatBuffer(cyan));
	    gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
	    makeFloatBuffer(white));
	    
	    gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);
	    
	    gl.glShadeModel(GL10.GL_SMOOTH);
	    gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);
	    
	    gl.glEnable(GL10.GL_LIGHTING);
	    gl.glEnable(SS_SUNLIGHT);
	    gl.glEnable(SS_FILLLIGHT1);
	    gl.glEnable(SS_FILLLIGHT2);
	    gl.glLoadIdentity();
	    }
		private void oldinitLighting(GL10 gl) 
		{
			float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };
			float[] posFill1 = { -15.0f, 15.0f, 0.0f, 1.0f };
			float[] posFill2 = { -10.0f, -4.0f, 1.0f, 1.0f };

			float[] white = { 1.0f, 1.0f, 1.0f, 1.0f };
			float[] dimblue = { 0.0f, 0.0f, .2f, 1.0f };

			float[] cyan = { 0.0f, 1.0f, 1.0f, 1.0f };
			float[] yellow = { 1.0f, 1.0f, 0.0f, 1.0f };
			float[] dimmagenta = { .75f, 0.0f, .25f, 1.0f };

			float[] dimcyan = { 0.0f, .5f, .5f, 1.0f };

			// lights go here

			gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
			gl.glLightfv(SS_SUNLIGHT, GL10.GL_DIFFUSE, makeFloatBuffer(white));
			gl.glLightfv(SS_SUNLIGHT, GL10.GL_SPECULAR, makeFloatBuffer(yellow));

			gl.glLightfv(SS_FILLLIGHT1, GL10.GL_POSITION,
					makeFloatBuffer(posFill1));
			gl.glLightfv(SS_FILLLIGHT1, GL10.GL_DIFFUSE,
					makeFloatBuffer(dimblue));
			gl.glLightfv(SS_FILLLIGHT1, GL10.GL_SPECULAR,
					makeFloatBuffer(dimcyan));

			gl.glLightfv(SS_FILLLIGHT2, GL10.GL_POSITION,
					makeFloatBuffer(posFill2));
			gl.glLightfv(SS_FILLLIGHT2, GL10.GL_SPECULAR,
					makeFloatBuffer(dimmagenta));
			gl.glLightfv(SS_FILLLIGHT2, GL10.GL_DIFFUSE,
					makeFloatBuffer(dimblue));

			// materials go here
			
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
					makeFloatBuffer(cyan));
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
					makeFloatBuffer(white));

			gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 25);

			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glLightModelf(GL10.GL_LIGHT_MODEL_TWO_SIDE, 0.0f);

			gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(SS_SUNLIGHT);
			gl.glEnable(SS_FILLLIGHT1);
			gl.glEnable(SS_FILLLIGHT2);
			gl.glLoadIdentity();
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) 
		{
			gl.glDisable(GL10.GL_DITHER);

			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glCullFace(GL10.GL_BACK);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_DEPTH_TEST);
			
			initGeometry(gl);
			initLighting(gl);
			
			int resid;
			resid = book.SolarSystem.R.drawable.gimpsun3;
			m_FlareSource = CT.createTexture(gl, myAppcontext, true, resid);
			m_LensFlare.createFlares(gl, myAppcontext);
			
			m_constellation = new Constellations(myAppcontext);
			m_constellation.init(gl, myAppcontext);
			m_TexFont = new TexFont(myAppcontext, gl);

			try {
				m_TexFont.LoadFont("TimesNewRoman.bff", gl);
			} catch (java.io.EOFException e) {
				Log.d(TAG, "Caught EOFException");
			} catch (java.io.IOException e) {
				Log.d(TAG, "Caught IOException");
			}

		}
		TexFont m_TexFont;
		protected FloatBuffer makeFloatBuffer(float[] arr) 
		{
			ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
			bb.order(ByteOrder.nativeOrder());
			FloatBuffer fb = bb.asFloatBuffer();
			fb.put(arr);
			fb.position(0);
			return fb;
		}

		public float getRadius(Planet m_Planet) 
		{
			return (m_Planet.m_Scale );
		}

		public void execute(GL10 gl) 
		{
			float[] paleYellow = { 1.0f, 1.0f, 0.3f, 1.0f };
			float[] white = { 1.0f, 1.0f, 1.0f, 1.0f };
			float[] black = { 0.0f, 0.0f, 0.0f, 0.0f };
			float[] sunPos = { 0.0f, 0.0f, 0.0f, 1.0f };
			float sunWidth=0.0f;
			float sunScreenLoc[]=new float[4];					//xyz and radius
			float earthScreenLoc[]=new float[4];					//xyz and radius

			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glShadeModel(GL10.GL_SMOOTH);

			outlineOn = SolarSystemActivity.line_flag;
			lineOn = SolarSystemActivity.name_flag;
			m_constellation.executeOutlines(gl, outlineOn, lineOn);
			
			m_constellation.executeStars(gl);
			
			gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

			gl.glPushMatrix();

			gl.glTranslatef(-m_Eyeposition[X_VALUE], -m_Eyeposition[Y_VALUE],
					-m_Eyeposition[Z_VALUE]); // 1

			gl.glLightfv(SS_SUNLIGHT, GL10.GL_POSITION, makeFloatBuffer(sunPos));
			gl.glEnable(SS_SUNLIGHT);

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION,
					makeFloatBuffer(paleYellow));

			gl.glDisable(GL10.GL_DEPTH_TEST);
			executePlanet(m_Sun, gl, false,sunScreenLoc); 
			gl.glEnable(GL10.GL_DEPTH_TEST);
			
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION,makeFloatBuffer(black));

			gl.glPopMatrix();
			
			if ((m_LensFlare != null) && (sunScreenLoc[Z_INDEX] > 0.0f)) 
			{
	    	    CGPoint centerRelative = new CGPoint();
	    	    CGSize 	windowSize = new CGSize();
	    	    float sunsBodyWidth=44.0f;			//about the width of the sun's bady within the glare in the bitmap, in pixels.
	    	    float cx,cy;
	    	    float aspectRatio;
	    	    float scale=0f;
	    	    
	    	    DisplayMetrics display = myAppcontext.getResources().getDisplayMetrics();
	    	    windowSize.width = display.widthPixels;
	    	    windowSize.height = display.heightPixels;
	    	    
	    	    cx=windowSize.width/2.0f;   	    
	    	    cy=windowSize.height/2.0f;
	    	    
	    	    aspectRatio=cx/cy;
	    	    
	    	    centerRelative.x = sunScreenLoc[X_INDEX]-cx; 
	     	    centerRelative.y =(cy-sunScreenLoc[Y_INDEX])/aspectRatio;    
	    
	    	    scale=CT.renderTextureAt(gl, centerRelative.x, centerRelative.y, 0f, windowSize, m_FlareSource,sunScreenLoc[RADIUS_INDEX], 1.0f,1.0f, 1.0f, 1.0f);

	    	    sunWidth=scale*windowSize.width*sunsBodyWidth/256.0f;
			}

			gl.glEnable(SS_FILLLIGHT2);

			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glPushMatrix();

			gl.glTranslatef(-m_Eyeposition[X_VALUE], -m_Eyeposition[Y_VALUE],
					-m_Eyeposition[Z_VALUE]); 

			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
					makeFloatBuffer(white));
			gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
					makeFloatBuffer(white));

			executePlanet(m_Earth, gl, true,earthScreenLoc);
			
			gl.glPopMatrix();
			
			if ((m_LensFlare != null) && (sunScreenLoc[Z_INDEX] > 0)) 
			{
				float scale = 1.0f;
				float delX = origwidth / 2.0f - sunScreenLoc[X_INDEX];
				float delY = origheight / 2.0f - sunScreenLoc[Y_INDEX];
				float grazeDist = earthScreenLoc[RADIUS_INDEX] + sunWidth;
				float percentVisible = 1.0f;
				float vanishDist = earthScreenLoc[RADIUS_INDEX] - sunWidth;

				float distanceBetweenBodies = (float) Math.sqrt(delX * delX
						+ delY * delY);

				if ((distanceBetweenBodies > vanishDist)&& (distanceBetweenBodies < grazeDist)) 
				{
					percentVisible = (float) ((distanceBetweenBodies - vanishDist) / sunWidth);

					if (percentVisible > 1.0) 
						percentVisible = 1.0f;
					else if (percentVisible < 0.3)		
						percentVisible = .5f;
				} 
				else if (distanceBetweenBodies > grazeDist) 
				{
					percentVisible = 1.0f;
				} 
				else 
				{
					percentVisible = 0.0f;
				}

				scale = STANDARD_FOV / m_FieldOfView;

				CGPoint source = new CGPoint();
				source.x = sunScreenLoc[X_INDEX];
				source.y = sunScreenLoc[Y_INDEX];
				CGSize winsize = new CGSize();
				winsize.width = origwidth;
				winsize.height = origheight;
				
				if (percentVisible > 0.0) 
				{
					lens_flare = SolarSystemActivity.lens_flare_flag;
					if (lens_flare == true)
					m_LensFlare.execute(gl, winsize, source, scale,	percentVisible);
				}
			}
			//gl.glDisable(GL10.GL_LIGHTING);

		}

		public void executePlanet(Planet planet, GL10 gl, Boolean render,float[] screenLoc) 		
		{
			Vector3 planetPos = new Vector3(0, 0, 0);
			float temp;
			float distance;
			float screenRadius;
			
			gl.glPushMatrix();

			planetPos.x = planet.m_Pos[0];
			planetPos.y = planet.m_Pos[1];
			planetPos.z = planet.m_Pos[2];
			
			gl.glTranslatef((float) planetPos.x, (float) planetPos.y,(float) planetPos.z);

			if (render) 
			{
				planet.draw(gl);
			}
			
			Vector3 eyePosition = new Vector3(0, 0, 0);
			
			eyePosition.x = m_Eyeposition[X_VALUE];
			eyePosition.y = m_Eyeposition[Y_VALUE];
			eyePosition.z = m_Eyeposition[Z_VALUE];
			
			distance = (float) planetPos.Vector3Distance(eyePosition, planetPos);

			float fieldWidthRadians = (m_FieldOfView /DEGREES_PER_RADIAN) / 2.0f;
			temp = (float) ((0.5f * origwidth) / Math.tan(fieldWidthRadians));
			
			screenRadius = temp * getRadius(planet) / distance;
						
			if(screenLoc!=null)
			{
				Miniglu.gluGetScreenLocation(gl, (float) planetPos.x, (float) -planetPos.y,
					(float) planetPos.z, (float) screenRadius, render,screenLoc); 
			}
			
			screenLoc[RADIUS_INDEX]=screenRadius;
					
			gl.glPopMatrix();

			angle += .5f;
		}
	}
}