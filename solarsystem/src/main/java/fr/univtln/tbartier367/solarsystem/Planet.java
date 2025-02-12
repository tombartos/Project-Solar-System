package fr.univtln.tbartier367.solarsystem;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import lombok.Getter;


@Getter
public class Planet {
    private AssetManager assetManager;
    private Spatial spatial;
    private Material material;

    public Planet(AssetManager assetManager ,String modelPath, String texturePath){
        spatial = assetManager.loadModel(modelPath);
        material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        material.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        material.setColor("Ambient", ColorRGBA.White);   // ... color of this object
        material.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected
        material.setColor("Specular", ColorRGBA.White);
        material.setTexture("DiffuseMap", assetManager.loadTexture(texturePath)); // with Lighting.j3md
        //material.setFloat("Shininess", 8f); 
        spatial.setMaterial(material);               
    }
}


