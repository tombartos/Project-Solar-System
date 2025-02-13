package fr.univtln.tbartier367.solarsystem;


import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

public class App extends SimpleApplication {
    private Long time_multiplier = 1L;
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
        flyCam.setMoveSpeed(25f);
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
        light.setRadius(9999999999f);
        light.setPosition(new Vector3f(0, 0, 0));
        rootNode.addLight(light);

        
        earth = Planet.factory(assetManager, "Models/earth.j3o", "Textures/earth.jpg", 1f,1f, 60, 40);
        //earth.getSpatial().setLocalTranslation(0, 0, 60);
        rootNode.attachChild(earth.getSpatial());
        initKeys();
    }

    private void initKeys() {
    /* You can map one or several inputs to one named mapping. */
    inputManager.addMapping("TimeFaster",  new KeyTrigger(KeyInput.KEY_P));
    inputManager.addMapping("TimeSlower",  new KeyTrigger(KeyInput.KEY_O));

    /* Add the named mappings to the action listeners. */
    inputManager.addListener(actionListener, "TimeFaster", "TimeSlower");

    }

      /** Use this listener for KeyDown/KeyUp events */
      final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("TimeFaster")){
                if (time_multiplier < 0L){
                    if (time_multiplier == -1L)
                        time_multiplier = 1L;
                    else
                        time_multiplier /= 5L;
                }
                else
                    time_multiplier*=5L;
            }

            if (name.equals("TimeSlower")){
                if (time_multiplier >0L){
                    if (time_multiplier == 1L)
                        time_multiplier = -1L;
                    else
                        time_multiplier /= 5L;
                }
                else
                    time_multiplier *= 5L;
            }
        }   
    };

    final private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            }
    };

    @Override
    public void simpleUpdate(float tpf) {
      // Update the time parameter (this is in seconds)
      time +=  tpf*time_multiplier;
      System.out.println(time);

      // Update the Earth's position
      //earth.getSpatial().setLocalTranslation(x, y, 0);


      for (Planet p : Planet.getPlanetlist()) {
        p.UpdatePosition(time/31536000 *p.getRevolutionSpeed());  //Constantes basees sur la terre pour des multiplicateurs x1 sur la terre
        p.getSpatial().rotate(0, p.getRotationSpeed()*tpf*time_multiplier/86400, 0);
    }

    }
}

