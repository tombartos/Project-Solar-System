package fr.univtln.tbartier367.solarsystem;


import com.jme3.app.SimpleApplication;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

public class App extends SimpleApplication {
    private Float time = 0f;
    private Planet earth;

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
        //First we initialize the sun, it's not a planet because it's the only one to have the Unshaded material
        Spatial sunSpatial = assetManager.loadModel("Models/sun.j3o");
        Material sunMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMaterial.setBoolean("ColorMap",true); 
        sunMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/sun.jpg")); // with Lighting.j3md
        sunSpatial.setMaterial(sunMaterial); 

        sunSpatial.setLocalTranslation(0,0,0);
        sunSpatial.setLocalScale(4,4,4);
        sunMaterial.setColor("GlowColor", ColorRGBA.Yellow);
        rootNode.attachChild(sunSpatial);

        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        light.setRadius(9999f);
        light.setPosition(new Vector3f(0, 0, 0));
        rootNode.addLight(light);

        
        earth = Planet.factory(assetManager, "Models/earth.j3o", "Textures/earth.jpg", 1f, 60, 40);
        //earth.getSpatial().setLocalTranslation(0, 0, 60);
        rootNode.attachChild(earth.getSpatial());



    }

    @Override
    public void simpleUpdate(float tpf) {
      // Update the time parameter
      time += tpf;
      System.out.println(time);

      // Update the Earth's position
      //earth.getSpatial().setLocalTranslation(x, y, 0);


      for (Planet p : Planet.getPlanetlist()) {
        p.UpdatePosition(time);
        //TODO: gerer les differentes rotations independemment
        p.getSpatial().rotate(0, p.getRotationSpeed()*tpf, 0);
    }

    }
}