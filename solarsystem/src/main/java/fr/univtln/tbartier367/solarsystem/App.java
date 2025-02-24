package fr.univtln.tbartier367.solarsystem;


import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.noise.Color;
import com.jme3.util.SkyFactory;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

public class App extends SimpleApplication {
    private Long time_multiplier = 1L;
    private Float time = 0f;
    private Spatial Saturn_Rings;
    private BitmapText speedText;
    private boolean showTrajectories = true;
    private ChaseCamera chaseCam;
    private Node planets = new Node();

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
    * Initializes the Kuiper Belt
    * 
    * @param semi_minor The semi minor axis of the belt
    * @param semi_major The semi major axis of the belt
    */
    public List<Spatial> init_KuiperBelt(float semi_minor, float semi_major){
            //Kuiper Belt
            float x;
            float y;
            Material asteroids_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  //Same texture for all asteroids
            asteroids_mat.setBoolean("UseMaterialColors",true); 
            asteroids_mat.setColor("Ambient", ColorRGBA.White);   
            asteroids_mat.setColor("Diffuse", ColorRGBA.White);   
            asteroids_mat.setColor("Specular", ColorRGBA.White);
            asteroids_mat.setTexture("DiffuseMap", assetManager.loadTexture("/Textures/asteroids.jpeg"));
            
            List<Spatial> asteroids_list = new ArrayList<>();         //We make a list to avoid losing access to asteroids
            for(float i = 0; i<2*Math.PI; i+=0.04f){
                //System.out.print(" CPT = "+cpt);
                x = semi_major * (float) Math.cos(i);
                y = semi_minor * (float) Math.sin(i);
                Spatial tmp = assetManager.loadModel("/Models/asteroids.j3o");
                tmp.setMaterial(asteroids_mat);
                tmp.setLocalTranslation(x, y, 0);
                tmp.setLocalScale(8);
                asteroids_list.add(tmp);
                rootNode.attachChild(tmp);                
            }
            return asteroids_list;
    }
    
