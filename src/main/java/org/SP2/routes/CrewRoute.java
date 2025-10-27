package org.SP2.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.SP2.controllers.impl.CrewController;
import org.SP2.controllers.impl.PirateController;
import org.SP2.enums.Role;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CrewRoute {
    private final CrewController crewController = new CrewController();
    private final PirateController pirateController = new PirateController();


    // expose as a reusable EndpointGroup
    public EndpointGroup routes() {
        return () -> path("crew", () -> {
            // public GETs
            get("/",      crewController::getAllCrews,   Role.ANYONE);
            get("/{id}",  crewController::getCrewById,   Role.ANYONE);

            // protected writes
            post("/",     crewController::createCrew,    Role.USER);
            put("/{id}",  crewController::updateCrew,    Role.USER);
            delete("/{id}", crewController::deleteCrew,  Role.USER);

            // nested pirates under a crew
            path("/{crewId}/pirates", () -> {
                get("/",  crewController::getPiratesForCrew, Role.ANYONE);
                post("/", pirateController::createPirate,    Role.USER); // nested create
            });
        });
    }
}
