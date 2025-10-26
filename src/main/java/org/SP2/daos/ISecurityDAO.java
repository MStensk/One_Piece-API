package org.SP2.daos;

import jakarta.persistence.EntityNotFoundException;
import org.SP2.entities.security.Role;
import org.SP2.entities.security.User;
import org.SP2.exceptions.security.ValidationException;

public interface ISecurityDAO {
    User getVerifiedUser(String username, String password) throws ValidationException; // used for login
    User createUser(String username, String password); // used for register
    Role createRole(String role);
    User addUserRole(String username, String role) throws EntityNotFoundException;
}
