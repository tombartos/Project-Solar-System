package fr.univtln.tbartier367.solarsystem;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

//Imports for drawTrajectory
//import com.jme3.scene.shape.Line;
//import com.jme3.material.Material;
//import com.jme3.material.RenderState;
//import com.jme3.math.Vector3f;
//import com.jme3.scene.Geometry;


public class Moon extends Planet {
    private Planet attachedPlanet;
    private float orbitScale; //We need to scale the orbit values if we want to see the moons moving

    private Moon(String Name, AssetManager assetManager ,String modelPath, String texturePath, float RotationSpeed, float RevolutionSpeed, float Semi_major, float Semi_minor, float Size, ColorRGBA Color, Planet attachedplanet, float orbitscale, PlanetDisplayedInfos PlanetInfos){
        //We need to multiply semi major and minor by 25 for scalling reasons or we don't see the moon moving properly
        super(Name, assetManager , modelPath, texturePath, RotationSpeed, RevolutionSpeed, Semi_major*orbitscale, Semi_minor*orbitscale, Size, Color, PlanetInfos);
        attachedPlanet = attachedplanet;
        orbitScale = orbitscale;
    }

    public static Moon factory(String name, AssetManager assetManager ,String modelPath, String texturePath, float rotationspeed, float revolutionspeed, float Semi_major, float eccentricity, float size, ColorRGBA color, Planet attachedplanet, float orbitscale, PlanetDisplayedInfos PlanetInfos){
          //We calculate the semi minor from eccentricity because all the data the we can find only gives Semi Major and eccentricity
          //But never the semi minor
        float Semi_minor = Semi_major * (float) Math.sqrt(1 - eccentricity * eccentricity);
        Moon planet = new Moon(name, assetManager, modelPath, texturePath, rotationspeed, revolutionspeed, Semi_major, Semi_minor, size, color, attachedplanet, orbitscale, PlanetInfos);
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
    
    x = centerX + semi_major * (float) Math.cos(-time);
    y = centerY;
    z = centerZ + semi_minor * (float) Math.sin(-time);
    spatial.setLocalTranslation(x, y, z);
    }

    @Override
        /**
     * Draw the trajectory of the planet
     * NOT IMPLEMENTED YET
     * 
     */
    public void drawTrajectory(Node rootNode, AssetManager assetManager){
        return;
    }

    //TODO: make it works properly (with update every frame ?)

    // public void drawTrajectory(Node rootNode, AssetManager assetManager){
    //     float centerX = attachedPlanet.x; // Direct access for better performance
    //     float centerY = attachedPlanet.y;
    //     float centerZ = attachedPlanet.z;
    //     float local_x = centerX + semi_major;     //At the begining we start with cos(0)=1 and sin(0)=0;
    //     float local_y = centerY;
    //     float local_z = centerZ;
    //     float x2;
    //     float y2;
    //     float z2;
        

    //     for(float i=0.05f; i<2*Math.PI+0.05f; i+=0.01f){
    //         x2 = local_x;
    //         y2 = local_y;
    //         z2 = local_z;
    //         local_x = centerX + semi_major * (float) Math.cos(i);
    //         local_y = centerY;
    //         local_z = centerZ + semi_minor * (float) Math.sin(i);
    //         Line line = new Line(new Vector3f(local_x,local_y,local_z), new Vector3f(x2,y2,z2));
    //         Geometry geometry = new Geometry("Bullet", line);
    //         Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //         mat.setColor("Color", color);
    //         geometry.setMaterial(mat);
    //         mat.getAdditionalRenderState().setFaceCullMode( RenderState.FaceCullMode.Off );
    //         mat_traj_List.add(mat);
    //         geometry.setCullHint(Spatial.CullHint.Never);
    //         geo_traj_List.add(geometry);
    //         rootNode.attachChild(geometry);
    //     } 
    //}
}
