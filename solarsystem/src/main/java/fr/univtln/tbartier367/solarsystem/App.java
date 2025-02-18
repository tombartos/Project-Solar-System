package fr.univtln.tbartier367.solarsystem;


import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

public class App extends SimpleApplication {
    private Long time_multiplier = 1L;
    private Float time = 0f;
    private Spatial Saturn_Rings;

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

    /**
     * Adds a 2D line to the scene.
     *
     * @param p1    The starting point of the line as a Vector3f.
     * @param p2    The ending point of the line as a Vector3f.
     * @param color The color of the line as a ColorRGBA.
     */
    public void add2DLine(Vector3f p1, Vector3f p2, ColorRGBA color){
        Line line = new Line(p1, p2);
        //line.setLineWidth(2);
        Geometry geometry = new Geometry("Bullet", line);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geometry.setMaterial(mat);
        mat.getAdditionalRenderState().setFaceCullMode( RenderState.FaceCullMode.Off );
        geometry.setCullHint(Spatial.CullHint.Never);
        rootNode.attachChild(geometry);
    }

    /**
     * Draw the trajectory of the planet
     * 
     * @param planet the planet that you want the trajectory
     */
    public void drawTrajectory(Planet planet){
        //It's not a Planet method beause we need to access to rootNode, we could give it in parameter
        //But I prefer to just put the method here
        float x = planet.getSemi_major();     //At the begining we start with cos(0)=1 and sin(0)=0;
        float y = 0f;
        float x2;
        float y2;
        ColorRGBA color = planet.getColor();

        for(float i=0.05f; i<2*Math.PI; i+=0.01f){
            x2 = x;
            y2 = y;
            x = planet.getSemi_major() * (float) Math.cos(i);
            y = planet.getSemi_major() * (float) Math.sin(i);
            add2DLine(new Vector3f(x,y,0), new Vector3f(x2,y2,0), color);
        } 

    }
    
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(40f);
        cam.setFrustumFar(10000f);
        //First we initialize the sun, it's not a planet because it's the only one to have the Unshaded material
        Sphere sunSphere = new Sphere(32, 32, 10);
        Geometry sunSpatial = new Geometry("Sun", sunSphere);
        Material sunMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        sunMaterial.setBoolean("ColorMap",true); 
        sunMaterial.setTexture("ColorMap", assetManager.loadTexture("Textures/sun.jpg")); // with Lighting.j3md
        sunSpatial.setMaterial(sunMaterial); 

        sunSpatial.setLocalTranslation(0,0,0);
        sunSpatial.setLocalScale(2);
        sunMaterial.setColor("GlowColor", ColorRGBA.Yellow);
        rootNode.attachChild(sunSpatial);

        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        light.setRadius(99999999999999999999999999999999999999f);
        light.setPosition(new Vector3f(0, 0, 0));
        rootNode.addLight(light);

        
        Planet earth = Planet.factory("Earth", assetManager, "Models/earth.j3o", "Textures/earth.jpg", 1f,1f, 149.598f, 0.0167f, 1f, ColorRGBA.Blue);
        rootNode.attachChild(earth.getSpatial());

        Planet mars = Planet.factory("Mars", assetManager, "Models/mars.j3o", "Textures/mars.jpg", 0.9732f, 0.5317f, 227.956f, 0.0935f, 0.532f, ColorRGBA.Red);
        rootNode.attachChild(mars.getSpatial());

        Planet jupiter = Planet.factory("Jupiter", assetManager, "Models/jupiter.j3o", "Textures/jupiter.jpg", 2.418f, 0.0842f, 778.479f, 0.0487f, 11.209f, ColorRGBA.Brown);
        rootNode.attachChild(jupiter.getSpatial());

        Planet mercury = Planet.factory("Mercury", assetManager, "Models/mercury.j3o", "Textures/mercury.jpg", 0.0056f, 4.1954f, 57.909f, 0.2056f, 0.383f, ColorRGBA.Gray);
        rootNode.attachChild(mercury.getSpatial());

        Planet saturn = Planet.factory("Saturn", assetManager, "Models/saturn.j3o", "Textures/saturn.jpg", 2.2522f, 0.0339f, 1432.041f, 0.0520f, 9.449f, ColorRGBA.Orange);
        rootNode.attachChild(saturn.getSpatial());

        Planet uranus = Planet.factory("Uranus", assetManager, "Models/saturn.j3o", "Textures/uranus.jpg", 1.4117f, 0.0119f, 2867.043f, 0.0469f, 4.007f, ColorRGBA.Cyan); //Same model than Saturn, it's just a sphere
        rootNode.attachChild(uranus.getSpatial());

        Planet neptune = Planet.factory("Neptune", assetManager, "Models/saturn.j3o", "Textures/neptune.jpg", 1.4897f, 0.0061f, 4514.953f, 0.0097f, 3.883f, ColorRGBA.Blue);
        rootNode.attachChild(neptune.getSpatial());

        Saturn_Rings = assetManager.loadModel("Models/rings.j3o");
        Material mat_Saturn_Rings = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_Saturn_Rings.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // Make the rings double-sided
        mat_Saturn_Rings.setTexture("ColorMap", assetManager.loadTexture("Textures/rings.png"));
        Saturn_Rings.setMaterial(mat_Saturn_Rings);
        Saturn_Rings.setLocalScale(8f);
        rootNode.attachChild(Saturn_Rings);

        for (Planet p : Planet.getPlanetlist())
            drawTrajectory(p);
            

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
        p.UpdatePosition(time/31536000 *p.getRevolutionSpeed(), Saturn_Rings);  //Constantes basees sur la terre pour des multiplicateurs x1 sur la terre
        p.getSpatial().rotate(0, p.getRotationSpeed()*tpf*time_multiplier/86400, 0);
        }
    }
}

