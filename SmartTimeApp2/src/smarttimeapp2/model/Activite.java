/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public sealed abstract class Activite permits Divertissement, ReseauxSociaux, Productivite {
    private String app;
    private String type; 
    private int duree;

    public Activite(String nom, String type, int duree) {
        this.app = nom;
        this.type = type;
        this.duree = duree;
    }

    public String getNom() { return app; }
    public int getDuree() { return duree; }

    @Override
    public String toString() {
        //String nomApp=(app!=Null)? app.getNom():"Appareil inconnu"
        return "Nom de l'application: "+ app + " - Activite: " +type + " - Duree: "+ duree + "mins";
    }

}
