# BetterLensFlare
Lens Flare effect for JMonkey Game Engine

### Features
1. Target based
2. Fake bloom
3. Anamorphism
4. Streaks - two types
5. Ghosts + Distortion 

### Usage:
```
//Let be some geometry added to the rootNode.
 Geometry target = ....

//Define and attach manager - state  
BetterLensFlare betterLensFlare=new BetterLensFlare(this, fpp, rootNode);
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
//START for target and check occlusion on every 5th frame.
betterLensFlare.startLensFlare(target,5);
```
### Credits
https://hub.jmonkeyengine.org/t/lens-flare-code-small-update-screens/23796

https://www.shadertoy.com/view/4sX3Rs

#### Screenshots:

![BetterLensFlare1](../master/img/BetterLensFlare1.jpg)

![BetterLensFlare2](../master/img/BetterLensFlare2.jpg)

![BetterLensFlare3](../master/img/BetterLensFlare3.jpg)

![BetterLensFlare4](../master/img/BetterLensFlare4.jpg)
