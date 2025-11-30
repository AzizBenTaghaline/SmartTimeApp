/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package smarttimeapp2.model;
import smarttimeapp2.model.Adulte;
import smarttimeapp2.model.Activite;
import smarttimeapp2.model.Productivite;
import smarttimeapp2.model.ReseauxSociaux;
import java.util.List;
import java.util.ArrayList;
import smarttimeapp2.model.ActiviteFilter;

/**
 *
 * @author HP
 */
public class Hazem {

    public static void main(String[] args) {

        // Création d’un utilisateur
        Adulte user = new Adulte("Hazem", 25, "hazem@gmail.com",1);
        user.ajouterAppareil("Samsung phone");
        user.ajouterAppareil("Laptop");
        user.ajouterAppareil("Ipad");

        // Ajout de quelques activités
        List<Activite> activites = List.of(
            new Productivite("Travail", "Développement projet", 120, 20),
            new ReseauxSociaux("Instagram", "RéseauxSociaux", 45, 10, 300, 5),
            new Productivite("Lecture", "Lecture technique", 60,85.2 ),
            new ReseauxSociaux("TikTok", "Scrolling", 80, 50, 2000, 10)
        );

        // ✅ Utilisation de lambdas pour filtrer
        ActiviteFilter filtreProductivite = a -> a instanceof Productivite;
        ActiviteFilter filtreLongues = a -> a.getDuree() > 60;
        ActiviteFilter filtreReseauxSociaux = a -> a instanceof ReseauxSociaux && a.getDuree() > 30;

        // ✅ Filtrage avec Stream + lambda
        System.out.println("=== Activités productives ===");
        activites.stream()
        .filter(a -> filtreProductivite.filtrer(a))
        .forEach(a -> System.out.println(a));


        System.out.println("\n=== Activités longues (>60 min) ===");
        activites.stream()
                .filter(a -> filtreLongues.filtrer(a))
                .forEach(a->System.out.println(a));

        System.out.println("\n=== Réseaux sociaux actifs (>30 min) ===");
        activites.stream()
                .filter(a -> filtreReseauxSociaux.filtrer(a))
                .forEach(a->System.out.println(a));

        // ✅ Exemple d'une lambda directe sans variable intermédiaire
        System.out.println("\n=== Activités de plus de 1h triées par durée ===");
        activites.stream()
                .filter(a -> a.getDuree() > 60)
                .sorted((a1, a2) -> Integer.compare(a2.getDuree(), a1.getDuree()))
                .forEach(a -> System.out.println(a.getNom() + " (" + a.getDuree() + " min)"));
    }
}
