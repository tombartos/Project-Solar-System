package fr.univtln.tbartier367.solarsystem;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import lombok.Getter;


@Getter
public class Planet {
    private static List<Planet> planetlist = new ArrayList<>();
    private String name;
    private AssetManager assetManager;
    private Spatial spatial;
    private Material material;
    private float semi_major;  //Semi-Major axis for ellipse
    private float semi_minor;  //Semi-Minor axis for ellipse
    private float rotationSpeed; //Scale from earth, 2 is 2x faster than earth
    private float revolutionSpeed; //Same
    private float size;            //same
    private float x = 0f;       //Position
    private float y = 0f;          
    private float z = 0f;
    private ColorRGBA color;

    private Planet(String Name, AssetManager assetManager ,String modelPath, String texturePath, float RotationSpeed, float RevolutionSpeed, float Semi_major, float Semi_minor, float Size, ColorRGBA Color){
        name = Name;
        spatial = assetManager.loadModel(modelPath);
        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        material.setColor("Ambient", ColorRGBA.White);   // ... color of this object
        material.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected
        material.setColor("Specular", ColorRGBA.White);
        material.setTexture("DiffuseMap", assetManager.loadTexture(texturePath)); // with Lighting.j3md
        //material.setFloat("Shininess", 8f); 
        spatial.setMaterial(material);
        semi_major = Semi_major;
        semi_minor = Semi_minor;
        rotationSpeed = RotationSpeed;      //Scalling based on earth, 2 = 2x faster than earth
        revolutionSpeed = RevolutionSpeed;  //Same
        size = Size;
        color = Color;                      //Color of the trajectory line
        spatial.setLocalScale(Size);

    }

    public static List<Planet> getPlanetlist() {
        return planetlist;
    }

    public static Planet factory(String name, AssetManager assetManager ,String modelPath, String texturePath, float rotationspeed, float revolutionspeed, float Semi_major, float eccentricity, float size, ColorRGBA color){
          //We calculate the semi minor from eccentricity because all the data the we can find only gives Semi Major and eccentricity
          //But never the semi minor
        float Semi_minor = Semi_major * (float) Math.sqrt(1 - eccentricity * eccentricity);
        Planet planet = new Planet(name, assetManager, modelPath, texturePath, rotationspeed, revolutionspeed, Semi_major, Semi_minor, size, color);
        planetlist.add(planet);
        return planet;
    }

    /**
     * Update the planet position depending on time
     * @param time The current time of the simulation in seconds
     * @param Saturn_Rings A reference to the Saturn Rings Spatial
     */

    public void UpdatePosition(float time, Spatial Saturn_Rings){
        x = semi_major * (float) Math.cos(time);
        y = semi_minor * (float) Math.sin(time);
        spatial.setLocalTranslation(x, y, z);
        if (name.equals("Saturn"))
            Saturn_Rings.setLocalTranslation(x,y,z);
    }

    
}


