uniform sampler2D m_Texture;
varying vec2 texCoord;

uniform vec2 m_Resolution;
uniform float m_Margin;
uniform float m_Softness;
uniform float m_Strength;
uniform float g_Time;
uniform bool m_Enabled;
uniform float m_StartTime;
uniform vec2 m_ClickPoint;
uniform float m_TpfInc;
uniform float m_Speed;
 uniform float m_Duration;
 //Ghosts
 uniform bool m_EnabledGhosts;
 uniform bool m_EnabledDistortion;
 uniform vec4 m_ColorGhosts;//vec3(1.0, 0.5, 0.25)
 
  //Bloom
 uniform bool m_EnabledFakeBloom;
 uniform bool m_BloomFadeOut;
 uniform float m_BloomStrength;//=0.5;
 uniform float m_BloomStrengthDynamic;//=0.1;
 uniform float m_BloomRange;// = 1.5  ;
 uniform vec4 m_ColorBloom;//vec3(1.0, 0.5, 0.25)
 
  //Anamorphic
 uniform bool m_EnabledAnamorphic;
 uniform float m_AnamStrength;//=0.5;
 uniform float m_AnamStrengthDynamic;//=0.1;
 uniform float m_AnamRange;// = 1.5  ;
  uniform vec4 m_ColorAnam;//vec3(1.0, 0.5, 0.25)
 //Streaks
  uniform  bool m_EnabledStreaks;
  uniform  int m_StreaksType;
  uniform  float m_StreaksCount ;
  uniform  vec4 m_ColorStreaks ;
  uniform  float m_StreaksLength ;
 uniform  float m_StreaksStrengthDynamic;
 //////////
 vec3 cc(vec3 color, float factor,float factor2) // color modifier
{
	float w = color.x+color.y+color.z;
	return mix(color,vec3(w)*factor,w*factor2);
}



vec3 classicGhosts(vec2 uv,vec2 pos)
{
       float intensity = 1.5;
	vec2 main = uv-pos;
	vec2 uvd = uv*(length(uv));
	 float dist=length(main); 
         dist = pow(dist,.1);
         
	 float f1 = max(0.01-pow(length(uv+1.2*pos),1.9),.0)*7.0;
        float f2 = max(1.0/(1.0+32.0*pow(length(uvd+0.8*pos),2.0)),.0)*00.1;
	float f22 = max(1.0/(1.0+32.0*pow(length(uvd+0.85*pos),2.0)),.0)*00.08;
	float f23 = max(1.0/(1.0+32.0*pow(length(uvd+0.9*pos),2.0)),.0)*00.06;
	 vec2 uvx = mix(uv,uvd,-0.5);
	 float f4 = max(0.01-pow(length(uvx+0.4*pos),2.4),.0)*6.0;
	float f42 = max(0.01-pow(length(uvx+0.45*pos),2.4),.0)*5.0;
	float f43 = max(0.01-pow(length(uvx+0.5*pos),2.4),.0)*3.0;
	
	uvx = mix(uv,uvd,-.4);
	
	float f5 = max(0.01-pow(length(uvx+0.2*pos),5.5),.0)*2.0;
	float f52 = max(0.01-pow(length(uvx+0.4*pos),5.5),.0)*2.0;
	float f53 = max(0.01-pow(length(uvx+0.6*pos),5.5),.0)*2.0;
	
	uvx = mix(uv,uvd,-0.5);
	
	float f6 = max(0.01-pow(length(uvx-0.3*pos),1.6),.0)*6.0;
	float f62 = max(0.01-pow(length(uvx-0.325*pos),1.6),.0)*3.0;
	float f63 = max(0.01-pow(length(uvx-0.35*pos),1.6),.0)*5.0;
	
	vec3 c = vec3(.0);
	
	c.r+=f2+f4+f5+f6; c.g+=f22+f42+f52+f62; c.b+=f23+f43+f53+f63;
	c = c*1.3 - vec3(length(uvd)*.05);
	
	return c * intensity;
}
 
