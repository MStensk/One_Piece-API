package org.SP2.controllers.impl;

import io.javalin.http.Context;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.SP2.config.HibernateConfig;
import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;

import java.util.ArrayList;
import java.util.List;

public class PopulateController {

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("onepiece");

    public void populate(Context ctx) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // --- Create Crews ---
            Crew strawHats = new Crew();
            strawHats.setCrewName("Straw Hat Pirates");
            strawHats.setCrewShip("Thousand Sunny");
            strawHats.setCrewJollyRoger("Straw Hat Skull");
            strawHats.setCrewCaptain("Monkey D. Luffy");

            Crew blackbeard = new Crew();
            blackbeard.setCrewName("Blackbeard Pirates");
            blackbeard.setCrewShip("Saber of Xebec");
            blackbeard.setCrewJollyRoger("Crossbones with 3 Skulls");
            blackbeard.setCrewCaptain("Marshall D. Teach");

            Crew redHair = new Crew();
            redHair.setCrewName("Red Hair Pirates");
            redHair.setCrewShip("Red Force");
            redHair.setCrewJollyRoger("Red-haired Skull");
            redHair.setCrewCaptain("Shanks");

            // --- Create Pirates ---
            strawHats.getPirates().addAll(getStrawHatPirates(strawHats));
            blackbeard.getPirates().addAll(getBlackbeardPirates(blackbeard));
            redHair.getPirates().addAll(getRedHairPirates(redHair));

            // --- Persist ---
            em.persist(strawHats);
            em.persist(blackbeard);
            em.persist(redHair);

            em.getTransaction().commit();
            ctx.status(201).result("✅ Database populated successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("❌ Failed to populate: " + e.getMessage());
        }
    }

    private static List<Pirate> getStrawHatPirates(Crew crew) {
        List<Pirate> pirates = new ArrayList<>();
        pirates.add(new Pirate("Monkey D. Luffy", "May 5", "Male", 19, 3000000000L, crew));
        pirates.add(new Pirate("Roronoa Zoro", "Nov 11", "Male", 21, 1111000000L, crew));
        pirates.add(new Pirate("Nami", "July 3", "Female", 20, 366000000L, crew));
        pirates.add(new Pirate("Sanji", "March 2", "Male", 21, 1032000000L, crew));
        pirates.add(new Pirate("Usopp", "April 1", "Male", 19, 500000000L, crew));
        pirates.add(new Pirate("Nico Robin", "Feb 6", "Female", 30, 930000000L, crew));
        return pirates;
    }

    private static List<Pirate> getBlackbeardPirates(Crew crew) {
        List<Pirate> pirates = new ArrayList<>();
        pirates.add(new Pirate("Marshall D. Teach", "Aug 3", "Male", 40, 3996000000L, crew));
        pirates.add(new Pirate("Shiryu of the Rain", "Jan 11", "Male", 39, 0L, crew));
        pirates.add(new Pirate("Van Augur", "Oct 5", "Male", 34, 0L, crew));
        pirates.add(new Pirate("Lafitte", "March 13", "Male", 38, 0L, crew));
        pirates.add(new Pirate("Jesus Burgess", "Dec 25", "Male", 35, 0L, crew));
        return pirates;
    }

    private static List<Pirate> getRedHairPirates(Crew crew) {
        List<Pirate> pirates = new ArrayList<>();
        pirates.add(new Pirate("Shanks", "March 9", "Male", 39, 4048900000L, crew));
        pirates.add(new Pirate("Benn Beckman", "Nov 9", "Male", 50, 0L, crew));
        pirates.add(new Pirate("Lucky Roux", "June 5", "Male", 40, 0L, crew));
        pirates.add(new Pirate("Yasopp", "Aug 2", "Male", 47, 0L, crew));
        return pirates;
    }
}
