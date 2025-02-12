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
    private AssetManager assetManager;
    private Spatial spatial;
    private Material material;
    private float semi_major;  //Semi-Major axis for ellipse
    private float semi_minor;  //Semi-Minor axis for ellipse
    private float rotationSpeed;
    private float x = 0f;       //Position
    private float y = 0f;          
    private float z = 0f;

    private Planet(AssetManager assetManager ,String modelPath, String texturePath, float RotationSpeed, float Semi_major, float Semi_minor){
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
        rotationSpeed = RotationSpeed;

    }

    public static List<Planet> getPlanetlist() {
        return planetlist;
    }

    public static Planet factory(AssetManager assetManager ,String modelPath, String texturePath, float rotationspeed, float Semi_major, float Semi_minor){
        Planet planet = new Planet(assetManager, modelPath, texturePath, rotationspeed, Semi_major, Semi_minor);
        planetlist.add(planet);
        return planet;
    }

    /**
     * Update the planet position depending on time
     * @param time The current time of the simulation
     */

    public void UpdatePosition(float time){
        x = semi_major * (float) Math.cos(time);
        y = semi_minor * (float) Math.sin(time);
        spatial.setLocalTranslation(x, y, z);
    }

    
}


