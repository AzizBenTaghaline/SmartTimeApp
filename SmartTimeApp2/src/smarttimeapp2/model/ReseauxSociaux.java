/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package smarttimeapp2.model;

/**
 *
 * @author HP
 */
public final class ReseauxSociaux extends Activite {
    private int nbrVideosVues;
    private int nbrLikes;
    private int nbrPublications;

    public ReseauxSociaux(String nom, String type, int duree, int nbrVideosVues, int nbrLikes, int nbrPublications) {
        super(nom, type, duree);
        this.nbrVideosVues = nbrVideosVues;
        this.nbrLikes = nbrLikes;
        this.nbrPublications = nbrPublications;
    }

    /*public int getNbrVideosVues() { return nbrVideosVues; }
    public int getNbrLikes() { return nbrLikes; }
    public int getNbrPublications() { return nbrPublications; }

    public void setNbrVideosVues(int nbrVideosVues) { this.nbrVideosVues = nbrVideosVues; }
    public void setNbrLikes(int nbrLikes) { this.nbrLikes = nbrLikes; }
    public void setNbrPublications(int nbrPublications) { this.nbrPublications = nbrPublications; }*/

    @Override
    public String toString() {
        return super.toString() 
            + "\nVideos vues: " + nbrVideosVues
            + " - Likes: " + nbrLikes
            + " - Publications: " + nbrPublications;
    }
}   