    @Override
    public void simpleInitApp() {
        rootNode.attachChild(planets);

        //HUD config
        setDisplayStatView(false);
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        speedText= new BitmapText(guiFont);
        speedText.setSize(guiFont.getCharSet().getRenderedSize());
        speedText.setText("Speed : "+time_multiplier);
        speedText.setLocalTranslation(300, speedText.getLineHeight(), 0);
        guiNode.attachChild(speedText);

         // Sky texture using six images
        Spatial sky = SkyFactory.createSky(
        assetManager,
        assetManager.loadTexture("Textures/skybox/right.png"),
        assetManager.loadTexture("Textures/skybox/left.png"),
        assetManager.loadTexture("Textures/skybox/front.png"),
        assetManager.loadTexture("Textures/skybox/back.png"),
        assetManager.loadTexture("Textures/skybox/top.png"),
        assetManager.loadTexture("Textures/skybox/bottom.png")
        );
        rootNode.attachChild(sky);
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
        planets.attachChild(sunSpatial);

        //Light in the sun
        PointLight light = new PointLight();
        light.setColor(ColorRGBA.White);
        light.setRadius(99999999999999999999999999999999999999f);
        light.setPosition(new Vector3f(0, 0, 0));
        rootNode.addLight(light);

        //Planets initialization
        Planet earth = Planet.factory("Earth", assetManager, "Models/earth.j3o", "Textures/earth.jpg", 1f,1f, 149.598f, 0.0167f, 1f, ColorRGBA.Blue);
        planets.attachChild(earth.getSpatial());

        Planet mars = Planet.factory("Mars", assetManager, "Models/mars.j3o", "Textures/mars.jpg", 0.9732f, 0.5317f, 227.956f, 0.0935f, 0.532f, ColorRGBA.Red);
        planets.attachChild(mars.getSpatial());

        Planet jupiter = Planet.factory("Jupiter", assetManager, "Models/jupiter.j3o", "Textures/jupiter.jpg", 2.418f, 0.0842f, 778.479f, 0.0487f, 11.209f, ColorRGBA.Brown);
        planets.attachChild(jupiter.getSpatial());

        Planet mercury = Planet.factory("Mercury", assetManager, "Models/mercury.j3o", "Textures/mercury.jpg", 0.0056f, 4.1954f, 57.909f, 0.2056f, 0.383f, ColorRGBA.Gray);
        planets.attachChild(mercury.getSpatial());

        Planet saturn = Planet.factory("Saturn", assetManager, "Models/saturn.j3o", "Textures/saturn.jpg", 2.2522f, 0.0339f, 1432.041f, 0.0520f, 9.449f, ColorRGBA.Orange);
        planets.attachChild(saturn.getSpatial());

        Planet uranus = Planet.factory("Uranus", assetManager, "Models/saturn.j3o", "Textures/uranus.jpg", 1.4117f, 0.0119f, 2867.043f, 0.0469f, 4.007f, ColorRGBA.Cyan); //Same model than Saturn, it's just a sphere
        planets.attachChild(uranus.getSpatial());

        Planet neptune = Planet.factory("Neptune", assetManager, "Models/saturn.j3o", "Textures/neptune.jpg", 1.4897f, 0.0061f, 4514.953f, 0.0097f, 3.883f, ColorRGBA.Blue);
        planets.attachChild(neptune.getSpatial());

        Planet venus = Planet.factory("Venus", assetManager, "Models/saturn.j3o", "Textures/venus.jpg", 0.00856f, 1.6255f, 108.210f, 0.0068f, 0.949f, ColorRGBA.Brown);
        planets.attachChild(venus.getSpatial());


        //Rings of Saturn, there is a problem with the texture loading
        Saturn_Rings = assetManager.loadModel("Models/rings.j3o");
        Material mat_Saturn_Rings = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_Saturn_Rings.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // Make the rings double-sided
        mat_Saturn_Rings.setTexture("ColorMap", assetManager.loadTexture("Textures/rings.png"));
        Saturn_Rings.setMaterial(mat_Saturn_Rings);
        Saturn_Rings.setLocalScale(8f);
        rootNode.attachChild(Saturn_Rings);

        //Trajectory lines for planets
        for (Planet p : Planet.getPlanetlist())
            p.drawTrajectory(rootNode, assetManager);

        //Moons initialization
        Moon moon = Moon.factory("Moon", assetManager, "Models/moon.j3o", "Textures/moon.jpg", 0.878f, 13.5185f, 0.3844f, 0.0549f, 0.2f, ColorRGBA.Gray, earth, 25f);
        planets.attachChild(moon.getSpatial());

        Moon phobos = Moon.factory("Phobos", assetManager, "Models/phobos.j3o", "Textures/phobos.jpg", 4f*mars.getRotationSpeed(), 1215f, 0.009376f, 0.0151f, 0.05f, ColorRGBA.Brown, mars, 500f);
        planets.attachChild(phobos.getSpatial());

        Moon deimos = Moon.factory("Deimos", assetManager, "Models/deimos.j3o", "Textures/deimos.jpg", 4f*mars.getRotationSpeed(), 311f, 0.0235f, 0.0002f, 0.05f, ColorRGBA.Brown, mars, 500f);
        planets.attachChild(deimos.getSpatial());

        Moon io = Moon.factory("Io", assetManager, "Models/saturn.j3o", "Textures/io.jpg", 4f*jupiter.getRotationSpeed(), 421.7f, 0.0028f, 0.0041f, 0.286f, ColorRGBA.Yellow, jupiter, 10000f);
        planets.attachChild(io.getSpatial());

        Moon europa = Moon.factory("Europa", assetManager, "Models/saturn.j3o", "Textures/europa.jpg", 4f*jupiter.getRotationSpeed(), 210f, 0.0094f, 0.001f, 0.245f, ColorRGBA.White, jupiter, 10000f);
        planets.attachChild(europa.getSpatial());


        //Kuiper Belt Initialization
        List<Spatial> asteroids_List = init_KuiperBelt(neptune.getSemi_minor()*1.2f, neptune.getSemi_major()*1.2f); //Not sure if the list will be used

        //Camera Initialization
        flyCam.setEnabled(false);
        cam.setFrustumFar(10000f);
        chaseCam = new ChaseCamera(cam, sunSpatial, inputManager);
        chaseCam.setDefaultDistance(500);
        chaseCam.setMaxDistance(4000);
        chaseCam.setDefaultHorizontalRotation(-FastMath.PI/2);
        chaseCam.setMinVerticalRotation(-FastMath.PI/2-0.01f);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // Only right mouse button
        
        initKeys();
    }

