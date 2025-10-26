package org.SP2.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;

import java.util.List;
import java.util.Optional;

public class PirateDAO {
    private static PirateDAO instance;
    private final EntityManagerFactory emf;

    private PirateDAO(EntityManagerFactory emf) { this.emf = emf; }

    public static synchronized PirateDAO getInstance(EntityManagerFactory emf) {
        if (instance == null) instance = new PirateDAO(emf);
        return instance;
    }

    public List<Pirate> getAllPirates() {
        try (EntityManager em = emf.createEntityManager()) {
            // JOIN FETCH to avoid LazyInitializationException and to include Crew
            return em.createQuery(
                            "SELECT p FROM Pirate p LEFT JOIN FETCH p.crew", Pirate.class)
                    .getResultList();
        }
    }

    public Optional<Pirate> getPirateById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            Pirate p = em.createQuery(
                            "SELECT p FROM Pirate p LEFT JOIN FETCH p.crew WHERE p.id = :id",
                            Pirate.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.ofNullable(p);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }


    public Pirate createPirate(int crewId, Pirate pirate) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Crew crew = em.find(Crew.class, crewId);
            if (crew == null) {
                tx.rollback();
                throw new IllegalArgumentException("Crew with id " + crewId + " not found");
            }
            pirate.setCrew(crew);
            em.persist(pirate);
            tx.commit();
            return pirate;
        }
    }

    public Pirate updatePirate(int id, Pirate updates) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Pirate managed = em.find(Pirate.class, id);
            if (managed == null) {
                tx.rollback();
                throw new IllegalArgumentException("Pirate with id " + id + " not found");
            }
            // apply updatable fields
            managed.setPirateName(updates.getPirateName());
            managed.setPirateBirthday(updates.getPirateBirthday());
            managed.setPirateGender(updates.getPirateGender());
            managed.setPirateAge(updates.getPirateAge());
            managed.setPirateBounty(updates.getPirateBounty());
            tx.commit();
            return managed;
        }
    }

    public boolean deletePirate(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Pirate p = em.find(Pirate.class, id);
            if (p != null) {
                em.remove(p);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        }
    }
}

