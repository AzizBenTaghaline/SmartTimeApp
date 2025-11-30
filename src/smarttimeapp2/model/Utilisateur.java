/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;
import java.util.List;
import java.util.ArrayList;
/**
 *
 * @author HP
 */
public sealed abstract class Utilisateur permits Adulte, Enfant{
    protected String nom;
    protected int age;
    protected String email;
    protected List<String> appareils;

    public Utilisateur(String nom, int age, String email){
        this.nom = nom;
        this.age = age;
        this.email = email;
        this.appareils = new ArrayList<>();
    }
   public void ajouterAppareil(/*Appareil*/String appareil) {
        appareils.add(appareil);
    }        
    @Override
    public String toString() {
        String result = "Utilisateur: " + nom 
                + "\nAge: " + age 
                + "\nEmail: " + email 
                + "\nAppareils:\n";

        for (int i = 0; i < appareils.size(); i++) {
            result += (i + 1) + ". " + appareils.get(i) + "\n";
        }

        return result;
    }
}

