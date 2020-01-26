package org.betterlensflare.blf;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
 import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.io.IOException;
/**
 *
 * @author xxx
 */
public class BetterLensFlare  extends BaseAppState 
{
    SimpleApplication app;
    FilterPostProcessor fpp ;
    
    BLFFilter bLFFilter;
    Spatial target; 
  
    private boolean sourceVisible=false;
    //
    private boolean active=false; 
    private Node parentNode;
  
    private float bloomFadeDurationInSecs=1.0f;
    private float bloomTimeOfWork=0;
    private float bloomStrength=1.0f;
    private float bloomStrengthDynamic=1.0f;
    private float bloomRange=1.5f;
    private float bloomNominalDistance=1;
    private float bloomStepDistance=0.1f;
    String targetName="";
    
    private float anamStrength=1.0f;
    private float anamRange=1.5f;
    private float anamNominalDistance=1;
    private float anamStepDistance=0.1f ;
    private float anamStrengthDynamic=0;
    
    private boolean fakeBloomEnabled=false;
    private boolean anamorphicEnabled=false;
    private boolean ghostsEnabled=false;
    private boolean streaksEnabled=false;
    private int frameCounter=0;
    private int checkFreq=5;
      
    private int streaksType=STREAK_TYPE_BASIC;
    private float  streaksCount=5;
    private float streaksLength=0.5f;
    private float streaksStrengthDynamic=0.1f ;
    private float streaksNominalDistance=0;
    private float streaksStepDistance=0;
    
    private ColorRGBA colorBloom= new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
    private ColorRGBA colorAnam= new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
    private ColorRGBA colorGhosts= new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
    private ColorRGBA colorStreaks= new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
    static public int STREAK_TYPE_BASIC=0;
    static public int STREAK_TYPE_ADVANCED=1;
    /**
     *Main contructor. 
     * @param app
     * @param fpp. Add filter on startLensFlare. Removes on stopLensFlare
     * @param parentNode - Used for raycasting. Node containing the target object and all models that may occlude it.  
     */
    public BetterLensFlare(SimpleApplication app, FilterPostProcessor fpp, Node parentNode )
        {
            this.app=app;
            this.fpp=fpp;
            this.parentNode=parentNode;
            bLFFilter=new BLFFilter(app);
        
          }
  
    /**
     * Start the fake bloom effect. 
     * @param colorBloom
     * @param bloomStrength. From 0 to 5.
     * @param bloomRange. From 0 to 15.
     * @param bloomNominalDistance. In most case the initial distance from cam to the target. If decreased by 50% doubles the strength. If doubled decreases the strength by 50%. 
     * @param bloomStepDistance. Value at which strength is changed depending on bloomNominalDistance. From 0 to 5.
    * @param bloomFadeDurationInSecs - time in secs to fade away after occlusion. From 0  to 5. 
     */
    public void enableBloom(ColorRGBA colorBloom, float bloomStrength, float bloomRange,float bloomNominalDistance, float bloomStepDistance,  float  bloomFadeDurationInSecs )
  {
   this.colorBloom=colorBloom;   
   this.bloomStrength=bloomStrength;
   this.bloomRange=bloomRange;
   this.bloomFadeDurationInSecs=bloomFadeDurationInSecs;
   this.bloomNominalDistance=bloomNominalDistance;
   this.bloomStepDistance=bloomStepDistance;
   
    bLFFilter.setEnabledFakeBloom(true);
    bLFFilter.setBloomStrength(bloomStrength);
    bLFFilter.setBloomRange(bloomRange);
    bLFFilter.setDuration(bloomFadeDurationInSecs);
    bLFFilter.setColorBloom(colorBloom);
    
    fakeBloomEnabled=true;
  }
    /**
   * Stops fake bloom effect
   */
   public void disableBloom( )
   {
    bLFFilter.setEnabledFakeBloom(false);
      fakeBloomEnabled=false;
    }
   