vec4 classicLens()
   {
     
    //Lens
    vec2 uv = gl_FragCoord.xy / m_Resolution.xy- .5;
    vec2 position = ( gl_FragCoord.xy / m_Resolution.xy * 2.0 );
     uv.x *= m_Resolution.x/m_Resolution.y; //fix aspect ratio
    vec3 color2 = m_ColorGhosts.rgb*classicGhosts(uv,vec2(m_ClickPoint.x*2.-1.0 ,m_ClickPoint.y-0.5));
    color2 = cc(color2,.5,.1);
    vec4 lens = vec4(color2,(color2.r+color2.g+color2.b)/3.0);
        
     return lens;
  }
    
 float sdCapsule(vec3 p, vec3 a, vec3 b, float r)
{
    vec3 ab = b - a;
    float t = dot(p - a, ab) / dot(ab, ab);
    t = clamp(t, 0.5, 0.5);
    return length((a + t * ab) - p) - r;
}
 

vec3 anamFlare(vec2 spos, vec2 fpos, vec3 clr)
{
	vec3 color;
	float d = distance(spos, fpos)*0.5;
	vec2 dd;
	dd.x = spos.x - fpos.x;
	dd.y = spos.y - fpos.y;
	dd = abs(dd);
	 
	 float d2  = sdCapsule(vec3(spos.xy,0.0),vec3(fpos,0.0), vec3(0.,0.,0.0),0.1);
	 
	 color = clr * max(0.0, (0.015*m_AnamStrength) / dd.y) * max(0.0,  (m_AnamRange*m_AnamStrengthDynamic)/3. -  dd.x);
	// color += clr * max(0.0, 0.4 - d);
	 //color += clr * max(0.0, 0.05 / d);
	 
	 
	return color;
}
#define dista 0.02
 #define chromaShift 1.0
vec3 tex2D(vec2 uv){
	if (uv.x == 0.0 || uv.y == 0.0 || uv.x == 1.0 || uv.y == 1.0) 
        return vec3(0.0);
       vec2 pos= vec2( m_ClickPoint.x , m_ClickPoint.y   );
       float d = distance(uv, pos) ;
	if (d < dista) 
            return vec3(((dista-d)/dista),((dista-d)/dista),((dista-d)/dista))*m_ColorGhosts;
	return vec3(0.0);
}
vec3 flare(float px, float py, float pz, float cShift, float i)
{
	vec3 t=vec3(0.);
	
	 
	vec2 uv=gl_FragCoord.xy / m_Resolution.xy-.5;
	float x = length(uv);
	uv*=exp2(log2(4.0*x)*py)*px+pz;
	t.r = tex2D(clamp(uv*(1.0+cShift*chromaShift)+0.5, 0.0, 1.0)).r;
	t.g = tex2D(clamp(uv+0.5, 0.0, 1.0)).g;
	t.b = tex2D(clamp(uv*(1.0-cShift*chromaShift)+0.5, 0.0, 1.0)).b;
	t = t*t;
	t *= clamp(.6-length(uv), 0.0, 1.0);
	t *= clamp(length(uv*20.0), 0.0, 1.0);
	t *= i;
	 
	return t;
}
    
vec3 lensDistortion()
    {
    
     vec3 finalColor =vec3(0.0);
    finalColor += flare(0.00005, 16.0, 0.0, 0.2, 1.0);
    finalColor += flare(0.5, 2.0, 0.0, 0.1, 1.0);
    finalColor += flare(20.0, 1.0, 0.0, 0.05, 1.0);
    finalColor += flare(-10.0, 1.0, 0.0, 0.1, 1.0);
    finalColor += flare(-10.0, 2.0, 0.0, 0.05, 2.0);
    finalColor += flare(-1.0, 1.0, 0.0, 0.1, 2.0);
    finalColor += flare(-0.00005, 16.0, 0.0, 0.2, 2.0);
     return finalColor;
    
    }    
  
float rand(int seed, float ray) 
{
	return mod(sin(float(seed)*363.5346+ray*674.2454)*6743.4365, 1.0);
}

