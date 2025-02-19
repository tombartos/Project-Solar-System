package fr.univtln.tbartier367.solarsystem;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;


public class Moon extends Planet {
    private Planet attachedPlanet;
    private float orbitScale; //We need to scale the orbit values if we want to see the moons moving

    private Moon(String Name, AssetManager assetManager ,String modelPath, String texturePath, float RotationSpeed, float RevolutionSpeed, float Semi_major, float Semi_minor, float Size, ColorRGBA Color, Planet attachedplanet, float orbitscale){
        //We need to multiply semi major and minor by 25 for scalling reasons or we don't see the moon moving properly
        super(Name, assetManager , modelPath, texturePath, RotationSpeed, RevolutionSpeed, Semi_major*orbitscale, Semi_minor*orbitscale, Size, Color);
        attachedPlanet = attachedplanet;
        orbitScale = orbitscale;
    }

    public static Moon factory(String name, AssetManager assetManager ,String modelPath, String texturePath, float rotationspeed, float revolutionspeed, float Semi_major, float eccentricity, float size, ColorRGBA color, Planet attachedplanet, float orbitscale){
          //We calculate the semi minor from eccentricity because all the data the we can find only gives Semi Major and eccentricity
          //But never the semi minor
        float Semi_minor = Semi_major * (float) Math.sqrt(1 - eccentricity * eccentricity);
        Moon planet = new Moon(name, assetManager, modelPath, texturePath, rotationspeed, revolutionspeed, Semi_major, Semi_minor, size, color, attachedplanet, orbitscale);
        planetlist.add(planet);
        return planet;
    }


    @Override
    public void UpdatePosition(float time, Spatial Saturn_Rings){
    // Saturn rings are not used here but are necessary to override
    // Change the center of the ellipse by adding centerX and centerY
    float centerX = attachedPlanet.x; // Direct access for better performance
    float centerY = attachedPlanet.y;
    float centerZ = attachedPlanet.z;
    
    x = centerX + semi_major * (float) Math.cos(time);
    y = centerY;
    z = centerZ + semi_minor * (float) Math.sin(time);
    spatial.setLocalTranslation(x, y, z);
    }


}