    /**
     * Start anamorphic lens
     * @param colorAnam
     * @param anamStrength
     * @param anamRange - width. From 0 to 15
     * @param anamNominalDistance. In most case the initial distance from cam to the target. If decreased by 50% doubles the strength. If doubled decreases the strength by 50%. 
     * @param anamStepDistance. Value at which strength is changed depending on anamNominalDistance. From 0 to 5.
     */
    public void enableAnamorphic( ColorRGBA colorAnam,float anamStrength, float anamRange,  float anamNominalDistance, float anamStepDistance )
    {
     this.colorAnam=colorAnam;
     this.anamStrength=anamStrength;
     this.anamRange=anamRange;
     this.anamNominalDistance=anamNominalDistance;
     this.anamStepDistance=anamStepDistance;
        
      bLFFilter.setAnamStrength(anamStrength);
      bLFFilter.setAnamRange(anamRange);
      bLFFilter.setEnabledAnamorphic(true);
      bLFFilter.setColorAnam(colorAnam);
      anamorphicEnabled=true;
    }

    /**
     * Stops anamorphic effect
     */
    public void disableAnamorphic( )
    {
    bLFFilter.setEnabledAnamorphic(false);
    anamorphicEnabled=false;
   }

    /**
     * Starts ghosts effect.
     * @param colorGhosts
     * @param lensDistortion - if to add additional lens distortion effect
     */
    public void enableGhosts(ColorRGBA colorGhosts, boolean lensDistortion)
    {
       this.colorGhosts=colorGhosts;
       bLFFilter.setColorGhosts(colorGhosts);
       bLFFilter.setEnabledGhosts(true);
       if(lensDistortion)
         bLFFilter.setEnabledDistortion(lensDistortion);
       ghostsEnabled=true;
    }

    /**
     * Stops ghosts effect
     */
    public void disableGhosts( )
    {
    bLFFilter.setEnabledGhosts(false);
    ghostsEnabled=false;
   }

    /**
     * Starts streaks effect - sun rays
     * @param colorStreaks
     * @param streaksType - REGULAR OR ADVANCED. Use predefined BetterLensFlare.STREAK_TYPE_BASIC or BetterLensFlare.STREAK_TYPE_ADVANCED
     * @param streaksCount - count of the rays. From 3 to 50. Usually 5-15. Heavy with ADVANCED type.
     * @param streaksLength - length of the rays. From 0 to 5. 
     * @param streaksNominalDistance. In most case the initial distance from cam to the target. If decreased by 50% doubles the strength. If doubled decreases the strength by 50%. 
     * @param streaksStepDistance. Value at which strength is changed depending on streaksNominalDistance. From 0 to 5.
   
     */
    public void enableStreaks(ColorRGBA colorStreaks,int streaksType,int  streaksCount,float streaksLength ,   float streaksNominalDistance, float streaksStepDistance)
    {
       this.colorStreaks=colorStreaks;
       this.streaksNominalDistance=streaksNominalDistance;
       this.streaksStepDistance=streaksStepDistance;
       this.streaksType=streaksType;
       this.streaksCount=streaksCount;
       this.streaksLength=streaksLength;

       bLFFilter.setColorStreaks(colorStreaks);
       bLFFilter.setStreaksType(streaksType);
       bLFFilter.setStreaksCount(streaksCount);
       bLFFilter.setStreaksLength(streaksLength);
       bLFFilter.setEnabledStreaks(true);
        
       streaksEnabled=true;
    }

    /**
     *Stops streaks.
     */
    public void disableStreaks( )
    {
    bLFFilter.setEnabledStreaks(false);
    streaksEnabled=false;
   }
      
  private void calcStrengthDynamic()
  {
    float distance=getDistance3D(app.getCamera().getLocation(),target.getLocalTranslation() ); 
    bloomStrengthDynamic= (bloomNominalDistance/distance);//*bloomStepDistance
    anamStrengthDynamic= (anamNominalDistance/distance);//*bloomStepDistance
    streaksStrengthDynamic= (streaksNominalDistance/distance);//*bloomStepDistance
     
     if(fakeBloomEnabled)
        bLFFilter.setBloomStrengthDynamic(bloomStrengthDynamic);
     if(anamorphicEnabled)
        bLFFilter.setAnamStrengthDynamic(anamStrengthDynamic);
     if(streaksEnabled)
        bLFFilter.setStreaksStrengthDynamic(streaksStrengthDynamic);
   
  }

