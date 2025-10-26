package org.SP2.daos;

import jakarta.persistence.EntityManagerFactory;
import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;

public class CrewDAOImpl implements IDAO {

    private static CrewDAOImpl instance;
    private final EntityManagerFactory emf;

    private CrewDAOImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static synchronized CrewDAOImpl getInstance(EntityManagerFactory emf) {
        if (instance == null) {
            instance = new CrewDAOImpl(emf);
        }
        return instance;
    }


    @Override
    public List<Crew> getAllCrews() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Crew c", Crew.class).getResultList();
        }
    }

    @Override
    public Optional<Crew> getCrewById(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            return Optional.ofNullable(em.find(Crew.class, id));
        }
    }

    @Override
    public Crew createCrew(Crew crew) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            em.persist(crew);
            tx.commit();
            return crew;
        }
    }

    @Override
    public Crew updateCrew(Crew crew) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Crew merged = em.merge(crew);
            tx.commit();
            return merged;
        }
    }

    @Override
    public boolean deleteCrew(int id) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Crew crew = em.find(Crew.class, id);
            if (crew != null) {
                em.remove(crew);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        }
    }

    @Override
    public Crew addPirate(Crew crew, Pirate pirate) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Crew managedCrew = em.merge(crew);
            pirate.setCrew(managedCrew);
            managedCrew.getPirates().add(pirate);
            em.persist(pirate);
            tx.commit();
            return managedCrew;
        }
    }

    @Override
    public Crew removePirate(Crew crew, Pirate pirate) {
        try (EntityManager em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            Crew managedCrew = em.merge(crew);
            Pirate managedPirate = em.merge(pirate);
            managedCrew.getPirates().remove(managedPirate);
            em.remove(managedPirate);
            tx.commit();
            return managedCrew;
        }
    }

    @Override
    public List<Pirate> getPiratesForCrew(Crew crew) {
        try (EntityManager em = emf.createEntityManager()) {
            Crew managedCrew = em.find(Crew.class, crew.getId());
            return managedCrew != null ? managedCrew.getPirates() : List.of();
        }
    }
}
