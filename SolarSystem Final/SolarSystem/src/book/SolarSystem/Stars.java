package book.SolarSystem;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.Context;
import android.util.Log;

public class Stars 
{
	Context context;
	public String TAG = "myStars";
	
	public Stars(Context myApplicationContext) 
	{
		this.context = myApplicationContext;
	}
	public class starData                                                                                                                    //1
	{
	    float x;
	    float y;
	    float z;
	    float mag;
	    float r,g,b,a;
	    float hdnum;
	    float ra, dec;
	};

	starData[] m_Data;
	FloatBuffer m_ColorData, m_VertexData;
    int m_TotalStars;
    int totalElems;
    Utils mUtilStar = new Utils();    
    int j, i;
    
    //static float ra = 0, dec = 0; 
    
	public void init(Context context, String filename)                                                                                
	{
		String xml;
		xml = mUtilStar.readPlistFromAssets(context, filename); 
	
	    float mag = 0;
	    float flag = 0;
	    int index,j = 0;
	    
	    final Document doc = mUtilStar.XMLfromString(xml);        
        final NodeList nodes_array = doc.getElementsByTagName("array");		
	    m_TotalStars= nodes_array.getLength();
	   
	    for(index = 0; index < m_TotalStars; index++)                                                                                        
	    {
	    	
	    	final Node node = nodes_array.item(index);
	    	
			if ( node.getNodeType() == Node.ELEMENT_NODE ) 
			{
				final Element e = (Element)nodes_array.item(index);
				
				final NodeList nodeKey = e.getElementsByTagName("key");
				final NodeList nodeString = e.getElementsByTagName("real");
				totalElems = nodeString.getLength();
				Log.d(TAG, "nodeString.getLength():"+totalElems);
				m_Data = new starData[totalElems];
				Log.d(TAG, "m_TotalStars:" + m_TotalStars);

				for (i = 0; i< totalElems/4; i++) 
				{
					m_Data[i] = new starData();
				}
				
				for (i = 0, j = 0; i<totalElems; i++) 
				{
					final Element eleKey = (Element)nodeKey.item(i);
					final Element eleString = (Element)nodeString.item(i);
					
					if ( eleString != null ) 
					{
						String strValue = mUtilStar.getValue(eleString, "real");
					
						if(mUtilStar.getValue(eleKey, "key").equals("hdnum")) 
						{
							m_Data[j].hdnum = Float.parseFloat(strValue);
						} 
						else if(mUtilStar.getValue(eleKey, "key").equals("ra")) 
						{
							m_Data[j].ra = Float.parseFloat(strValue);
						} 
						else if(mUtilStar.getValue(eleKey, "key").equals("dec")) 
						{
							m_Data[j].dec = Float.parseFloat(strValue);
						} 
						else if(mUtilStar.getValue(eleKey, "key").equals("mag")) 
						{
							m_Data[j].mag = Float.parseFloat(strValue);							
							flag = 1;
						}
						
						if (flag == 1) 
						{
							mUtilStar.sphereToRectTheta(m_Data[j].ra/Utils.DEGREES_PER_RADIAN, 
									m_Data[j].dec/Utils.DEGREES_PER_RADIAN , Utils.STANDARD_RADIUS);
							m_Data[j].x = mUtilStar.getXPrime();
							m_Data[j].y = mUtilStar.getYPrime();
							m_Data[j].z = mUtilStar.getZPrime();
						
							Log.d(TAG, "Star ra="+m_Data[j].ra+", dec="+m_Data[j].dec + ", x="+m_Data[j].x+", y=" + m_Data[j].y+", z="+m_Data[j].z);
						
							mag = 1.0f - m_Data[j].mag/4.0f;                                                                                            //4
					        
					        if(mag < .2f)
					            mag = .2f;
					        else if(mag > 1.0f)
					            mag=1.0f;

					        m_Data[j].r = m_Data[j].g = m_Data[j].b =mag;
					       
					        m_Data[j].a = 1.0f;
							flag = 0;
							j++;
						}	
					}					
				}			
			}			
	    }
	    
	    splitData(j);
    }
	
	private void splitData(int arraySize)
    {
        int i, j=0;
        float[] colorArray;
        float[] vertexArray;
        int colorSize = arraySize;
        int numColors = 4;
        int numvertices = 3;
        int vertexSize = numvertices*arraySize;
                
        colorArray = new float[colorSize*numColors];
        vertexArray = new float[vertexSize];
        
        for(i = 0; i < colorSize; i++)
        {   
           	j=i*numColors;
           	
        	colorArray[j + 0] = m_Data[i].r; 
        	colorArray[j + 1] = m_Data[i].g; 
        	colorArray[j + 2] = m_Data[i].b; 
        	colorArray[j + 3] = m_Data[i].a; 
        }
        
        j=0;
        
        for (i = 0; i < arraySize; i++) 
        {
        	j = i*numvertices;
        	vertexArray[j + 0] = m_Data[i].x;
        	vertexArray[j + 1] = m_Data[i].y;
        	vertexArray[j + 2] = m_Data[i].z;        	
        }
        
        m_ColorData = makeFloatBuffer(colorArray);
        m_VertexData = makeFloatBuffer(vertexArray);
    }

	public void execute(GL10 gl)
	{
	    float[] pointSize = new float[2];
	    GL11 gl11 = (GL11) gl;
	    gl.glDisable(GL10.GL_LIGHTING);                                                                                           
	    gl.glDisable(GL10.GL_TEXTURE_2D);
	    gl.glDisable(GL10.GL_DEPTH_TEST);
	    
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	    
	    gl.glMatrixMode(GL10.GL_MODELVIEW);
	    gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_DST_ALPHA);
	    gl.glEnable(GL10.GL_BLEND);
	    	    
	    gl.glColorPointer(4, GL10.GL_FLOAT, 0, m_ColorData);       
	    gl.glVertexPointer(3,GL10.GL_FLOAT, 0, m_VertexData);
	    	    
	    gl11.glGetFloatv(GL10.GL_SMOOTH_POINT_SIZE_RANGE, makeFloatBuffer(pointSize));                              
	    gl.glEnable(GL10.GL_POINT_SMOOTH);
	    gl.glPointSize(4.0f);  						//could be round or square, 1 pixel or several depending on the OpenGL drivers                           
	    
	    gl.glDrawArrays(GL10.GL_POINTS,0,totalElems/4);                                                      
	    
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);	
	    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);	
	    gl.glEnable(GL10.GL_DEPTH_TEST);
	    gl.glEnable(GL10.GL_LIGHTING);
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