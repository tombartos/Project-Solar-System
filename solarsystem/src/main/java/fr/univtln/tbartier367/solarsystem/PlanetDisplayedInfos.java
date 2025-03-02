package fr.univtln.tbartier367.solarsystem;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(staticName="of")
@Getter
public class PlanetDisplayedInfos {
    private String name;
    private float weight;
    private float equatorialRadius;
    private Long volume;
    private float gravity;
    private float rotationSpeed;
    private float pressure;    
}
