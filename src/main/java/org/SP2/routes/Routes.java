package org.SP2.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.SP2.controllers.impl.CrewController;
import org.SP2.controllers.impl.PirateController;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private final CrewRoute crewRoute = new CrewRoute();
    private final PirateRoute pirateRoute = new PirateRoute();

    public EndpointGroup all() {
        return () -> {
            path("/", crewRoute.routes());
            path("/", pirateRoute.routes());
        };
    }
}