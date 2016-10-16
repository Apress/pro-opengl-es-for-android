package book.SolarSystem;

import book.SolarSystem.Planet;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;



class SolarSystemRendererES2 implements GLSurfaceView.Renderer 
{
    private float[] m_MVMatrix = new float[16];							//modelview matrix
    private float[] m_MVPMatrix = new float[16];						//modelview/projection matrix
    private float[] m_ProjMatrix = new float[16];						//projection matrix
    private float[] m_MMatrix = new float[16];							//model matrix
    private float[] m_WorldMatrix = new float[16];						//world matrix
    private float[] m_NormalMatrix=new float[9];						//normal matrix
    private float[] m_EyePosition=new float[3];							//eye position		
    private float[] m_LightPosition=new float[3];						//light position
    private int[] m_DayUniformIDs=new int[10];							//cache the uniforms
    private int[] m_NightUniformIDs=new int[10];						
    private int m_DaysideProgram;
    private int m_NightsideProgram;
    private int m_DayTexture;
    private int m_NightTexture;	
    private int m_CloudTexture;

    private Context m_Context;
    private static String TAG = "OpenGL ES2-Earth";

	private Planet m_Earth;
	private float m_Angle;
	
	public final static int X_VALUE = 0;
	public final static int Y_VALUE = 1;
	public final static int Z_VALUE = 2;
	
    //uniform IDs

    private final static int UNIFORM_MVP_MATRIX = 0;
    private final static int UNIFORM_NORMAL_MATRIX = 1;
    private final static int UNIFORM_LIGHT_POSITION = 2;
    private final static int UNIFORM_EYE_POSITION = 3;
    private final static int UNIFORM_SAMPLER0 = 4;
    private final static int UNIFORM_SAMPLER1 = 5;

    //attribute IDs
    
    private final static int ATTRIB_VERTEX =	1;
    private final static int ATTRIB_NORMAL =	2;
    private final static int ATTRIB_TEXTURE0_COORDS =	3;
    
	public Context myAppcontext;

    public SolarSystemRendererES2(Context context) 
    {
        m_Context = context;
		myAppcontext = context;
		
		m_Angle=0.0f;
		
		m_LightPosition[0]=10.0f;
		m_LightPosition[1]=2.0f;
		m_LightPosition[2]=10.0f;
		
		m_EyePosition[0]=10.0f;
		m_EyePosition[1]=0.0f;
		m_EyePosition[2]=10.0f;
    }

	private void initGeometry(GL10 glUnused) 
	{		
		m_DayTexture=createTexture(glUnused,m_Context, book.SolarSystem.R.drawable.earth_light);
		m_NightTexture=createTexture(glUnused,m_Context, book.SolarSystem.R.drawable.earth_night);
		m_CloudTexture=createTexture(glUnused,m_Context, book.SolarSystem.R.drawable.clouds);

		m_Earth = new Planet(50, 50, 1.0f, 1.0f, glUnused, myAppcontext,true,-1); 
	}
	
