package book.SolarSystem;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class Miniglu 
{		
	static float DEGREES_PER_RADIAN=57.29f;
	
		static Quaternion m_Quaternion = new Quaternion(0, 0, 0, 1);

		public static void gluLookAt(GL10 gl, float eyex, float eyey, float eyez,
				float centerx, float centery, float centerz, float upx,
				float upy, float upz) 
		{
			Vector3 up = new Vector3(0.0f, 0.0f, 0.0f); 
			Vector3 from = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 to = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 lookat = new Vector3(0.0f, 0.0f, 0.0f);
			Vector3 axis = new Vector3(0.0f, 0.0f, 0.0f);
			float angle;
			
			lookat.x = centerx; 
			lookat.y = centery;
			lookat.z = centerz;

			from.x = eyex;
			from.y = eyey;
			from.z = eyez;

			to.x = lookat.x;
			to.y = lookat.y;
			to.z = lookat.z;

			up.x = upx;
			up.y = upy;
			up.z = upz;

			Vector3 temp = new Vector3(0, 0, 0);
			temp = temp.Vector3Sub(to, from); 
			Vector3 n = temp.normalise(temp); 

			temp = temp.Vector3CrossProduct(n, up);
			Vector3 v = temp.normalise(temp);

			Vector3 u = temp.Vector3CrossProduct(v, n);

			float[][] matrix;

			matrix = temp.Matrix3MakeWithRows(v, u, temp.Vector3Negate(n));

			m_Quaternion = m_Quaternion.QuaternionMakeWithMatrix3(matrix);

			m_Quaternion.printThis("GluLookat:");

			axis = m_Quaternion.QuaternionAxis();
			angle = m_Quaternion.QuaternionAngle();

			gl.glRotatef((float) angle * DEGREES_PER_RADIAN, (float) axis.x,
					(float) axis.y, (float) axis.z); // 5
		}

		public static void gluGetScreenLocation(GL10 gl, float xa, float ya, float za,
				float screenRadius, boolean render, float[] screenLoc) 
		{
			float[] mvmatrix = new float[16];
			float[] projmatrix = new float[16];
			int[] viewport = new int[4];
			float[] xyz = new float[3];

			GL11 gl11 = (GL11) gl;
			gl11.glGetIntegerv(GL11.GL_VIEWPORT, viewport, 0); // 4
			gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mvmatrix, 0);
			gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projmatrix, 0);

			gluProject(xa, ya, za, mvmatrix, projmatrix, viewport,xyz);

			xyz[1]=viewport[3]-xyz[1];
			
			screenLoc[0] = xyz[0];
			screenLoc[1] = xyz[1];
			screenLoc[2] = xyz[2];		
		}
		
		public static boolean gluProject(float objx, float objy, float objz,
				float[] modelMatrix, float[] projMatrix, int[] viewport,float[] win) 
		{
			float[] in = new float[4];
			float[] out = new float[4];
			in[0] = objx; 
			in[1] = objy;
			in[2] = objz;
			in[3] = 1.0f;

			gluMultMatrixVector3f (modelMatrix, in, out);
			
			gluMultMatrixVector3f (projMatrix, out, in);

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

			win[0] = in[0] * viewport[2] + viewport[0];
			win[1] = in[1] * viewport[3] + viewport[1];
			win[2] = in[3];
			
			return (true);
		}

		public static void gluMultMatrixVector3f(float[] matrix, float[] in, float[] out) 
		{
			int i;

			for (i = 0; i < 4; i++) 
			{
				out[i] = 
						in[0] * matrix[0 * 4 + i] + 
						in[1] * matrix[1 * 4 + i] + 
						in[2] * matrix[2 * 4 + i] + 
						in[3] * matrix[3 * 4 + i];
			}
		}
		
		static public Quaternion gluGetOrientation() 
		{
			return m_Quaternion;
		}
}
