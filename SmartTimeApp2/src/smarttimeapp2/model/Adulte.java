 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public final class Adulte extends Utilisateur{
    private int ID;
     public Adulte(String nom, int age, String email, int ID) {
        super(nom, age, email);
        this.ID = ID;
    }
    public int getID() { return ID; }
 @Override
    public String toString() {
         String result = "Utilisateur: " + nom 
                 +"\nID: " + ID
                + "\nAge: " + age 
                + "\nEmail: " + email 
                + "\nAppareils:\n";

        for (int i = 0; i < appareils.size(); i++) {
            result += (i + 1) + ". " + appareils.get(i) + "\n";
        }

        return result;
    }
}

