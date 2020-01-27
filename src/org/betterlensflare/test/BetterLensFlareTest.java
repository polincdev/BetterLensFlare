 
package org.betterlensflare.test;


 
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
 
import org.betterlensflare.blf.BetterLensFlare;
 
 
/** Shows how to use Better Lens Flare effect.*/
public class BetterLensFlareTest extends SimpleApplication {

  public static void main(String[] args) {
    BetterLensFlareTest app = new BetterLensFlareTest();
    app.start();
  }
  private Node shootables;
  private Geometry mark;
  private BetterLensFlare betterLensFlare;
   
  @Override
  public void simpleInitApp() {
     
     flyCam.setMoveSpeed(5);  
     setDisplayStatView(false);
     setDisplayFps(false);
     
    /** create four colored boxes and a floor */
    shootables = new Node("Shootables");
    rootNode.attachChild(shootables);
    shootables.attachChild(makeCube("a Dragon", -5f, 0f, 5f));
    shootables.attachChild(makeCube("a tin can", -2f, -4f, 0f));
    shootables.attachChild(makeCube("the Sheriff", 1f, 1f, -10f));
    shootables.attachChild(makeCube("the Deputy", -5f, 0f, -12f));
    shootables.attachChild(makeFloor());
    
    
    //Target to follow - must have unique name for the sake of raycasting
     Geometry target= makeBall("Sun",-15,1,-15);
     rootNode.attachChild(target);
   
     //Main color - common for everything
     ColorRGBA lensFlareColor=new ColorRGBA(1.0f, 0.5f, 0.25f,1f);
     // ColorRGBA lensFlareColor=new ColorRGBA(0.2f, 0.3f, 0.75f,1f);
     
    //Bloom effect to make up for case when target is not completely occluded. 
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    BloomFilter bloomFilter=new BloomFilter(BloomFilter.GlowMode.Objects);
    bloomFilter.setExposurePower(5); 
    // bloom.setDownSamplingFactor(2);  
    bloomFilter.setBloomIntensity(2); 
    bloomFilter.setBlurScale(2f); 
    bloomFilter.setDownSamplingFactor(2);
    fpp.addFilter(bloomFilter);
    this.getViewPort().addProcessor(fpp);
     
    //Manager - state
    betterLensFlare=new BetterLensFlare(this, fpp, rootNode);
    this.stateManager.attach(betterLensFlare);
    
    //Enable elements of LENS FLARE
    //Fake bloom - works mostly for sun-type models
    betterLensFlare.enableBloom(lensFlareColor,1.5f, 3.5f,10f, 0.1f,.15f);
    //Long vertical - in most cases useful for indoor light sources
    betterLensFlare.enableAnamorphic(lensFlareColor,1.5f, 5.0f,10f, 0.1f);
    //Dots and distortion created by the lens
    betterLensFlare.enableGhosts(lensFlareColor,true);
    //Sun rays
    betterLensFlare.enableStreaks(lensFlareColor,BetterLensFlare.STREAK_TYPE_BASIC,10,2.7f,10f, 0.1f );
    //
    //Vector3f worldPos=target.getWorldTranslation();
    //Vector3f posOnScreen=cam.getScreenCoordinates(worldPos);
    //START for target and check occlusion on every 5th frame.
    betterLensFlare.startLensFlare(target,5);
  }
   
  
  

  /** A cube object for target practice */
  protected Geometry makeCube(String name, float x, float y, float z) {
    Box box = new Box(1, 5, 1);
    Geometry cube = new Geometry(name, box);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
    cube.setMaterial(mat1);
    return cube;
  }

  /** A floor to show that the "shot" can go through several objects. */
  protected Geometry makeFloor() {
    Box box = new Box(15, .2f, 15);
    Geometry floor = new Geometry("the Floor", box);
    floor.setLocalTranslation(0, -4, -5);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.Gray);
    floor.setMaterial(mat1);
    return floor;
  }
 
  
  /** A sphere object for target practice */
  protected Geometry makeBall(String name, float x, float y, float z) {
    Sphere sphere = new Sphere(32,32,0.25f);
    Geometry cube = new Geometry(name, sphere);
    cube.setLocalTranslation(x, y, z);
    Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat1.setColor("Color", ColorRGBA.randomColor());
     mat1.setColor("GlowColor", ColorRGBA.Yellow);
    cube.setMaterial(mat1);
    return cube;
    
  
    
  }
  
}
 
