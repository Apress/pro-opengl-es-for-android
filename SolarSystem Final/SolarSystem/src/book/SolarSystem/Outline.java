package book.SolarSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import book.SolarSystem.Stars.starData;
import book.SolarSystem.*;

public class Outline {
	int m_Width;
	int m_Height, j;
	float m_MaxS;
	float m_MaxT;
	Context context;
	int flag = 0;
	public String TAG = "myOutline";
	int colorArrSize = 0;
	FloatBuffer m_NameData;
	int vertexArrSize = 0;
	int[] name_textures;

	float m_Red;
	float m_Green;
	float m_Blue;
	float m_Alpha;

	public class plist_data 
	{
		String name;
		float name_ra;
		float dec;
		int num_coordinates;
		float x;
		float y;
		float z;
		float r, g, b, a;

		coords_data[] mycoords;
		OutlineDrawData[] m_Data;
	}

	public class coords_data 
	{
		float ra;
		float dec;
	}

	public class OutlineDrawData 
	{
		float x;
		float y;
		float z;
		float r, g, b, a;
	}

	plist_data[] myPlist;

	public Outline(Context myApplicationContext) 
	{
		this.context = myApplicationContext;
	}

	int[] textures = new int[1];

	public int initWithText(GL10 gl, Context contextRegf, String string,
			CGSize size, int alignment, String font) // 1
	{
		int width;
		int height;
		int i;
		// void data;
		// CGColorSpaceRef colorSpace;
		int[] saveName = new int[1];
		GL11 gl11 = (GL11) gl;
		gl.glEnable(GL10.GL_TEXTURE_2D);

		width = (int) size.width;
		if ((width != 1) && ((width & (width - 1)) == 1)) 
		{
			i = 1;
			while (i < width)
				i *= 2;
			width = i;
		}

		height = (int) size.height;
		
		if ((height != 1) && ((height & (height - 1)) == 1)) 
		{
			i = 1;
			while (i < height)
				i *= 2;
			height = i;
		}

		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_4444);
		
		Canvas canvas = new Canvas(bitmap);
		bitmap.eraseColor(0);

		Paint textPaint = new Paint();
		textPaint.setTextSize(100);
		textPaint.setAntiAlias(true);
		textPaint.setARGB(0xff, 0x00, 0x00, 0x00);

		canvas.drawText(string, s_x, s_y, textPaint);

		gl.glGenTextures(1, textures, 0);
		gl11.glGetIntegerv(GL11.GL_TEXTURE_BINDING_2D, saveName, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); 
		gl.glBindTexture(GL10.GL_TEXTURE_2D, saveName[0]); 

		m_Width = width;
		m_Height = height;
		m_MaxS = size.width / (float) width;
		m_MaxT = size.height / (float) height;
		bitmap.recycle();

