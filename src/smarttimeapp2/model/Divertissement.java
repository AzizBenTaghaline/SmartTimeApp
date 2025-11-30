/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

import smarttimeapp2.model.Activite;

/**
 *
 * @author HP
 */
public final class Divertissement extends Activite {
    private String CategorieAge;
    public Divertissement(String nom, String type, int duree, String CategorieAge) {
        super(nom, type, duree);
        this.CategorieAge=CategorieAge;
    }
}