    /**
     * Calculates distance in 3d space between two vectors
     * @param originPoint
     * @param targetPoint
     * @return
     */
    public float getDistance3D(Vector3f originPoint,Vector3f targetPoint )
	{
	     float v0 = originPoint.x - targetPoint.x;
           float v1 = originPoint.y - targetPoint.y;
           float v2 = originPoint.z - targetPoint.z;
           return (float)Math.sqrt(v0*v0 + v1*v1 + v2*v2);
	}     
  
/**
 * Main method to start the lens effect. 
 * @param target - model added to parent node set in the contructor. 
 * @param checkFreq - how often to check occlusion. Higher values==faster but less accurate occlusion testing.  1 means on every frame, 2 on every second frame etc. The highest fps the highest the number may be. With 60 fps 5-10 seems ok. 
 */
 public void startLensFlare(Spatial target,   int checkFreq )
  {
     
    this.target=target;
    this.targetName=target.getName();
    this.checkFreq=Math.abs(checkFreq);
    //
    fpp.addFilter(bLFFilter);
    bLFFilter.setEnabled(true);       
    //
    bLFFilter.setEnabledEffect(true);
    bloomTimeOfWork=0;
    //
    active=true;
  }
  void bloomFadeOut()
  {
    
    bLFFilter.setDuration(bloomFadeDurationInSecs);
    bLFFilter.startStartTime();
    bLFFilter.setBloomFadeOut(true);
   
  }
  
    /**
     * Main method to stop the effect
     */
    public void stopLensFlare( )
  {
       active=false;
       bLFFilter.setEnabled(false);
       bLFFilter.setEnabledEffect(false);
        bloomTimeOfWork=0;
      fpp.removeFilter(bLFFilter);
  }

    
private double easeOut(double tNorm)
{
      return tNorm*(2-tNorm);
}
 
 private boolean testTargetVisibility()
 {
    
     if(isInVieportByBoundingVolume(target) && isTargetVisible() )
        {
            bLFFilter.setEnabledEffect(true);
           if(fakeBloomEnabled)
             {
             bLFFilter.setEnabledFakeBloom(true);
             bLFFilter.setBloomFadeOut(false);
             }
          if(anamorphicEnabled)   
             bLFFilter.setEnabledAnamorphic(true);
          if(ghostsEnabled)
             bLFFilter.setEnabledGhosts(true);
          if(streaksEnabled)
             bLFFilter.setEnabledStreaks(true);
           return true;
        }
     else
        {
            if(anamorphicEnabled)      
              bLFFilter.setEnabledAnamorphic(false);
           
            if(ghostsEnabled)
              bLFFilter.setEnabledGhosts(false);
           
            if(streaksEnabled)
             bLFFilter.setEnabledStreaks(false);
          
              return false;
        }
 }
  Vector3f dir=new Vector3f();
 private boolean isTargetVisible()
    {
         target.getWorldTranslation() .subtract(app.getCamera().getLocation(), dir);
         CollisionResults results = new CollisionResults();
          Ray ray = new Ray(app.getCamera().getLocation(),dir);
            parentNode.collideWith(ray, results);
          if (results.size() > 0) 
             {
               CollisionResult closest = results.getClosestCollision();
               // System.out.println("COLLIDE IWTH "+closest.getGeometry().getName());
                if(closest.getGeometry().getName().equals(targetName))
                   return   true;
               else   
                return false;
              }
         else
            return true;
         
   }

private boolean isInVieportByBoundingVolume(Spatial object)
 {
      BoundingVolume bv = object.getWorldBound();
    int planeState = app.getCamera().getPlaneState();
     app.getCamera().setPlaneState(0);
    Camera.FrustumIntersect result = app.getCamera().contains(bv);
    app.getCamera().setPlaneState(planeState);
    boolean culled=false;
    if(result == Camera.FrustumIntersect.Inside || result == Camera.FrustumIntersect.Intersects)
       culled=true;
            
     return culled;
 }

 
  public void update(float tpf)
        {
          
         if(active)
           {
             
               //limit raycasting
               frameCounter++;
               if(frameCounter%checkFreq==0)
                {
                    if(testTargetVisibility())
                      {
                       //if visible again restart fadeout
                      if(!sourceVisible)
                          bloomTimeOfWork=0;
                       //
                       sourceVisible=true;
                       }
                   else
                    {
                        if(sourceVisible)
                          {
                            bloomFadeOut();
                            sourceVisible=false;
                          }
                        else
                           {
                              if(fakeBloomEnabled)
                                {
                                 bloomTimeOfWork=bloomTimeOfWork+tpf;
                                 if(bLFFilter.isBloomFadeOut() && bloomTimeOfWork>bloomFadeDurationInSecs)
                                   {
                                    bLFFilter.setEnabledFakeBloom(false);
                                    bLFFilter.setBloomFadeOut(false);
                                    bLFFilter.setEnabledEffect(false);
                                     }
                                }

                          }

                    }
                }
               //
               updateTargetPos(); 
                
            }
         
        }
  
