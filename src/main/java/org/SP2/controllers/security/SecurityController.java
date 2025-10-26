package org.SP2.controllers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.bugelhartmann.TokenVerificationException;
import dk.bugelhartmann.UserDTO;
import dk.bugelhartmann.TokenSecurity;
import io.javalin.http.*;
import org.SP2.config.HibernateConfig;
import org.SP2.daos.ISecurityDAO;
import org.SP2.daos.SecurityDAO;
import org.SP2.entities.security.User;
import org.SP2.exceptions.security.ApiException;
import org.SP2.exceptions.security.ValidationException;
import org.SP2.utils.Utils;

import java.text.ParseException;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {
    ISecurityDAO securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory("onepiece"));
    ObjectMapper objectMapper = new Utils().getObjectMapper();
    TokenSecurity tokenSecurity = new TokenSecurity();

    @Override
    public Handler login(){
        return (Context ctx) -> {
            User user = ctx.bodyAsClass(User.class);
            try {
                User verified = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                Set<String> stringRoles = verified.getRoles()
                        .stream()
                        .map(role->role.getRolename())
                        .collect(Collectors.toSet());
                UserDTO userDTO = new UserDTO(verified.getUsername(), stringRoles);
                String token = createToken(userDTO);

                ObjectNode on = objectMapper
                        .createObjectNode()
                        .put("token",token)
                        .put("username", userDTO.getUsername());
                ctx.json(on).status(200);

            } catch(ValidationException ex){
                ObjectNode on = objectMapper.createObjectNode().put("msg","login failed. Wrong username or password");
                ctx.json(on).status(401);
            }
        };
    }

    @Override
    public Handler register() {
        return ctx -> {
            try {
                ObjectNode json = objectMapper.readValue(ctx.body(), ObjectNode.class);
                String username = json.get("username").asText();
                String password = json.get("password").asText();

                // 1. Create user
                User created = securityDAO.createUser(username, password);

                // 2. Ensure "USER" role exists and assign it
                securityDAO.createRole("USER");
                securityDAO.addUserRole(username, "USER");

                // 3. Re-fetch user so we have roles loaded
                User updated = securityDAO.getVerifiedUser(username, password);

                System.out.println("Creating token for: " + updated.getUsername());
                System.out.println("Roles: " + updated.getRoles());

                // 4. Create DTO and token
                Set<String> stringRoles = updated.getRoles().stream()
                        .map(role -> role.getRolename())
                        .collect(Collectors.toSet());

                UserDTO userDTO = new UserDTO(updated.getUsername(), stringRoles);
                String token = createToken(userDTO);

                ObjectNode on = objectMapper.createObjectNode()
                        .put("token", token)
                        .put("username", userDTO.getUsername());

                ctx.status(201).json(on);

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(400).json(objectMapper.createObjectNode()
                        .put("msg", "Registration failed: " + e.getMessage()));
            }
        };
    }

    @Override
    public Handler authenticate() {
        return (Context ctx) -> {
            // This is a preflight request => no need for authentication
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }
            // If the endpoint is not protected with roles or is open to ANYONE role, then skip
            Set<String> allowedRoles = ctx.routeRoles().stream().map(role -> role.toString().toUpperCase()).collect(Collectors.toSet());
            if (isOpenEndpoint(allowedRoles))
                return;

            // If there is no token we do not allow entry
            UserDTO verifiedTokenUser = validateAndGetUserFromToken(ctx);
            ctx.attribute("user", verifiedTokenUser); // -> ctx.attribute("user") in ApplicationConfig beforeMatched filter
        };
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        // If the endpoint is not protected with any roles:
        if (allowedRoles.isEmpty())
            return true;

        // 1. Get permitted roles and Check if the endpoint is open to all with the ANYONE role
        if (allowedRoles.contains("ANYONE")) {
            return true;
        }
        return false;
    }

    @Override
    public Handler authorize() {
        return (Context ctx)-> {
            Set<String> allowedRoles = ctx.routeRoles()
                    .stream()
                    .map(role -> role.toString().toUpperCase())
                    .collect(Collectors.toSet());

            // 1. Check if the endpoint is open to all (either by not having any roles or having the ANYONE role set
            if (isOpenEndpoint(allowedRoles))
                return;
            // 2. Get user and ensure it is not null
            UserDTO user = ctx.attribute("user");
            if (user == null) {
                throw new ForbiddenResponse("No user was added from the token");
            }
            // 3. See if any role matches
            if (!userHasAllowedRole(user, allowedRoles))
                throw new ForbiddenResponse("User was not authorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        };
    }

    private static boolean userHasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        return user.getRoles().stream()
                .anyMatch(role -> allowedRoles.contains(role.toUpperCase()));
    }

    private String createToken(UserDTO user) {
        try {
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (System.getenv("DEPLOYED") != null) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            System.out.println("Loaded token config:");
            System.out.println("ISSUER = " + ISSUER);
            System.out.println("TOKEN_EXPIRE_TIME = " + TOKEN_EXPIRE_TIME);
            System.out.println("SECRET_KEY = " + SECRET_KEY);
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(500, "Could not create token");
        }
    }

    private static String getToken(Context ctx) {
        String header = ctx.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }

        // If the Authorization Header was malformed, then no entry
        String token = header.split(" ")[1];
        if (token == null) {
            throw new UnauthorizedResponse("Authorization header is malformed"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return token;
    }

    private UserDTO verifyToken(String token) {
        boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
        String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

        try {
            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new UnauthorizedResponse("Token not valid");
            }
        } catch (ParseException | TokenVerificationException e) {
//            logger.error("Could not create token", e);
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    private UserDTO validateAndGetUserFromToken(Context ctx) {
        String token = getToken(ctx);
        UserDTO verifiedTokenUser = verifyToken(token);
        if (verifiedTokenUser == null) {
            throw new UnauthorizedResponse("Invalid user or token"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return verifiedTokenUser;
    }
}
