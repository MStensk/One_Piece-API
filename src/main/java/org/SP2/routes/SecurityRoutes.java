package org.SP2.routes;

import io.javalin.apibuilder.EndpointGroup;
import org.SP2.controllers.security.ISecurityController;
import org.SP2.controllers.security.SecurityController;
import org.SP2.enums.Role;

import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {
    ISecurityController securityController = new SecurityController();
    public EndpointGroup getSecurityRoute = () -> {
        path("/auth",()-> {
//          before(securityController::authenticate);
//          get("/", personEntityController.getAll(), Role.ANYONE);
//            get("/", personEntityController.getAll());
//            get("/resetdata", personEntityController.resetData());
//            get("/{id}", personEntityController.getById());

            post("/login", securityController.login(), Role.ANYONE);
            post("/register", securityController.register(), Role.ANYONE);
//            put("/{id}", personEntityController.update());
//            delete("/{id}", personEntityController.delete());
        });
    };
}