  Vector2f targetPoint=new Vector2f();
  private void updateTargetPos()
   {
   //update 
    Vector3f posOnScreen=app.getCamera().getScreenCoordinates( target.getWorldTranslation()  );
    targetPoint.set(posOnScreen.x/app.getCamera().getWidth(), posOnScreen.y/app.getCamera().getHeight());
    bLFFilter.setClickPoint(targetPoint );
    //Bloom Strength
    calcStrengthDynamic();
   }
   
    @Override
    protected void initialize(Application app) {
     }

    @Override
    protected void cleanup(Application app) {
     }

    @Override
    protected void onEnable() {
     }

    @Override
    protected void onDisable() {
      }
    
    /////////////////////////////////////////////////FILTER/////////////////////////////
 class BLFFilter extends Filter {
 
 
private float DEFAULT_DURATION=1.0f;
private float DEFAULT_BLOOM_STRENGTH=0.5f;
private float DEFAULT_BLOOM_STRENGTH_DYNAMIC=1f;
private float DEFAULT_BLOOM_RANGE=1.5f;

private float DEFAULT_ANAM_STRENGTH=0.5f;
private float DEFAULT_ANAM_STRENGTH_DYNAMIC=1f;
private float DEFAULT_ANAM_RANGE=1.5f;

private float DEFAULT_STREAKS_STRENGTH_DYNAMIC=0;

private Material material;
 
private float duration = DEFAULT_DURATION;

  
private boolean enabled = false;
private boolean bloomFadeOut = false;
private boolean enabledFakeBloom = false;
private boolean enabledGhosts = false;
private boolean enabledDistortion = false;
private boolean enabledAnamorphic = false;
private float startTime=0;

private float bloomStrength=DEFAULT_BLOOM_STRENGTH;//=0.5;
private float bloomStrengthDynamic=DEFAULT_BLOOM_STRENGTH_DYNAMIC;//=0.1;
private float bloomRange=DEFAULT_BLOOM_RANGE;// = 1.5  ;


private float anamStrength=DEFAULT_ANAM_STRENGTH;//=0.5;
private float anamStrengthDynamic=DEFAULT_ANAM_STRENGTH_DYNAMIC;//=0.1;
private float anamRange=DEFAULT_ANAM_RANGE;// = 1.5  ;
private int DEFAULT_STREAKS_COUNT=7;

private float DEFAULT_STREAKS_LENGTH=0.5f;
private boolean enabledStreaks= false;
private int streaksType=STREAK_TYPE_BASIC;
private float  streaksCount=DEFAULT_STREAKS_COUNT;
private float streaksLength=DEFAULT_STREAKS_LENGTH;
private float streaksStrengthDynamic=DEFAULT_STREAKS_STRENGTH_DYNAMIC;

private  ColorRGBA colorBloom=new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
private  ColorRGBA colorAnam=new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
private  ColorRGBA colorGhosts=new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);
private  ColorRGBA colorStreaks=new ColorRGBA(1.0f, 0.5f, 0.25f,1.f);

//private float timeOfWork=0;
private Vector2f clickPoint=new Vector2f(0,0);
private Vector2f resolution=new Vector2f(0,0);
Application app;
public BLFFilter(Application app) {
super("BLFFilter");
this.app=app;
}


@Override
protected void initFilter(AssetManager assetManager, RenderManager arg1, ViewPort arg2, int w, int h) {
resolution.set(w,h);
material = new Material(assetManager, "MatDefs/BetterLensFlare/BetterLensFlare.j3md");
material.setBoolean("Enabled", enabled);
material.setVector2("Resolution", resolution);
 
//Bloom
material.setFloat("Duration", duration);
material.setFloat("StartTime", startTime); 
material.setBoolean("BloomFadeOut", bloomFadeOut);
material.setBoolean("EnabledFakeBloom", enabledFakeBloom);
material.setFloat("BloomStrength", bloomStrength); 
material.setFloat("BloomRange", bloomRange); 
material.setFloat("BloomStrengthDynamic", bloomStrengthDynamic); 
material.setColor("ColorBloom", colorBloom); 
 
//Anamorphic
material.setBoolean("EnabledAnamorphic", enabledAnamorphic); 
material.setFloat("AnamStrength", anamStrength); 
material.setFloat("AnamRange", anamRange); 
material.setFloat("AnamStrengthDynamic", anamStrengthDynamic); 
material.setColor("ColorAnam", colorAnam); 
 
//Ghosts
material.setBoolean("EnabledGhosts", enabledGhosts);
material.setBoolean("EnabledDistortion", enabledDistortion);  
material.setColor("ColorGhosts", colorGhosts); 

//Streaks
material.setColor("ColorStreaks", colorStreaks); 
material.setFloat("StreaksLength", streaksLength); 
material.setFloat("StreaksCount", streaksCount); 
material.setInt("StreaksType", streaksType); 
material.setBoolean("EnabledStreaks", enabledStreaks); 

}


@Override
protected Material getMaterial() {
return material;
}
  
/**

The duration. A 
@param duration
*/
public void setDuration(float duration) {
//checkFloatArgument(softness, 0, 1, "softness");
this.duration = duration;
if(material!=null)
material.setFloat("Duration", duration);
}
public float getDuration() {
return duration;
}
  

public boolean getBloomFadeOut() {
return bloomFadeOut;
}
 
public void startStartTime(  )
{
    //        
    float timeSinceStart =  app.getTimer().getTimeInSeconds();
   material.setFloat("StartTime", timeSinceStart); 
    
} 
public void setEnabledEffect(boolean enabled) {
 //  super.setEnabled(enabled);
  this.enabled=enabled;
   // System.out.println("DDDD="+enabled);
  //
  if(material!=null)
    material.setBoolean("Enabled", enabled); 
}
public void setEnabledFakeBloom(boolean enabledFakeBloom) {
  this.enabledFakeBloom=enabledFakeBloom;
   //
  if(material!=null)
    material.setBoolean("EnabledFakeBloom", enabledFakeBloom); 
}
public void setEnabledGhosts(boolean enabledGhosts) {
  this.enabledGhosts=enabledGhosts;
      //
  if(material!=null)
    material.setBoolean("EnabledGhosts", enabledGhosts); 
}
 


public void setBloomFadeOut(boolean bloomFadeOut) {
  this.bloomFadeOut=bloomFadeOut;
   //
  material.setBoolean("BloomFadeOut", bloomFadeOut);
}

public boolean isBloomFadeOut( )
{
    return bloomFadeOut;
}

public void setClickPoint(Vector2f clickPoint ) {
   material.setVector2("ClickPoint", clickPoint);
}


@Override
public void write(JmeExporter ex) throws  IOException {
super.write(ex);
OutputCapsule oc = ex.getCapsule(this);
oc.write(duration, "Duration", DEFAULT_DURATION);
oc.write(startTime, "StartTime", 0);
oc.write(bloomFadeOut, "BloomFadeOut", false);
oc.write(enabledFakeBloom, "EnabledFakeBloom", false);
oc.write(bloomStrength, "BloomStrength", 0f);
oc.write(bloomRange, "BloomRange", 0f);
oc.write(bloomStrengthDynamic, "BloomStrengthDynamic", 0f);
oc.write(colorBloom, "ColorBloom", ColorRGBA.Blue);

oc.write(enabledAnamorphic, "EnabledAnamorphic", false);
oc.write(anamStrength, "AnamStrength", 0f);
oc.write(anamRange, "AnamRange", 0f);
oc.write(anamStrengthDynamic, "AnamStrengthDynamic", 0f);
oc.write(colorAnam, "ColorAnam", ColorRGBA.Blue);

oc.write(enabledGhosts, "EnabledAnamorphic", false);
oc.write(enabledDistortion, "EnabledAnamorphic", false);
oc.write(colorGhosts, "ColorAnam", ColorRGBA.Blue);

oc.write(enabledStreaks, "EnabledStreaks", false);
oc.write(streaksLength, "StreaksLength", 0f);
oc.write(streaksCount, "StreaksCount", 0f);
oc.write(streaksType, "StreaksType", 0 );
oc.write(colorStreaks, "ColorStreaks", ColorRGBA.Blue);
}
 


@Override
public void read(JmeImporter im) throws IOException {
super.read(im);
InputCapsule ic = im.getCapsule(this);
duration = ic.readFloat("Duration", DEFAULT_DURATION);
startTime = ic.readFloat("StartTime", 0);
bloomFadeOut = ic.readBoolean("BloomFadeOut", false);
enabledFakeBloom = ic.readBoolean("EnabledFakeBloom", false);
bloomStrength = ic.readFloat("BloomStrength", 0);
bloomRange = ic.readFloat("BloomRange", 0);
bloomStrengthDynamic = ic.readFloat("BloomStrengthDynamic", 0);
 
 enabledAnamorphic = ic.readBoolean("EnabledAnamorphic", false);
anamStrength = ic.readFloat("AnamStrength", 0);
anamRange = ic.readFloat("AnamRange", 0);
anamStrengthDynamic = ic.readFloat("AnamStrengthDynamic", 0);
 
enabledGhosts = ic.readBoolean("EnabledAnamorphic", false);
enabledDistortion = ic.readBoolean("EnabledAnamorphic", false);
 
enabledStreaks = ic.readBoolean("EnabledStreaks", false);
streaksLength = ic.readFloat("StreaksLength", 0);
streaksCount = ic.readFloat("StreaksCount", 0);
streaksType = ic.readInt("StreaksType", 0);
 
}