vec3 rayStreaks(  )
{	
    
        vec3 col;
	float pi = 3.14159265359;
	float t = abs(sin(g_Time));
	vec2 position = ( (gl_FragCoord.xy) / m_Resolution.xy ) - m_ClickPoint ;
	position.y *= m_Resolution.y/m_Resolution.x;
	float ang = atan(position.x, position.y);
	float dist = length(position);
	col.rgb  = vec3(m_ColorStreaks.r  , m_ColorStreaks.g  , m_ColorStreaks.b  ) * (pow(dist, -0.8) * 0.009);
        if(m_StreaksType==0)
	for (float ray = 0.5; ray < m_StreaksCount; ray += 1.)
            {
		//float rayang = rand(5234, ray)*6.2+time*5.0*(rand(2534, ray)-rand(3545, ray));
		float rayang = ray * 1.2;
		rayang = mod(rayang, pi*2.0);
		if (rayang < ang - pi) {rayang += pi*2.0;} //needed to fix atan(x,y) 
		if (rayang > ang + pi) {rayang -= pi*2.0;}
		float brite = .05 - abs(ang - rayang);
		brite -= dist * (1.-(m_StreaksLength* clamp(m_StreaksStrengthDynamic,0.0,0.3)));
		if (brite > 0.0) {
			col.rgb += vec3(0.2+m_ColorStreaks.r *ray, 0.4+m_ColorStreaks.g*ray, 0.5+m_ColorStreaks.g*ray)   * brite;
		}
	    }
         else if(m_StreaksType==1)   
	 for (float ray = 0.5; ray < m_StreaksCount; ray += 0.097) 
             {
		float rayang = rand(5, ray)*6.2+(g_Time*0.02)*20.0*(rand(2546, ray)-rand(5785, ray))-(rand(3545, ray)-rand(5467, ray));
		rayang = mod(rayang, pi*2.0);
		if (rayang < ang - pi) {rayang += pi*2.0;}
		if (rayang > ang + pi) {rayang -= pi*2.0;}
		float brite = 0.3 - abs(ang - rayang);
		brite -= dist * (1.-(m_StreaksLength* clamp(m_StreaksStrengthDynamic,0.0,0.3)));
		
		if (brite > 0.0) 
		{
		 	col += vec3(0.1+m_ColorStreaks.r*rand(8644, ray), 0.55+m_ColorStreaks.g*rand(4567, ray), 0.7+m_ColorStreaks.b*rand(7354, ray)) * brite * 0.025;
		}
             }
	return col.rgb;
}

  
void main() {
  
    vec2 uv = texCoord;
   vec2 center =m_ClickPoint;
   // 
   
   if( m_Enabled)
    {
        
          //BG
          vec4 c =  texture2D(m_Texture, uv ); 
          gl_FragColor = c; 
         // Fake bloom
          if( m_EnabledFakeBloom)
            {
             float widthHeightRatio = m_Resolution.x/m_Resolution.y;
            vec2 pos = m_ClickPoint - uv;
            pos.y /= widthHeightRatio;
            //
            float dist = 1./length(pos);
            dist = dist * m_BloomStrength * m_BloomStrengthDynamic*0.1 ;
            dist = pow(dist,3.0/(m_BloomRange* m_BloomStrengthDynamic));
            if(m_BloomFadeOut)
               dist*=max(0.,1.-((g_Time - m_StartTime)/m_Duration));
            vec3 col = dist * m_ColorBloom.rgb;// vec3(1.0, 0.5, 0.25);//
            col = 1.0 - exp( -col );
             // Output to screen
            gl_FragColor += vec4(col, 1.0); 
             }
             
         //Anamorphic    
         if( m_EnabledAnamorphic)
            {  
             vec2 position = ( gl_FragCoord.xy / m_Resolution.xy * 2.0 );
             position.y *=  0.5;
             vec3 anam = anamFlare(position, vec2(m_ClickPoint.x*2.,m_ClickPoint.y), m_ColorAnam.rgb);
             gl_FragColor +=  vec4(anam,1.);   
             } 
              
       if(m_EnabledStreaks)       
          {
                gl_FragColor +=  vec4(rayStreaks(),1.);
           }
       if( m_EnabledGhosts)
            {
                vec4 lens = classicLens();
                gl_FragColor +=  lens;
                
               if(m_EnabledDistortion)
                  {
                    vec3 distortion =  lensDistortion();
                    gl_FragColor +=  vec4(distortion,1.);
                   }
                   
                 
           }
       
    }
else
   {
   
    vec4 c =  texture2D(m_Texture, texCoord); 
    gl_FragColor = c;
   }


}