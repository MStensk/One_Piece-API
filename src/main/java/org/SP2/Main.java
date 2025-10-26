package org.SP2;

import org.SP2.config.ApplicationConfig;
import org.SP2.routes.Routes;
import org.SP2.routes.SecurityRoutes;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        ApplicationConfig
                .getInstance()
                .initiateServer()
                .setRoute(new Routes().all())
                .setRoute(new SecurityRoutes().getSecurityRoute)
                .startServer(7076);
    }
}