    public float getBloomStrength() {
        return bloomStrength;
    }

    public void setBloomStrength(float bloomStrength) {
        checkFloatArgument(bloomStrength, 0, 5, "BloomStrength");
        this.bloomStrength = bloomStrength;
          if(material!=null)
          material.setFloat("BloomStrength", bloomStrength); 
    }

    public float getBloomStrengthDynamic() {
        return bloomStrengthDynamic;
    }

    public void setBloomStrengthDynamic(float bloomStrengthDynamic) {
        this.bloomStrengthDynamic = bloomStrengthDynamic;
       if(material!=null)
          material.setFloat("BloomStrengthDynamic", bloomStrengthDynamic); 
    }

    public float getBloomRange() {
        return bloomRange;
    }

    public void setBloomRange(float bloomRange) {
           checkFloatArgument(bloomRange, 0, 15, "BloomRange");
        this.bloomRange = bloomRange;
        if(material!=null)
          material.setFloat("BloomRange", bloomRange); 
    }

    public boolean isEnabledAnamorphic() {
        return enabledAnamorphic;
    }

    public void setEnabledAnamorphic(boolean enabledAnamorphic) {
        this.enabledAnamorphic=enabledAnamorphic;
         if(material!=null)
          material.setBoolean("EnabledAnamorphic", enabledAnamorphic); 
       
    }

