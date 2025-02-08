package fr.univtln.tbartier367.solarsystem;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture;

public class App extends SimpleApplication {

    /**
     * The main method.
     * @param args the main method arguments
     */
    public static void main(String[] args){
        
        AppSettings settings=new AppSettings(true);
        settings.setWindowSize(1280, 720);
        App app = new App();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start(); // start the game
    }

    /**
     * The default constructor. 
     */
    public App(){
    }

    @Override
    public void simpleInitApp() {

        Planet earth = new Planet(assetManager, "Models/earth.obj", "Textures/earth.jpg");
        // Attach the model to the root node
        earth.getSpatial().setLocalTranslation(0, 0, -10);
        rootNode.attachChild(earth.getSpatial());

        // Add a directional light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(0f, 0f, -1.0f).normalizeLocal());
        rootNode.addLight(sun);
    }
}