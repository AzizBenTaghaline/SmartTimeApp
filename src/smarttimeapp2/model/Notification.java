/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public class Notification {
    private String typeNotification;
    private boolean lue;             
    private String message; 

    public Notification(String typeNotification, String message) {
        this.typeNotification = typeNotification;
        this.message = message;
        this.lue = false;
    }

    public String getTypeNotification() { return typeNotification; }
    public String getMessage() { return message; }

    public void setTypeNotification(String typeNotification) { this.typeNotification = typeNotification; }
    public void setMessage(String message) { this.message = message; }

    public void marquerCommeLue() {
        this.lue = true;
    }

    @Override
    public String toString() {
        return "Notification: " + message 
            + "\nType: " + typeNotification 
            + "\nLue: " + (lue ? "Oui" : "Non");
    }
}