    public float getAnamStrength() {
        return anamStrength;
    }

    public void setAnamStrength(float anamStrength) {
           checkFloatArgument(anamStrength, 0, 5, "AnamStrength");
        this.anamStrength = anamStrength;
          if(material!=null)
          material.setFloat("AnamStrength", anamStrength); 
    }

    public float getAnamStrengthDynamic() {
        return anamStrengthDynamic;
    }

    public void setAnamStrengthDynamic(float anamStrengthDynamic) {
        this.anamStrengthDynamic = anamStrengthDynamic;
       if(material!=null)
          material.setFloat("AnamStrengthDynamic", anamStrengthDynamic); 
    }

    public float getAnamRange() {
        return anamRange;
    }

    public void setAnamRange(float anamRange) {
        checkFloatArgument(anamRange, 0, 15, "AnamRange");
        this.anamRange = anamRange;
        if(material!=null)
          material.setFloat("AnamRange", anamRange); 
    }

    public boolean isEnabledDistortion() {
        return enabledDistortion;
    }

    public void setEnabledDistortion(boolean enabledDistortion) {
        this.enabledDistortion = enabledDistortion;
         if(material!=null)
           material.setBoolean("EnabledDistortion", enabledDistortion); 
    }
    
    
     public ColorRGBA getColorBloom() {
        return colorBloom;
    }

