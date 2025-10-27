package org.SP2.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.SP2.controllers.impl.PopulateController;
import org.SP2.enums.Role;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PopulateRoute {
    private final PopulateController populateController = new PopulateController();

    public EndpointGroup routes() {
        return () -> path("populate", () -> {
            post("/", populateController::populate, Role.ANYONE);
        });
    }
}

