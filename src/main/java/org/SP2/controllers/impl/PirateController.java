package org.SP2.controllers.impl;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManagerFactory;
import org.SP2.config.HibernateConfig;
import org.SP2.daos.CrewDAOImpl;
import org.SP2.daos.IDAO;
import org.SP2.daos.PirateDAO;
import org.SP2.dtos.PirateDTO;
import org.SP2.entities.Pirate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PirateController {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory("onepiece"); // or ("onepiece")
    private final IDAO crewDAO = CrewDAOImpl.getInstance(emf);
    private final PirateDAO pirateDAO = PirateDAO.getInstance(emf);

    private static final Logger logger = LoggerFactory.getLogger(PirateController.class);

    // --- existing nested create: POST /crew/{crewId}/pirates ---
    public void createPirate(Context ctx) {
        int crewId = Integer.parseInt(ctx.pathParam("crewId"));
        PirateDTO pirateDTO = ctx.bodyAsClass(PirateDTO.class);

        crewDAO.getCrewById(crewId).ifPresentOrElse(crew -> {
            Pirate pirate = pirateDTO.toEntity(crew);
            crewDAO.addPirate(crew, pirate); // maintains bidirectional link
            ctx.status(HttpStatus.CREATED).json(new PirateDTO(pirate));
            logger.info("Created pirate {} in crew {}", pirate.getPirateName(), crewId);
        }, () -> {
            ctx.status(HttpStatus.NOT_FOUND).result("Crew not found");
            logger.warn("Crew with id {} not found when creating pirate", crewId);
        });
    }

    // --- new: GET /pirates ---
    public void getAllPirates(Context ctx) {
        List<PirateDTO> list = PirateDTO.toDTOList(pirateDAO.getAllPirates());
        ctx.status(HttpStatus.OK).json(list);
        logger.info("Fetched all pirates, count: {}", list.size());
    }

    // --- new: GET /pirates/{id} ---
    public void getPirateById(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        pirateDAO.getPirateById(id).ifPresentOrElse(p -> {
            ctx.status(HttpStatus.OK).json(new PirateDTO(p));
        }, () -> {
            ctx.status(HttpStatus.NOT_FOUND).result("Pirate not found");
        });
    }

    // --- new: POST /pirates (expects crewId in body) ---
    public void createPirateStandalone(Context ctx) {
        PirateDTO dto = ctx.bodyAsClass(PirateDTO.class);
        if (dto.getCrewId() == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("crewId is required");
            return;
        }
        Pirate created = pirateDAO.createPirate(dto.getCrewId(), dto.toEntity(null)); // crew set in DAO
        ctx.status(HttpStatus.CREATED).json(new PirateDTO(created));
        logger.info("Created pirate {} under crew {}", created.getPirateName(), dto.getCrewId());
    }

    // --- new: PUT /pirates/{id} ---
    public void updatePirate(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        PirateDTO dto = ctx.bodyAsClass(PirateDTO.class);
        Pirate updated = pirateDAO.updatePirate(id, dto.toEntity(null));
        ctx.status(HttpStatus.OK).json(new PirateDTO(updated));
    }

    // --- new: DELETE /pirates/{id} ---
    public void deletePirate(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean ok = pirateDAO.deletePirate(id);
        if (ok) ctx.status(HttpStatus.NO_CONTENT);
        else ctx.status(HttpStatus.NOT_FOUND).result("Pirate not found");
    }
}