    public void setColorBloom(ColorRGBA colorBloom) {
        this.colorBloom = colorBloom;
          if(material!=null)
            material.setColor("ColorBloom", colorBloom); 
    }

    public ColorRGBA getColorAnam() {
        return colorAnam;
    }

    public void setColorAnam(ColorRGBA colorAnam) {
        this.colorAnam = colorAnam;
          if(material!=null)
            material.setColor("ColorAnam", colorAnam); 
    }

    public ColorRGBA getColorGhosts() {
        return colorGhosts;
    }

    public void setColorGhosts(ColorRGBA colorGhosts) {
        this.colorGhosts = colorGhosts;
        if(material!=null)
            material.setColor("ColorGhosts", colorGhosts); 
    }
    
     public boolean isEnabledStreaks() {
        return enabledStreaks;
    }

    public void setEnabledStreaks(boolean enabledStreaks) {
        this.enabledStreaks = enabledStreaks;
      if(material!=null)
          material.setBoolean("EnabledStreaks", enabledStreaks); 
    }

    public int getStreaksType() {
        return streaksType;
       
    }

    public void setStreaksType(int streaksType) {
        checkFloatArgument(streaksType, 0, 1, "StreaksType");
        this.streaksType = streaksType;
        if(material!=null)
          material.setInt("StreaksType", streaksType); 
    }

    public float getStreaksCount() {
        return streaksCount;
    }

    public void setStreaksCount(float streaksCount) {
       checkIntArgument((int)streaksCount,3, 50, "StreaksCount");
        this.streaksCount = streaksCount;
          if(material!=null)
          material.setFloat("StreaksCount", streaksCount); 
    }

    public float getStreaksLength() {
        return streaksLength;
    }
 
    public void setStreaksLength(float streaksLength) {
         checkFloatArgument( streaksLength, 0, 5, "StreaksLength");
        this.streaksLength = streaksLength;
           if(material!=null)
          material.setFloat("StreaksLength", streaksLength); 
    }

    public ColorRGBA getColorStreaks() {
        return colorStreaks;
    }
  
    public void setColorStreaks(ColorRGBA colorStreaks) {
        this.colorStreaks = colorStreaks;
         if(material!=null)
            material.setColor("ColorStreaks", colorStreaks); 
    }

    public float getStreaksStrengthDynamic() {
        return streaksStrengthDynamic;
    }

    public void setStreaksStrengthDynamic(float streaksStrengthDynamic) {
        this.streaksStrengthDynamic = streaksStrengthDynamic;
        if(material!=null)
        material.setFloat("StreaksStrengthDynamic", streaksStrengthDynamic);
    }

   
 
private   void checkFloatArgument(float value, float min, float max, String name) {
if (value < min || value > max) {
throw new IllegalArgumentException(name + " was " + value + " but should be between " + min + " and " + max);
}
}
private   void checkIntArgument(int value, int min, int max, String name) {
if (value < min || value > max) {
throw new IllegalArgumentException(name + " was " + value + " but should be between " + min + " and " + max);
}
}   
}
 
}