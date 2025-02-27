package fr.univtln.tbartier367.solarsystem.utils;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.system.AppSettings;

import com.jme3.app.SimpleApplication;


public class ModelExporter extends SimpleApplication {

    public static void main(String[] args){
        
        AppSettings settings=new AppSettings(true);
        settings.setWindowSize(1280, 720);
        ModelExporter app = new ModelExporter();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start(); // start the game
    }

     /**
     * The default constructor. 
     */
    public ModelExporter(){
    }
    

        /**
     * Exports the model model (path) from obj to j3o format and put it in the destination
     * path.
     * @param model the path to the model from resources (includes file name)
     * @param destination the destination path (includes file name)
     * @param reverse_normals boolean to chose if you want to reverse normals to fix texture problems
     */
    public void obj_to_j3o(String model, String destination, boolean reverse_normals){
        // I don't understand why the reading process is done from resources path but destination works as usual
        Spatial spatial = assetManager.loadModel(model);
        if (reverse_normals){
        // Reverse the normals of the mesh, written by Copilot
            if (spatial instanceof Geometry) {
                Geometry geom = (Geometry) spatial;
                Mesh mesh = geom.getMesh();
                VertexBuffer vb = mesh.getBuffer(VertexBuffer.Type.Normal);
                if (vb != null) {
                    FloatBuffer normals = (FloatBuffer) vb.getData();
                    for (int j = 0; j < normals.capacity(); j++) {
                        normals.put(j, -normals.get(j));
                    }
                    vb.updateData(normals);
                }
            }
        }
        BinaryExporter exporter = BinaryExporter.getInstance();
        File file = new File(destination);
        try {
            exporter.save(spatial, file);
        }
        catch (IOException ex){
            System.out.println("Erreur lors de l'export du modele.");
        }
        
    }

    
        @Override
        public void simpleInitApp() {
            obj_to_j3o("Models/asteroids.j3o", "ExportedModels/asteroids.j3o", false);
        }
}


