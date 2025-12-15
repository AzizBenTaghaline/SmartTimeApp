/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public final class Productivite extends Activite{
    private double progression;
    public Productivite(String nom, String type, int duree,double progression) {
        super(nom, type, duree);
        this.progression=progression;
    } 
     public double getProgression() {return progression;}
     public void setProgression(double progression) {this.progression = progression;}
     public void resetProgression(){
         this.setProgression(0);
         System.out.println("Avancement de l'activite remise a 0%");
     }
    @Override
    public String toString() {
        return super.toString() + "\nProgression dans l'activite: " + progression + "%";
    }
}

