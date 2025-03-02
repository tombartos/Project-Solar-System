package fr.univtln.tbartier367.solarsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName="of")
@Getter
public class PlanetDisplayedInfos {
    private String name;
    private float weight;
    private float equatorialRadius;
    private float volume;
    private float gravity;
    private float rotationSpeed;
    private float pressure;
    
    @Override
    public String toString() {
        String res = "Name : " + name + "\n" + 
        "Weight : " + weight + " kg\n" +
        "Equatorial Radius : " + equatorialRadius + " km\n" +
        "Volume : " + volume + " km3\n" +
        "Gravity : " + gravity + " m/s2\n" + 
        "Rotation Speed : " + rotationSpeed + " km/h\n" +
        "Atmospheric Pressure : " + pressure + " hPa";
        return res;
    }
}
