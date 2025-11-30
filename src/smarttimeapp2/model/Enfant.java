/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public final class Enfant extends Utilisateur {
    private int superviseurID;

    public Enfant(String nom, int age, String email,int supervisuerID) {
        super(nom, age, email);
        this.superviseurID = superviseurID;
    }

    public int getSuperviseurID() { return superviseurID; }
    public void setSupervisuerID(int superviseurID) { this.superviseurID = superviseurID; }

    @Override
    public String toString() {
        return super.toString() + "l'ID du superviseur: " + superviseurID + "\n";
    }
}