    public void onDrawFrame(GL10 glUnused) 
    {
        // Ignore the passed-in GL10 interface, and use the GLES20
        // class's static methods instead.
    	
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
      
        m_Angle+=0.20;
        
        Matrix.setRotateM(m_MMatrix, 0,m_Angle, 0, 1.0f, 0.0f);
        Matrix.multiplyMM(m_MVMatrix, 0, m_WorldMatrix, 0, m_MMatrix, 0);
        Matrix.multiplyMM(m_MVPMatrix, 0, m_ProjMatrix, 0, m_MVMatrix, 0);

        //this is a bit of a cheat since it doesn't work for the general case, but
        //only when the MV matrix has no scaling or uniform scaling.
       
    	m_NormalMatrix[0]=m_MVMatrix[0];
    	m_NormalMatrix[1]=m_MVMatrix[1];
    	m_NormalMatrix[2]=m_MVMatrix[2];
    	m_NormalMatrix[3]=m_MVMatrix[4];
    	m_NormalMatrix[4]=m_MVMatrix[5];
    	m_NormalMatrix[5]=m_MVMatrix[6];
    	m_NormalMatrix[6]=m_MVMatrix[8];
    	m_NormalMatrix[7]=m_MVMatrix[9];
    	m_NormalMatrix[8]=m_MVMatrix[10];
      	
    	//nightside 
  	
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,m_NightTexture); 
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,m_CloudTexture); 
        
        GLES20.glUseProgram(m_NightsideProgram);
        checkGlError("glUseProgram:nightside");
        
        GLES20.glUniformMatrix4fv(m_NightUniformIDs[UNIFORM_MVP_MATRIX], 1, false, m_MVPMatrix, 0);
        GLES20.glUniformMatrix3fv(m_NightUniformIDs[UNIFORM_NORMAL_MATRIX], 1, false, m_NormalMatrix,0);
        GLES20.glUniform3fv(m_NightUniformIDs[UNIFORM_LIGHT_POSITION], 1, m_LightPosition,0);

        m_Earth.setBlendMode(m_Earth.PLANET_BLEND_MODE_SOLID);
        m_Earth.draw(glUnused,ATTRIB_VERTEX,ATTRIB_NORMAL,-1,ATTRIB_TEXTURE0_COORDS,m_NightTexture);
        checkGlError("glDrawArrays");
       
        //dayside
        
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,m_DayTexture); 
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,m_CloudTexture); 
        
        GLES20.glUseProgram(m_DaysideProgram);
        checkGlError("glUseProgram:dayside");
        
        GLES20.glUniformMatrix4fv(m_DayUniformIDs[UNIFORM_MVP_MATRIX], 1, false, m_MVPMatrix, 0);
        GLES20.glUniformMatrix3fv(m_DayUniformIDs[UNIFORM_NORMAL_MATRIX], 1, false, m_NormalMatrix,0);
        GLES20.glUniform3fv(m_DayUniformIDs[UNIFORM_LIGHT_POSITION], 1, m_LightPosition,0);
        checkGlError("LightPosition");
        GLES20.glUniform3fv(m_DayUniformIDs[UNIFORM_EYE_POSITION], 1, m_EyePosition,0);
 //       checkGlError("EyePosition");

        m_Earth.setBlendMode(m_Earth.PLANET_BLEND_MODE_FADE);
        m_Earth.draw(glUnused,ATTRIB_VERTEX,ATTRIB_NORMAL,-1,ATTRIB_TEXTURE0_COORDS,m_DayTexture);
        checkGlError("glDrawArrays");
    }
   
    public void onSurfaceChanged(GL10 glUnused, int width, int height) 
    {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(m_ProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
    {
    	m_NightsideProgram=createProgram(m_NightSideVertexShader,m_NightSideFragmentShader,m_NightUniformIDs);
    	m_DaysideProgram=createProgram(m_DaySideVertexShader,m_DaySideFragmentShader,m_DayUniformIDs);

		initGeometry(glUnused);

	    //bind the clouds to texture unit 1
	    
		GLES20.glUseProgram(m_DaysideProgram);
		GLES20.glUniform1i(m_DayUniformIDs[UNIFORM_SAMPLER0],0);
		GLES20.glUniform1i(m_DayUniformIDs[UNIFORM_SAMPLER1],1);
	 
		GLES20.glUseProgram(m_NightsideProgram);
		GLES20.glUniform1i(m_NightUniformIDs[UNIFORM_SAMPLER0],0);
		GLES20.glUniform1i(m_NightUniformIDs[UNIFORM_SAMPLER1],1);
	    
        Matrix.setLookAtM(m_WorldMatrix, 0, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
    }

    private int createProgram(String vertexSource, String fragmentSource,int[] uniformIDs) 
    {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        
        if (vertexShader == 0) 
        {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        
        if (pixelShader == 0) 
        {
            return 0;
        }

        int program = GLES20.glCreateProgram();
        
        if (program != 0) 
        {           
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            
        	//this must be done before linking
        	
        	GLES20.glBindAttribLocation(program, ATTRIB_VERTEX, "aPosition");
            GLES20.glBindAttribLocation(program, ATTRIB_NORMAL, "aNormal");
            GLES20.glBindAttribLocation(program, ATTRIB_TEXTURE0_COORDS, "aTextureCoord");
            
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            
            if (linkStatus[0] != GLES20.GL_TRUE) 
            {
                Log.e(TAG, "Could not link program: ");
                Log.e(TAG, GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
                      
            uniformIDs[UNIFORM_MVP_MATRIX]=GLES20.glGetUniformLocation(program, "uMVPMatrix");
            uniformIDs[UNIFORM_NORMAL_MATRIX]=GLES20.glGetUniformLocation(program, "uNormalMatrix");
            uniformIDs[UNIFORM_EYE_POSITION] = GLES20.glGetUniformLocation(program, "uEyePosition");
            uniformIDs[UNIFORM_LIGHT_POSITION]=GLES20.glGetUniformLocation(program, "uLightPosition");
            uniformIDs[UNIFORM_SAMPLER0] = GLES20.glGetUniformLocation(program, "sTexture");
            uniformIDs[UNIFORM_SAMPLER1] = GLES20.glGetUniformLocation(program, "sCloudTexture");
        }
        return program;
    }

    public int createTexture(GL10 gl, Context contextRegf, int resource) 
    {
    	int[] textures = new int[1];
    	
    	Bitmap tempImage = BitmapFactory.decodeResource(contextRegf.getResources(), resource); 

    	gl.glGenTextures(1, textures, 0); 
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, tempImage, 0); 
    		
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); 
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); 	

    	tempImage.recycle();

    	gl.glBindTexture(GL10.GL_TEXTURE_2D,0);
    	
    	return textures[0];
    }
    
    private int loadShader(int shaderType, String source) 
    {
        int shader = GLES20.glCreateShader(shaderType);
        
        if (shader != 0) 
        {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            
            if (compiled[0] == 0) 
            {
                Log.e(TAG, "Could not compile shader " + shaderType + ":");
                Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
    
    private void checkGlError(String op) 
    {
        int error;
        
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) 
        {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private final String m_DaySideVertexShader =
            "attribute vec4 aPosition;\n" +
            "attribute vec3 aNormal;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec2 vTextureCoord;\n" +
            "varying lowp vec4 colorVarying;\n" +
           	"varying lowp vec4 specularColorVarying;\n" +
            "uniform vec3 uLightPosition;\n" +
            "uniform vec3 uEyePosition;\n" +
            "uniform mat4 uMVPMatrix;\n" +
            "uniform mat3 uNormalMatrix;\n" +
            "void main() {\n" +
            "  float shininess=25.0;\n" +
            "  float balance=.75;\n" +
            "  float specular=0.0;\n" +
            "  vTextureCoord = aTextureCoord;\n" +
            "  vec3 normalDirection = normalize(uNormalMatrix * aNormal);\n"+
            "  vec3 lightDirection = normalize(uLightPosition);\n" +
            "  vec3 eyeNormal = normalize(uEyePosition);\n" +
            "  vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0);\n"+
            "  float nDotVP = max(0.0, dot(normalDirection, lightDirection));\n"+
            "  float nDotVPReflection = dot(reflect(-lightDirection,normalDirection),eyeNormal);\n" +
			"  specular = pow(max(0.0,nDotVPReflection),shininess)*balance;\n" +
			"  specularColorVarying=vec4(specular,specular,specular,0.0);\n" +   
            "  colorVarying = diffuseColor * nDotVP*1.3;\n"+
            "  gl_Position = uMVPMatrix * aPosition;\n" +
            "}\n";

     private final String m_DaySideFragmentShader =
        	"varying lowp vec4 colorVarying;\n" +
           	"varying lowp vec4 specularColorVarying;\n" +
            "precision mediump float;\n"    +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D sTexture;\n" + 
            "uniform sampler2D sCloudTexture;\n" +
            "void main() {\n" +
            "  vec4 finalSpecular=vec4(0,0,0,1);\n" +

            "  vec4 cloudColor;\n" +
            "  vec4 surfaceColor;\n" +
            "  float halfBlue;\n" +
            
            "  cloudColor=texture2D(sCloudTexture, vTextureCoord );\n" +
            "  surfaceColor=texture2D(sTexture, vTextureCoord );\n" +
            
            "  halfBlue=0.5*surfaceColor[2];\n" +
            
			"  if(halfBlue>1.0)\n" +
			"     halfBlue=1.0;\n" +
			
			"  if((surfaceColor[0]<halfBlue) && (surfaceColor[1]<halfBlue))\n" +
			"      finalSpecular=specularColorVarying;\n" +
			
            "  if(cloudColor[0]>0.2) {\n;" +
            "     cloudColor[3]=1.0;\n" + 
            "     gl_FragColor=(cloudColor*1.3+surfaceColor*.4)*colorVarying;\n" +
            "  }\n" +
            "  else\n" +
            "	  gl_FragColor=(surfaceColor+finalSpecular)*colorVarying;\n" +
            "}\n";  
     
     //same as "solid" shaders in the iOS example. Handles the dark side of the planet
     
     private final String m_NightSideVertexShader =
             "attribute vec4 aPosition;\n" +
             "attribute vec3 aNormal;\n" +
             "attribute vec2 aTextureCoord;\n" +
             "varying vec2 vTextureCoord;\n" +
             "varying lowp vec4 colorVarying;\n" +
             "uniform vec3 uLightPosition;\n" +
             "uniform mat4 uMVPMatrix;\n" +
             "uniform mat3 uNormalMatrix;\n" +
             "void main() {\n" +
             "  vTextureCoord = aTextureCoord;\n" +
             "  vec3 normalDirection = normalize(uNormalMatrix * aNormal);\n"+
             "  vec4 diffuseColor = vec4(1.0, 1.0, 1.0, 1.0);\n"+
             "  float nDotVP = max(0.0, dot(normalDirection, normalize(uLightPosition)));\n"+
             "  colorVarying = diffuseColor * nDotVP;"+
             "  gl_Position = uMVPMatrix * aPosition;\n" +
             "}\n";

         private final String m_NightSideFragmentShader =
         	 "varying lowp vec4 colorVarying;\n" +
             "precision mediump float;\n"    +
             "varying vec2 vTextureCoord;\n" +
             "uniform sampler2D sTexture;\n" + 
             "uniform sampler2D sCloudTexture;\n" +
             "void main() {\n" +
             "  vec4 newColor;\n" +
             "  vec4 cloudColor;\n" +
             "  vec4 surfaceColor;\n" +
             "  float cloudNightBrightness=.1;\n" +
             "  newColor=1.0-colorVarying;\n" +
             "  cloudColor=texture2D(sCloudTexture, vTextureCoord );\n" +
             "  surfaceColor=texture2D(sTexture, vTextureCoord );\n" +
             "  if(cloudColor[0]>0.4) {\n;" +
             "     cloudColor[3]=1.0;\n" + 
             "     gl_FragColor=cloudNightBrightness*cloudColor+0.6*surfaceColor*newColor;\n" +
             "  }\n" +
             "  else\n" +
             "       gl_FragColor = surfaceColor*newColor;\n" +
             "}\n";

}