    private void initKeys() {
        /* You can map one or several inputs to one named mapping. */
        inputManager.addMapping("TimeFaster",  new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("TimeSlower",  new KeyTrigger(KeyInput.KEY_O));
        inputManager.addMapping("ShowTrajectories", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        /* Add the named mappings to the action listeners. */
        inputManager.addListener(actionListener, "TimeFaster", "TimeSlower", "ShowTrajectories", "Select");
    }

    /** Use this listener for KeyDown/KeyUp events */
    final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("TimeFaster") && !keyPressed){
                if (time_multiplier == 6103515625L){
                    speedText.setText("Speed = " + time_multiplier + " Max Speed");
                    return;
                }
                if (time_multiplier < 0L){
                    if (time_multiplier == -1L)
                        time_multiplier = 1L;
                    else
                        time_multiplier /= 5L;
                }
                else
                    time_multiplier*=5L;
                speedText.setText("Speed : "+time_multiplier);
            }

            if (name.equals("TimeSlower")&& !keyPressed){
                if (time_multiplier == -6103515625L){
                    speedText.setText("Speed = " + time_multiplier + " Max Speed");
                    return;
                }
                if (time_multiplier >0L){
                    if (time_multiplier == 1L)
                        time_multiplier = -1L;
                    else
                        time_multiplier /= 5L;
                }
                else
                    time_multiplier *= 5L;
                speedText.setText("Speed : "+time_multiplier);
            }
            
            if (name.equals("ShowTrajectories") && !keyPressed){
                if (showTrajectories){          //Hide trajectories
                    showTrajectories = false;
                    for (Planet p : Planet.getPlanetlist()) {
                        for(Geometry geo: p.getGeo_traj_List()){
                            geo.setCullHint(Spatial.CullHint.Always);
                        }
                    }
                }
                else {      //Show trajectories
                    showTrajectories = true;   
                    for (Planet p : Planet.planetlist) {
                        for(Geometry geo: p.getGeo_traj_List()){
                            geo.setCullHint(Spatial.CullHint.Never);
                        }
                    }
                }
            }
            if (name.equals("Select") && !keyPressed){
                 // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray
                Vector2f cursor = inputManager.getCursorPosition();
                Vector3f camvec = cam.getDirection(); //CONTINUER POUR FAIRE UNE ORTATION PAR RAPPORT AU CURSEUR
                Ray ray = new Ray(cam.getLocation(), new Vector3f(cursor.getX(), cursor.getY(), cam.getDirection().z)); //Pas sur a tester
                // 3. Collect intersections between Ray and Shootables in results list.
                planets.collideWith(ray, results);
                System.out.println("----- Collisions? " + results.size() + "-----");
                // String hit = results.getCollision(0).getGeometry().getName();
                // System.out.println(hit);
                //TODO: tester et continuer
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
      //System.out.println(time);

      for (Planet p : Planet.getPlanetlist()) {
        p.UpdatePosition(time/31536000 *p.getRevolutionSpeed(), Saturn_Rings);  //Constantes basees sur la terre (en secondes) pour des multiplicateurs x1 sur la terre
        p.getSpatial().rotate(0, p.getRotationSpeed()*tpf*time_multiplier/86400, 0);
        }
    }
}