		return (textures[0]);
	}

	public void renderAtPoint(GL10 gl, CGPoint point, float depth, float red,
			float green, float blue, float alpha, float scale, int texture) 
	{
		int[] boxRect = new int[4];
		GL11Ext gl11e = (GL11Ext) gl;
		GL11 gl11 = (GL11) gl;
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);// textures[0]);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_LIGHTING);

		gl.glColor4f(red, green, blue, alpha);

		boxRect[0] = 0;
		boxRect[1] = 0;
		boxRect[2] = m_Width;
		boxRect[3] = m_Height;

		scale = 100;

		gl11.glTexParameteriv(GL10.GL_TEXTURE_2D,
				GL11Ext.GL_TEXTURE_CROP_RECT_OES, makeIntBuffer(boxRect));

		gl11e.glDrawTexfOES(point.x * scale, (480 - point.y) * scale, depth,
				m_Width, m_Height);

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_LIGHTING);
	}

	Utils mUtilOutLine = new Utils();
	int m_TotalOutline, arraySize;

	public void initWithContentsOfFile(String filename) 
	{
		float mag;
		float x, y, z, flag = 0;
		int index, j = 0;
		float ra = 0, dec = 0;

		filename = mUtilOutLine.readPlistFromAssets(context, filename);
		final Document doc = mUtilOutLine.XMLfromString(filename);
		final NodeList nodes_array = doc.getElementsByTagName("dict"); 

		m_TotalOutline = nodes_array.getLength();
		Log.d(TAG, "myoutline are:" + m_TotalOutline);
		myPlist = new plist_data[m_TotalOutline];

		for (index = 0; index < m_TotalOutline; index++) 
		{
			final Node node = nodes_array.item(index);
			
			if (node.getNodeType() == Node.ELEMENT_NODE) 
			{
				final Element e = (Element) nodes_array.item(index);

				final NodeList nodeKey = e.getElementsByTagName("key");
				final NodeList nodeString = e.getElementsByTagName("real");
				myPlist[index] = new plist_data();

				for (int i = 0; i < nodeString.getLength(); i++) 
				{
					final Element eleKey = (Element) nodeKey.item(i);
					final Element eleString = (Element) nodeString.item(i);

					if (eleString != null) 
					{
						String strValue = mUtilOutLine.getValue(eleString,
								"real");

						String keyName = mUtilOutLine.getValue(eleKey, "key");

						String keyNameTest = mUtilOutLine.getValue(eleKey, "*");

						if (mUtilOutLine.getValue(eleKey, "key").equals("name")) 
						{
							myPlist[index].name = strValue;
							j = 0;
						} 
						else if (mUtilOutLine.getValue(eleKey, "key").equals("name_ra")) 
						{
							myPlist[index].name_ra = Float.parseFloat(strValue);
						} 
						else if (mUtilOutLine.getValue(eleKey, "key").equals("decreasing")) 
						{
							myPlist[index].dec = Float.parseFloat(strValue);
						} 
						else if (mUtilOutLine.getValue(eleKey, "key").equals("coordinates")) 
						{
							if (strValue == null) 
							{
								strValue = "";
							}
							arraySize = Integer.parseInt(strValue);
							myPlist[index].num_coordinates = arraySize;
							myPlist[index].mycoords = new coords_data[arraySize];
						} 
						else if (mUtilOutLine.getValue(eleKey, "key").equals("ra")) 
						{
							myPlist[index].mycoords[j] = new coords_data();
							myPlist[index].mycoords[j].ra = Float
									.parseFloat(strValue);
						} 
						else if (mUtilOutLine.getValue(eleKey, "key").equals("dec")) 
						{
							myPlist[index].mycoords[j].dec = Float
									.parseFloat(strValue);
							j++;
						}
					}
				}
			}
		}

		getCoordinatesNew();
	}

	public void getCoordinatesNew() 
	{
		int i, j;
		for (i = 0; i < m_TotalOutline; i++) 
		{
			Log.d(TAG, "name   [" + i + "]:" + myPlist[i].name);
			Log.d(TAG, "name_ra[" + i + "]:" + myPlist[i].name_ra);
			Log.d(TAG, "dec    [" + i + "]:" + myPlist[i].dec);

			for (j = 0; j < myPlist[i].num_coordinates; j++) 
			{
				Log.d(TAG, "here ra      [" + j + "]:"
						+ myPlist[i].mycoords[j].ra);
				Log.d(TAG, "here dec     [" + j + "]:"
						+ myPlist[i].mycoords[j].dec);
			}

			colorArrSize += myPlist[i].num_coordinates;
			vertexArrSize += myPlist[i].num_coordinates;
			Log.d(TAG, "=============================================");
		}

	}

	public void getCoordinates() 
	{
		int i, j;
		for (i = 0; i < m_TotalOutline; i++) {
			Log.d(TAG, "name   [" + i + "]:" + myPlist[i].name);
			Log.d(TAG, "name_ra[" + i + "]:" + myPlist[i].name_ra);
			Log.d(TAG, "dec    [" + i + "]:" + myPlist[i].dec);

			mUtilOutLine.sphereToRectTheta(myPlist[i].name_ra
					/ mUtilOutLine.DEGREES_PER_RADIAN, myPlist[i].dec
					/ mUtilOutLine.DEGREES_PER_RADIAN,
					mUtilOutLine.STANDARD_RADIUS);
			myPlist[i].x = mUtilOutLine.getXPrime();
			myPlist[i].y = mUtilOutLine.getYPrime();
			myPlist[i].z = mUtilOutLine.getZPrime();
			myPlist[i].r = myPlist[i].g = myPlist[i].b = 0.0f;
			myPlist[i].a = 1.0f;

			myPlist[i].m_Data = new OutlineDrawData[myPlist[i].num_coordinates];
			
			for (j = 0; j < myPlist[i].num_coordinates; j++) 
			{
				myPlist[i].m_Data[j] = new OutlineDrawData();

				mUtilOutLine.sphereToRectTheta(myPlist[i].mycoords[j].ra
						/ mUtilOutLine.DEGREES_PER_RADIAN,
						myPlist[i].mycoords[j].dec
								/ mUtilOutLine.DEGREES_PER_RADIAN,
						mUtilOutLine.STANDARD_RADIUS);
				myPlist[i].m_Data[j].x = mUtilOutLine.getXPrime();
				myPlist[i].m_Data[j].y = mUtilOutLine.getYPrime();
				myPlist[i].m_Data[j].z = mUtilOutLine.getZPrime();

				myPlist[i].m_Data[j].r = myPlist[i].m_Data[j].g = myPlist[i].m_Data[j].b = 0.0f;
				myPlist[i].m_Data[j].a = 1.0f;
				Log.d(TAG, "x      [" + j + "]:" + myPlist[i].m_Data[j].x);
				Log.d(TAG, "y      [" + j + "]:" + myPlist[i].m_Data[j].y);
				Log.d(TAG, "y      [" + j + "]:" + myPlist[i].m_Data[j].z);
			}

			colorArrSize += myPlist[i].num_coordinates;
			vertexArrSize += myPlist[i].num_coordinates;
			Log.d(TAG, "=============================================");
		}

	}

	public void execute(GL10 gl, boolean showLines, boolean showNames) 
	{
		int i;
		int count;
		// NSMutableArray *colorArray;
		// NSMutableDictionary *object;
		float[] colorArray;
		plist_data object = new plist_data();
		float red, green, blue, alpha;
		OutlineDrawData[] data_line;
		plist_data[] data_name;
		int numVertices = 0;
		float lineWidth = 3.0f;
		float[] screenLoc = new float[4];
		float scale = 1.0f; // TODO: FILL THIS with screen scale

		// scale=[[UIScreen mainScreen] scale];

		gl.glLineWidth(lineWidth * scale);

		gl.glPushMatrix();

		count = myPlist.length;

		if (showLines) 
		{
			for (i = 0; i < count; i++) // enumerate each outline set
			{
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
				gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
				gl.glDisable(GL10.GL_TEXTURE_2D);
				gl.glDisable(GL10.GL_LIGHTING);
				gl.glEnable(GL10.GL_DEPTH_TEST);
				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnable(GL10.GL_BLEND);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

				object = myPlist[i]; // object points to each outline set

				// colorArray = [object objectForKey:@"color_main"];

				if (m_Red < 0) 
				{
					red = myPlist[i].r;
					green = myPlist[i].g;
					blue = myPlist[i].b;
					alpha = myPlist[i].a;
					/*
					 * red=[[colorArray objectAtIndex:0]floatValue];
					 * green=[[colorArray objectAtIndex:1]floatValue];
					 * blue=[[colorArray objectAtIndex:2]floatValue];
					 * alpha=[[colorArray objectAtIndex:3]floatValue];
					 */
				} 
				else 
				{
					red = m_Red;
					green = m_Green;
					blue = m_Blue;
					alpha = m_Alpha;
				}

				gl.glColor4f(red, green, blue, alpha);

				numVertices = myPlist[i].num_coordinates; // [[object
															// objectForKey:@"coordinates"]count];
				// data=(GLfloat *)[[object objectForKey:@"binarydata"]bytes];
				data_line = new OutlineDrawData[numVertices];
				data_line = myPlist[i].m_Data;

				if (numVertices > 0) 
				{
					FloatBuffer m_LineData;
					m_LineData = createInterleavedLineData(numVertices,
							data_line);
					gl.glVertexPointer(3, GL10.GL_FLOAT, 0, m_LineData);

					gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, numVertices);
				}

			}
		}
		
		DisplayMetrics display = context.getResources().getDisplayMetrics();
		float width = display.widthPixels;
	    float height = display.heightPixels;
	    
		if (showNames) 
		{
			numVertices = m_TotalOutline;
			data_name = new plist_data[numVertices];// (GLfloat *)[[object
													// objectForKey:@"binary_name_coords"]bytes];
			for (i = 0; i < count; i++) // enumerate each outline set
			{
				// object = myPlist[i]; //object points to each outline set

				data_name[i] = myPlist[i];

				if (data_name != null) 
				{
					Miniglu.gluGetScreenLocation(gl, data_name[i].x, data_name[i].y, data_name[i].z, 0, false, screenLoc);

					if (screenLoc[2] > 0.0f) 
					{
						CGPoint point = new CGPoint();
						point.x = screenLoc[0];
						point.y = screenLoc[1];
						renderConstName(gl, myPlist[i].name, (int) point.x, (int) (height - point.y), 1.0f, 0.0f, 0.0f);
					}

				}
			}
		}
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glPopMatrix();
		gl.glEnable(GL10.GL_LIGHTING);
	}
	public void renderConstName(GL10 gl, String name, int x, int y, float r, float g, float b) 
	{
		gl.glDisable(GL10.GL_LIGHTING);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL10.GL_BLEND);

		m_TexFont.SetPolyColor(r, g, b);
		m_TexFont.PrintAt(gl, name.toUpperCase(), x, y);
	}

	public class CGPoint 
	{
		float x;
		float y;
	};

	TexFont m_TexFont;

	public void init(GL10 gl, String filename, float red,
			float green, float blue, float alpha) 
	{
		float x, y, z;
		int i, j;
		float ra, dec;
		int nameTexture;
		int numpoints = 0;
		float[] data;
		int numbytes;
		int index = 0;
		coords_data[] coords;
		String name;

		m_Red = red;
		m_Green = green;
		m_Blue = blue;
		m_Alpha = alpha;

		initWithContentsOfFile(filename);// this fills myPlist

		// convert the ra/dec to xyz
		name_textures = new int[m_TotalOutline];

		for (i = 0; i < m_TotalOutline; i++) 											
		{
			name = myPlist[i].name;

			if ((name != null) && (name.length() != 0)) 													
			{
				ra = myPlist[i].name_ra; 
				dec = myPlist[i].dec; 

				mUtilOutLine.sphereToRectTheta(15.0f * ra
						/ mUtilOutLine.DEGREES_PER_RADIAN, dec
						/ mUtilOutLine.DEGREES_PER_RADIAN,
						mUtilOutLine.STANDARD_RADIUS);


				myPlist[i].x = mUtilOutLine.getXPrime();
				myPlist[i].y = mUtilOutLine.getYPrime();
				myPlist[i].z = mUtilOutLine.getZPrime();
			}
			numpoints = myPlist[i].num_coordinates;

			myPlist[i].m_Data = new OutlineDrawData[myPlist[i].num_coordinates];
			coords = new coords_data[numpoints];
			coords = myPlist[i].mycoords; 

			for (j = 0; j < numpoints; j++)
			{
				myPlist[i].m_Data[j] = new OutlineDrawData();

				ra = coords[j].ra;
				dec = coords[j].dec;

				mUtilOutLine.sphereToRectTheta(15.0f * ra
						/ mUtilOutLine.DEGREES_PER_RADIAN, dec
						/ mUtilOutLine.DEGREES_PER_RADIAN,
						mUtilOutLine.STANDARD_RADIUS);

				myPlist[i].m_Data[j].x = mUtilOutLine.getXPrime();
				myPlist[i].m_Data[j].y = mUtilOutLine.getYPrime();
				myPlist[i].m_Data[j].z = mUtilOutLine.getZPrime();

				myPlist[i].m_Data[j].r = myPlist[i].m_Data[j].g = myPlist[i].m_Data[j].b = 0.0f;
				myPlist[i].m_Data[j].a = 1.0f;

			}
		}

		m_TexFont = new TexFont(context, gl);

		try 
		{
			m_TexFont.LoadFont("TimesNewRoman.bff", gl);
		} catch (java.io.EOFException e) 
		{
			Log.d(TAG, "Caught EOFException");
		} catch (java.io.IOException e) 
		{
			Log.d(TAG, "Caught IOException");
		}
	}

	public class CGSize 
	{
		float height;
		float width;
	};

	public int createLabel(GL10 gl, String label) {
		float fontSize = 20;
		CGSize size = new CGSize();
		int labelTexture;
		float scale;

		size.width = 50;
		size.height = 50;
		labelTexture = initWithText(gl, context, label, size, 0, null);
		return labelTexture;
	}

	private FloatBuffer createInterleavedColorData(int size,OutlineDrawData[] data) 
	{
		int i, j;
		float[] vertexArray;
		int m_Numvertices = 3;
		int vertexSize = m_Numvertices * size;

		vertexArray = new float[vertexSize];

		for (i = 0; i < arraySize; i++) 
		{
			j = i * m_Numvertices;
			vertexArray[j + 0] = data[i].x;
			vertexArray[j + 1] = data[i].y;
			vertexArray[j + 2] = data[i].z;
		}
		return (makeFloatBuffer(vertexArray));
	}

	private FloatBuffer createInterleavedLineData(int size,OutlineDrawData[] data) 
	{
		int i, j;
		float[] vertexArray;
		int m_Numvertices = 3;
		int vertexSize = m_Numvertices * size;

		vertexArray = new float[vertexSize];

		for (i = 0; i < size; i++) {
			j = i * m_Numvertices;
			vertexArray[j + 0] = data[i].x;
			vertexArray[j + 1] = data[i].y;
			vertexArray[j + 2] = data[i].z;
		}
		return (makeFloatBuffer(vertexArray));
	}

	protected static IntBuffer makeIntBuffer(int[] arr) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer fb = bb.asIntBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	protected static FloatBuffer makeFloatBuffer(float[] arr) 
	{
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	void gluGetScreenLocation(GL10 gl, float xa, float ya, float za) 
	{
		float sx, sy, sz = 0;
		float[] mvmatrix = new float[16];
		float[] projmatrix = new float[16];
		int[] viewport = new int[4];
		int i;
		GL11 gl11 = (GL11) gl;
		gl11.glGetIntegerv(GL11.GL_VIEWPORT, viewport, 0); 
		gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projmatrix, 0);

		gluProject(xa, ya, za, mvmatrix, projmatrix, viewport);

		winy = viewport[3] - winy; // 5

		sx = winx;
		sy = winy;

		sz = winz;

		float scale = 1.0f; // 1 for pre-retine and 2 for other [[UIScreen
							// mainScreen] scale]; //6
		sx /= scale;
		sy /= scale;
		setSParams(sx, sy, sz);
	}

	float s_x, s_y, s_z;

	public void setSParams(float sx, float sy, float sz) 
	{
		s_x = sx;
		s_y = sy;
		s_z = sz;
	}

	public boolean gluProject(float objx, float objy, float objz,
			float[] modelMatrix, float[] projMatrix, int[] viewport) 
	{
		float[] in = new float[4];
		float[] out = new float[4];
		in[0] = objx; // 1
		in[1] = objy;
		in[2] = objz;
		in[3] = 1.0f;

		gluMultMatrixVector3f(modelMatrix, in, out); 

		gluMultMatrixVector3f(projMatrix, out, in);

		if (in[3] == 0.0f)
			in[3] = 1.0f;

		in[0] /= in[3];
		in[1] /= in[3];
		in[2] /= in[3];

		/* Map x, y and z to range 0-1 */

		in[0] = in[0] * 0.5f + 0.5f; 
		in[1] = in[1] * 0.5f + 0.5f;
		in[2] = in[2] * 0.5f + 0.5f;

		/* Map x,y to viewport */
		
		in[0] = in[0] * viewport[2] + viewport[0];
		in[1] = in[1] * viewport[3] + viewport[1];

		setWinParams(in[0], in[1], in[3]);
		return (true);
	}

	float winx, winy, winz;

	public void setWinParams(float x, float y, float z) 
	{
		winx = x;
		winy = y;
		winz = z;
	}

	void gluMultMatrixVector3f(float[] matrix, float[] in, float[] out) 
	{
		int i;

		for (i = 0; i < 4; i++) 
		{
			out[i] = in[0] * matrix[0 * 4 + i] + in[1] * matrix[1 * 4 + i]
					+ in[2] * matrix[2 * 4 + i] + in[3] * matrix[3 * 4 + i];
		}
	}
}
