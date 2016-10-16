package book.SolarSystem;

public final class Vector3 
{
	public double x,y,z;

	public double getX() 
	{
		return x;
	}

	public double getY() 
	{
		return y;
	}

	public double getZ() 
	{
		return z;
	}

	public Vector3(double ix, double iy, double iz) 
	{
		x = ix;
		y = iy;
		z = iz;
	}

	public void set(double ix, double iy, double iz) 
	{
		x = ix;
		y = iy;
		z = iz;
	}

	public double magnitude() 
	{
		return Math.sqrt(x*x+y*y+z*z);
	}

	public void multiply(double f) 
	{
		x *= f;
		y *= f;
		z *= f;
	}

	public Vector3 normalise(Vector3 temp) 
	{
		double mag = magnitude();
		temp.x /= mag;
		temp.y /= mag;
		temp.z /= mag;
		return (temp);
	}
	
	public double Vector3Distance(Vector3 a, Vector3 b) 
	{
		double temp = Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2) + Math.pow(b.z - a.z, 2);
		return (Math.sqrt(temp));
	}
	
	public Vector3 Vector3Add(Vector3 a, Vector3 b) 
	{
		Vector3 temp = new Vector3(0, 0, 0);
		temp.x = b.x + a.x;
		temp.y = b.y + a.y;
		temp.z = b.z + a.z;
		return (temp);
	}
	
	public Vector3 Vector3Sub(Vector3 a, Vector3 b) 
	{
		Vector3 temp = new Vector3(0, 0, 0);
		temp.x = a.x - b.x;
		temp.y = a.y - b.y;
		temp.z = a.z - b.z;
		return (temp);
	}

	public Vector3 Vector3CrossProduct(Vector3 A, Vector3 B) 
	{
		Vector3 temp = new Vector3(0, 0, 0);
		temp.x = A.y * B.z - B.y * A.z;
		temp.y = A.z * B.x - B.z * A.x;
		temp.z = A.x * B.y - B.x * A.y; 
		return temp;
	}
	
	public Vector3 Vector3Negate(Vector3 a) 
	{
		Vector3 temp = new Vector3(0, 0, 0);
		temp.x = -a.x;
		temp.y = -a.y;
		temp.z = -a.z;
		return (temp);
	}

	public float[][] Matrix3MakeWithRows(Vector3 v, Vector3 u, Vector3 z) 
	{
		float tmp[][] = new float[3][3];
		tmp[0][0] = (float) v.x;
		tmp[0][1] = (float) v.y;
		tmp[0][2] = (float) v.z;
		
		tmp[1][0] = (float) u.x;
		tmp[1][1] = (float) u.y;
		tmp[1][2] = (float) u.z;
		
		tmp[2][0] = (float) z.x;
		tmp[2][1] = (float) z.y;
		tmp[2][2] = (float) z.z;
		
		return tmp;
	}
}