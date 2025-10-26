package org.SP2.controllers.impl;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.SP2.config.HibernateConfig;
import org.SP2.daos.CrewDAOImpl;
import org.SP2.daos.IDAO;
import org.SP2.dtos.CrewDTO;
import org.SP2.dtos.PirateDTO;
import org.SP2.entities.Crew;
import org.SP2.entities.Pirate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CrewController {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("onepiece");
    private final IDAO crewDAO = CrewDAOImpl.getInstance(emf);

    private static final Logger logger = LoggerFactory.getLogger(Crew.class);
    private static final Logger debugLogger = LoggerFactory.getLogger("app");

    // -------------------
    // Hotel CRUD
    // -------------------

    public void getAllCrews(Context ctx) {
        try {
            List<CrewDTO> crews = CrewDTO.toDTOList(crewDAO.getAllCrews());
            ctx.status(HttpStatus.OK).json(crews);
            logger.info("Fetched all crews, count: {}", crews.size());
        } catch (Exception e) {
            logger.error("Error fetching all crews", e);
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error fetching crews: " + e.getMessage());
        }
    }

    public void getCrewById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        crewDAO.getCrewById(id).ifPresentOrElse(crew -> {
            ctx.status(HttpStatus.OK).json(new CrewDTO(crew));
            logger.info("Fetched crew with id: {}", id);
        }, () -> {
            ctx.status(HttpStatus.NOT_FOUND).result("crew not found");
            logger.warn("crew with id {} not found", id);
        });
    }

    public void createCrew(Context ctx) {
        try {
            CrewDTO dto = ctx.bodyAsClass(CrewDTO.class);
            Crew created = crewDAO.createCrew(dto.toEntity());
            ctx.status(HttpStatus.CREATED).json(new CrewDTO(created));
        } catch (Exception e) {
            e.printStackTrace(); // prints full stack trace to console
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error: " + e.getMessage());
        }
    }

    public void updateCrew(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        CrewDTO dto = ctx.bodyAsClass(CrewDTO.class);
        dto.setId(id);

        Crew updated = crewDAO.updateCrew(dto.toEntity());
        ctx.status(HttpStatus.OK).json(new CrewDTO(updated));
        logger.info("Updated crew with id: {}", id);
    }

    public void deleteCrew(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = crewDAO.deleteCrew(id);

        if (deleted) {
            ctx.status(HttpStatus.NO_CONTENT);
            logger.info("Deleted crew with id: {}", id);
        } else {
            ctx.status(HttpStatus.NOT_FOUND).result("crew not found");
            logger.warn("Attempted to delete non-existing crew with id: {}", id);
        }
    }

    // -------------------
    // Room management
    // -------------------

    public void addPirate(Context ctx) {
        int crewId = Integer.parseInt(ctx.pathParam("id"));
        PirateDTO pirateDTO = ctx.bodyAsClass(PirateDTO.class);

        crewDAO.getCrewById(crewId).ifPresentOrElse(crew -> {
            Pirate pirate = new Pirate(pirateDTO, crew);
            Crew updatedCrew = crewDAO.addPirate(crew, pirate);
            ctx.status(HttpStatus.CREATED).json(new CrewDTO(updatedCrew));
            logger.info("Added pirate to crew with id: {}", crewId);
        }, () -> {
            ctx.status(HttpStatus.NOT_FOUND).result("Crew not found");
            logger.warn("Crew with id {} not found while adding pirate", crewId);
        });
    }

    public void getPiratesForCrew(Context ctx) {
        int crewId = Integer.parseInt(ctx.pathParam("crewId"));
        crewDAO.getCrewById(crewId).ifPresentOrElse(crew -> {
            List<PirateDTO> pirates = PirateDTO.toDTOList(crewDAO.getPiratesForCrew(crew));
            ctx.status(HttpStatus.OK).json(pirates);
            logger.info("Fetched {} pirates for crew with id: {}", pirates.size(), crewId);
        }, () -> {
            ctx.status(HttpStatus.NOT_FOUND).result("Crew not found");
            logger.warn("Crew with id {} not found while fetching pirates", crewId);
        });
    }
}
