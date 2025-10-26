package org.SP2.routes;


import io.javalin.apibuilder.EndpointGroup;
import org.SP2.controllers.impl.PirateController;
import org.SP2.enums.Role;

import static io.javalin.apibuilder.ApiBuilder.*;
public class PirateRoute {
    private final PirateController pirateController = new PirateController();

    public EndpointGroup routes() {
        return () -> path("pirates", () -> {
            // public GETs
            get("/",      pirateController::getAllPirates, Role.ANYONE);
            get("/{id}",  pirateController::getPirateById, Role.ANYONE);

            // protected writes
            post("/",     pirateController::createPirateStandalone, Role.USER);
            put("/{id}",  pirateController::updatePirate,           Role.USER);
            delete("/{id}", pirateController::deletePirate,         Role.USER);
        });
    }
}
