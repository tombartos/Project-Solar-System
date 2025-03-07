package fr.univtln.tbartier367.solarsystem;


import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;

public class App extends SimpleApplication {
    private static final int RES_WIDTH = 1920;
    private static final int RES_HEIGHT = 1080;
    private static Long time_multiplier = 1L;
    private static Float time = 0f;
    private static Geometry sunSpatial;
    private static Geometry Saturn_Rings;
    private static BitmapText speedText;
    private static BitmapText timeText;
    private static int seconds = 0;
    private static int minutes = 0;
    private static int hours = 0;
    private static int days = 0;
    private static boolean showTrajectories = true;
    private static ChaseCamera chaseCam;
    private static Node planets = new Node();
    private static Label lbl;
    private static String sunInfo = "Name : Sun\n" + 
            "Weight : 1.988E30 kg\n"+
            "Equatorial Radius : 696 342 km\n" + 
            "Volume : 1.412E18 km3\n" +
            "Gravity : 274 m/s2\n" +
            "Rotation Speed : 7.189 km/h\n" +
            "Atmospheric Pressure : 0 hPa";
   


    /**
     * The main method.
     * @param args the main method arguments
     */
    public static void main(String[] args){
        
        AppSettings settings = new AppSettings(true);
        settings.setResolution(RES_WIDTH, RES_HEIGHT);
        settings.setFullscreen(true);
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

    //Methode de Audouard Florian
    private static Vector3f getClickDirection(Camera cam, Vector2f screenPos) {
        Vector3f worldCoords = cam.getWorldCoordinates(screenPos, 0f).clone();
        Vector3f direction = worldCoords.subtract(cam.getLocation()).normalize();
        return direction;
    }

    /**
    * Initializes the Kuiper Belt
    * @param assetManager The Asset Manager of the project
    * @param rootNode The Node where the asteroids will be creates, usually rootNode
    * @param semi_minor The semi minor axis of the belt
    * @param semi_major The semi major axis of the belt
    */
    private static List<Spatial> init_KuiperBelt(AssetManager assetManager, Node rootNode, float semi_minor, float semi_major){
            //Kuiper Belt
            float x;
            float y;
            Material asteroids_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  //Same texture for all asteroids
            asteroids_mat.setBoolean("UseMaterialColors",true); 
            asteroids_mat.setColor("Ambient", ColorRGBA.White);   
            asteroids_mat.setColor("Diffuse", ColorRGBA.White);   
            asteroids_mat.setColor("Specular", ColorRGBA.White);
            TextureKey key = new TextureKey("/Textures/asteroids.jpeg", true);
            asteroids_mat.setTexture("DiffuseMap", assetManager.loadTexture(key));
            
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
        speedText.setSize(guiFont.getCharSet().getRenderedSize()+20);
        speedText.setText("Speed : "+time_multiplier);
        speedText.setLocalTranslation(300, speedText.getLineHeight(), 0);
        guiNode.attachChild(speedText);
        
        timeText = new BitmapText(guiFont);
        timeText.setSize(guiFont.getCharSet().getRenderedSize()+20);
        timeText.setText("Time elapsed : " + days + " days, "+ hours + ":" + minutes + ":" + seconds);
        timeText.setLocalTranslation(1000, timeText.getLineHeight(), 0);
        guiNode.attachChild(timeText);



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
        sunSpatial = new Geometry("Sun", sunSphere);
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

        //Planets displayed infos initalization
        PlanetDisplayedInfos earthInfos = PlanetDisplayedInfos.of(
            "Earth", 5.972e24f, 6378.0f, 1.083206916846e12f, 9.807f, 1674.4f, 101.325f);
        PlanetDisplayedInfos marsInfos = PlanetDisplayedInfos.of(
            "Mars", 6.4171e23f, 3389.5f, 1.6318e11f, 3.721f, 868.22f, 0.636f);
        PlanetDisplayedInfos jupiterInfos = PlanetDisplayedInfos.of(
            "Jupiter", 1.898e27f, 69911.0f, 1.43128e12f, 24.79f, 12.6f, 0.0f);
        PlanetDisplayedInfos mercuryInfos = PlanetDisplayedInfos.of(
            "Mercury", 3.3011e23f, 2439.7f, 6.0827208e7f, 3.7f, 10.892f, 0.0f);
        PlanetDisplayedInfos saturnInfos = PlanetDisplayedInfos.of(
            "Saturn", 5.683e26f, 58232.0f, 8.2713e11f, 10.44f, 9.87f, 0.0f);
        PlanetDisplayedInfos uranusInfos = PlanetDisplayedInfos.of(
            "Uranus", 8.681e25f, 25362.0f, 6.8334e10f, 8.69f, 2.59f, 0.0f);
        PlanetDisplayedInfos neptuneInfos = PlanetDisplayedInfos.of(
            "Neptune", 1.024e26f, 24622.0f, 6.2525e10f, 11.15f, 2.68f, 0.0f);
        PlanetDisplayedInfos venusInfos = PlanetDisplayedInfos.of(
            "Venus", 4.8675e24f, 6051.8f, 9.28415345893e11f, 8.87f, 6.52f, 92.0f);
        //Moons displayed infos initialization
        PlanetDisplayedInfos moonInfos = PlanetDisplayedInfos.of(
            "Moon", 7.342e22f, 1737.4f, 2.1958e10f, 1.62f, 4.627f, 0.0f);
        PlanetDisplayedInfos phobosInfos = PlanetDisplayedInfos.of(
            "Phobos", 1.0659e16f, 11.267f, 5.7e3f, 0.0057f, 0.0f, 0.0f);
        PlanetDisplayedInfos deimosInfos = PlanetDisplayedInfos.of(
            "Deimos", 1.4762e15f, 6.2f, 9.7e2f, 0.003f, 0.0f, 0.0f);
        PlanetDisplayedInfos ioInfos = PlanetDisplayedInfos.of(
            "Io", 8.9319e22f, 1821.6f, 2.53e10f, 1.796f, 17.334f, 0.0f);
        PlanetDisplayedInfos europaInfos = PlanetDisplayedInfos.of(
            "Europa", 4.7998e22f, 1560.8f, 1.593e10f, 1.314f, 13.74f, 0.0f);

        //Planets initialization
        Planet earth = Planet.factory("Earth", assetManager, "Models/earth.j3o", "Textures/earth.jpg", 1f,1f, 149.598f, 0.0167f, 1f, ColorRGBA.Blue, earthInfos);
        planets.attachChild(earth.getSpatial());

        Planet mars = Planet.factory("Mars", assetManager, "Models/mars.j3o", "Textures/mars.jpg", 0.9732f, 0.5317f, 227.956f, 0.0935f, 0.532f, ColorRGBA.Red, marsInfos);
        planets.attachChild(mars.getSpatial());

        Planet jupiter = Planet.factory("Jupiter", assetManager, "Models/saturn.j3o", "Textures/jupiter.jpg", 2.418f, 0.0842f, 778.479f, 0.0487f, 11.209f, ColorRGBA.Brown, jupiterInfos);
        planets.attachChild(jupiter.getSpatial());

        Planet mercury = Planet.factory("Mercury", assetManager, "Models/mercury.j3o", "Textures/mercury.jpg", 0.0056f, 4.1954f, 57.909f, 0.2056f, 0.05f, ColorRGBA.Gray, mercuryInfos); //Size of this one is not accurate
        planets.attachChild(mercury.getSpatial());

        Planet saturn = Planet.factory("Saturn", assetManager, "Models/saturn.j3o", "Textures/saturn.jpg", 2.2522f, 0.0339f, 1432.041f, 0.0520f, 9.449f, ColorRGBA.Orange, saturnInfos);
        planets.attachChild(saturn.getSpatial());

        Planet uranus = Planet.factory("Uranus", assetManager, "Models/saturn.j3o", "Textures/uranus.jpg", 1.4117f, 0.0119f, 2867.043f, 0.0469f, 4.007f, ColorRGBA.Cyan, uranusInfos); //Same model than Saturn, it's just a sphere
        planets.attachChild(uranus.getSpatial());

        Planet neptune = Planet.factory("Neptune", assetManager, "Models/saturn.j3o", "Textures/neptune.jpg", 1.4897f, 0.0061f, 4514.953f, 0.0097f, 3.883f, ColorRGBA.Blue, neptuneInfos);
        planets.attachChild(neptune.getSpatial());

        Planet venus = Planet.factory("Venus", assetManager, "Models/saturn.j3o", "Textures/venus.jpg", 0.00856f, 1.6255f, 108.210f, 0.0068f, 1.5f, ColorRGBA.Brown, venusInfos);   //Size not accurate
        planets.attachChild(venus.getSpatial());


        //Rings of Saturn, there is a problem with the texture loading
        //Saturn_Rings = assetManager.loadModel("Models/rings.j3o");
        Torus torus = new Torus(20, 20, 4, 32);
        Saturn_Rings = new Geometry("rings", torus);

        Material mat_Saturn_Rings = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_Saturn_Rings.setBoolean("UseMaterialColors",true);  // Set some parameters, e.g. blue.
        mat_Saturn_Rings.setColor("Ambient", ColorRGBA.White);   // ... color of this object
        mat_Saturn_Rings.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected
        mat_Saturn_Rings.setColor("Specular", ColorRGBA.White);
        mat_Saturn_Rings.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off); // Make the rings double-sided
        mat_Saturn_Rings.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        //Saturn_Rings.setQueueBucket(Bucket.Transparent);
        Texture rings_tex = assetManager.loadTexture("Textures/rings.png");
        rings_tex.setWrap(Texture.WrapMode.Repeat);
        mat_Saturn_Rings.setTexture("DiffuseMap", rings_tex);
        Saturn_Rings.setMaterial(mat_Saturn_Rings);
        // Saturn_Rings.setLocalScale(8f);
        Saturn_Rings.rotate(-FastMath.PI/2, 0f, 0.0001f);
        Saturn_Rings.setLocalScale(1f, 1f, 0.01f);
        rootNode.attachChild(Saturn_Rings);

        //Trajectory lines for planets
        for (Planet p : Planet.getPlanetlist())
            p.drawTrajectory(rootNode, assetManager);

        //Moons initialization
        Moon moon = Moon.factory("Moon", assetManager, "Models/moon.j3o", "Textures/moon.jpg", 0.878f, 13.5185f, 0.3844f, 0.0549f, 0.2f, ColorRGBA.Gray, earth, 25f, moonInfos);
        planets.attachChild(moon.getSpatial());

        Moon phobos = Moon.factory("Phobos", assetManager, "Models/phobos.j3o", "Textures/phobos.jpg", 4f*mars.getRotationSpeed(), 1215f, 0.009376f, 0.0151f, 0.05f, ColorRGBA.Brown, mars, 500f, phobosInfos);
        planets.attachChild(phobos.getSpatial());

        Moon deimos = Moon.factory("Deimos", assetManager, "Models/deimos.j3o", "Textures/deimos.jpg", 4f*mars.getRotationSpeed(), 311f, 0.0235f, 0.0002f, 0.05f, ColorRGBA.Brown, mars, 500f, deimosInfos);
        planets.attachChild(deimos.getSpatial());

        Moon io = Moon.factory("Io", assetManager, "Models/saturn.j3o", "Textures/io.jpg", 4f*jupiter.getRotationSpeed(), 421.7f, 0.0028f, 0.0041f, 0.286f, ColorRGBA.Yellow, jupiter, 10000f, ioInfos);
        planets.attachChild(io.getSpatial());

        Moon europa = Moon.factory("Europa", assetManager, "Models/saturn.j3o", "Textures/europa.jpg", 4f*jupiter.getRotationSpeed(), 210f, 0.0094f, 0.001f, 0.245f, ColorRGBA.White, jupiter, 10000f, europaInfos);
        planets.attachChild(europa.getSpatial());


        //Kuiper Belt Initialization
        //The list is not used at the moment but can be useful in the future
        List<Spatial> asteroids_List = init_KuiperBelt(assetManager, rootNode, neptune.getSemi_minor()*1.2f, neptune.getSemi_major()*1.2f); //Not sure if the list will be used

        //Camera Initialization
        flyCam.setEnabled(false);
        cam.setFrustumFar(10000f);
        chaseCam = new ChaseCamera(cam, sunSpatial, inputManager);
        chaseCam.setDefaultDistance(500);
        chaseCam.setMaxDistance(4000);
        chaseCam.setDefaultHorizontalRotation(-FastMath.PI/2);
        chaseCam.setMinVerticalRotation(-FastMath.PI/2+0.01f);
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // Only right mouse button
        
        //GUI Initialization
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        // Create a simple container for our elements
        Container myWindow = new Container();
        guiNode.attachChild(myWindow);

        // Put it somewhere that we will see it.
        // Note: Lemur GUI elements grow down from the upper left corner.
        myWindow.setLocalTranslation(0, RES_HEIGHT, 0);

        // Add some elements
        lbl = new Label(sunInfo);
        lbl.setFontSize(40);
        myWindow.addChild(lbl);

        Container myWindow2 = new Container();
        guiNode.attachChild(myWindow2);
        //myWindow2.setLocalTranslation(1700,250,0);
        myWindow2.setLocalTranslation(RES_WIDTH-RES_WIDTH/8, RES_HEIGHT/5, 0);
        Label lbl_controls = new Label("Controls :\n"+
                                        "P: Time Faster\n"+
                                        "O: Time Slower\n"+
                                        "T: Show Tajectories\n"+
                                        "R_Click: Rotate\n"+
                                        "L_Click: Focus on planet\n"+
                                        "ESC: Quit\n");
        lbl_controls.setFontSize(20);
        myWindow2.addChild(lbl_controls);

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
            if (name.equals("Select") && !keyPressed){      //Planet selection
                 // 1. Reset results list.
                CollisionResults results = new CollisionResults();
                // 2. Aim the ray
                Vector2f cursor = inputManager.getCursorPosition();
                Ray ray = new Ray(cam.getLocation(), getClickDirection(cam, cursor));
                // 3. Collect intersections between Ray and Shootables in results list.
                planets.collideWith(ray, results);
                if(results.size()>0){
                    Geometry hit = results.getCollision(0).getGeometry();
                    if (hit.equals(sunSpatial)){
                        chaseCam.setSpatial(sunSpatial);
                        lbl.setText(sunInfo);
                    }
                    else{
                        for(Planet p: Planet.planetlist){
                            Spatial tmp = p.getSpatial();
                            if(hit.equals(tmp)) {
                                chaseCam.setSpatial(tmp);
                                PlanetDisplayedInfos infos = p.getPlanetInfos();
                                lbl.setText(infos.toString());
                            }
                        }
                    }
                    
                }
            }
        }
    };

    //  The Analog Listener is not used at the moment
    //
    // final private AnalogListener analogListener = new AnalogListener() {
    //     @Override
    //     public void onAnalog(String name, float value, float tpf) {
    //         }
    // };

    @Override
    public void simpleUpdate(float tpf) {
      // Update the time parameter (this is in seconds)
      time +=  tpf*time_multiplier;
      seconds = (int) (time%60);
      minutes = (int) ((time/60)%60);
      hours = (int) ((time/3600)%24);
      days = (int) (time/86400);
      timeText.setText("Time elapsed : " + days + " days, "+ hours + ":" + minutes + ":" + seconds);

      for (Planet p : Planet.getPlanetlist()) {
        p.UpdatePosition(time/31536000 * 2*FastMath.PI *p.getRevolutionSpeed(), Saturn_Rings);  //Constantes basees sur la terre (en secondes) pour des multiplicateurs x1 sur la terre
        p.getSpatial().rotate(0, p.getRotationSpeed() *2*FastMath.PI *tpf*time_multiplier/86400, 0);
        }
    }
}

//Sources utilisees pour les informations des planetes : Wikipedia, Nasa, Copilot