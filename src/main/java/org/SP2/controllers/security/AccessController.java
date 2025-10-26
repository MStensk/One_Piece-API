package org.SP2.controllers.security;


import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import org.SP2.enums.Role;

import java.util.Set;

public class AccessController {
    private final ISecurityController securityController;

    public AccessController(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public Handler accessHandler() {
        return ctx -> {
            Set<RouteRole> allowedRoles = ctx.routeRoles();

            // If no roles OR ANYONE role => open endpoint
            if (allowedRoles.isEmpty() || allowedRoles.contains(Role.ANYONE)) {
                return;
            }

            // Otherwise require authentication and authorization
            try {
                // authenticate() will parse/verify the token and put UserDTO in ctx.attribute("user")
                securityController.authenticate().handle(ctx);
                // authorize() will check that the user has at least one of the route's roles
                securityController.authorize().handle(ctx);
            } catch (UnauthorizedResponse e) {
                throw e; // pass through 401
            } catch (Exception e) {
                throw new UnauthorizedResponse("You need to log in, or your token is invalid.");
            }
        };
    }
}

