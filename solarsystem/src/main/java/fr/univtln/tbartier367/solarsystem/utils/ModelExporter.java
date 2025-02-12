package fr.univtln.tbartier367.solarsystem.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

import fr.univtln.tbartier367.solarsystem.App;

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
     */
    public void obj_to_j3o(String model, String destination){
        Spatial spatial = assetManager.loadModel(model);
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
            obj_to_j3o("Models/earth.obj", "ExportedModels/earth.j3o");
        }
}
