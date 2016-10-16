package book.SolarSystem;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Utils 
{
	float XPrime, YPrime, ZPrime;
	public final static float RADIANS_PER_90_DEGREES = 1.570796326794897f;
	public final static float DEGREES_PER_RADIAN = 57.2977f;
	public final static float STANDARD_RADIUS = 1000.0f;
	public String TAG = "myutils";
	
	public void sphereToRectTheta(float theta, float phi, float radius) 
	{
		float cos_theta, sin_theta, cos_phi, sin_phi;
		float xprime, yprime, zprime;

		phi = RADIANS_PER_90_DEGREES - phi; /*
											 * phi is to be measured starting
											 * from the z-axis.
											 */

		sin_theta = (float) Math.sin(theta);
		cos_theta = (float) Math.cos(theta);

		sin_phi = (float) Math.sin(phi);
		cos_phi = (float) Math.cos(phi);

		xprime = (float) (radius * cos_theta * sin_phi);
		yprime = (float) (radius * cos_phi);
		zprime = (float) -1.0 * (radius * sin_theta * sin_phi);
		setXYZPrime(xprime, yprime, zprime);
	}

	public void setXYZPrime(float x, float y, float z) 
	{
		XPrime = x;
		YPrime = y;
		ZPrime = z;
	}

	public float getXPrime() 
	{
		return XPrime;
	}

	public float getYPrime() 
	{
		return YPrime;
	}

	public float getZPrime() 
	{
		return ZPrime;
	}

	public String readPlistFromAssets(Context context, String filename) 
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		
		try 
		{
			InputStream is = context.getAssets().open(filename);
			br = new BufferedReader(new InputStreamReader(is));

			String temp;
			
			while ((temp = br.readLine()) != null)
				sb.append(temp);			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				br.close(); // stop reading
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	// Create xml document object from XML String
	public Document XMLfromString(String xml) 
	{
		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try 
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			doc = db.parse(is);
		} 
		catch (ParserConfigurationException e) 
		{
			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} 
		catch (SAXException e) 
		{
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} 
		catch (IOException e) 
		{
			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}

		return doc;
	}

	// fetch value from Text Node only
	public   String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	/// Fetch value from XML Node
	public   String getValue(Element item, String str) 
	{
		NodeList n;
		String value="";
		Node node;
		
		try
		{
			boolean doesThisWork=false;
			
			//this is the way it should work, but a bug on some Honeycomb installations 
			//return an empty list for the value of "n"
			
			if(doesThisWork)
			{
				n = item.getElementsByTagName(str);
				
				node=n.item(0);
				
				value=node.getNodeValue();
				
				value=getElementValue(node);
			}	
			else
			{
				//this isn't the best way, but it works on the XOOM while running Android 3.2.1	
				n=item.getChildNodes();
			
				node=n.item(0);
			
				value=node.getNodeValue();			
			}
		}
		catch (org.w3c.dom.DOMException e)
		{
			System.out.println("DOMException: " + e.getMessage());			
		}
				
		return value;
	}
}
